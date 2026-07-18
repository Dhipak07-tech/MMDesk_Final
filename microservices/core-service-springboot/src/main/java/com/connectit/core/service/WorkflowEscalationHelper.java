package com.connectit.core.service;

import com.connectit.core.model.Ticket;
import com.connectit.core.util.DbUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowEscalationHelper {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void handleTicketCompletion(Ticket t) {
        String assigneeUid = t.getAssignedTo();
        if (assigneeUid == null) {
            log.info("[WorkflowEscalation] Ticket resolved/closed but no assignee found. Skipping timesheet generation.");
            return;
        }

        log.info("[WorkflowEscalation] Ticket {} completed (Status: {}). Assignee: {}. Checking for group manager/leader...",
                t.getTicketNumber(), t.getStatus(), assigneeUid);

        // Find active group leaders/managers for this assignee
        List<Map<String, Object>> groups = jdbcTemplate.queryForList(
                "SELECT g.id, g.leader_uid, g.leader_name FROM settings_group_members m " +
                "JOIN settings_groups g ON m.group_id = g.id " +
                "WHERE m.user_id = ? AND m.status = 'active'", assigneeUid
        );

        if (groups.isEmpty()) {
            log.info("[WorkflowEscalation] Assignee {} is not part of any active group with a leader. Skipping timesheet generation.", assigneeUid);
            return;
        }

        // Query time cards to find hours worked
        Double hours = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(hours_worked), 0) FROM time_cards WHERE task = ? AND user_id = ?",
                Double.class, t.getTicketNumber(), assigneeUid
        );
        if (hours == null || hours == 0) {
            hours = 2.0; // Default to 2 hours of compiled work
        }

        String todayStr = java.time.LocalDate.now().toString();
        String mondayStr = getWeekMonday(todayStr);
        String sundayStr = getWeekSunday(todayStr);

        for (Map<String, Object> group : groups) {
            String leaderUid = (String) group.get("leader_uid");
            if (leaderUid == null || leaderUid.isBlank()) {
                log.info("[WorkflowEscalation] Group {} does not have a leader assigned.", group.get("id"));
                continue;
            }

            log.info("[WorkflowEscalation] Populating pending timesheet entry for leader: {}", leaderUid);

            // Get or create Leader timesheet
            List<Map<String, Object>> timesheets = jdbcTemplate.queryForList(
                    "SELECT id FROM timesheets WHERE user_id = ? AND week_start = ?",
                    leaderUid, mondayStr
            );
            Long timesheetId;
            if (timesheets.isEmpty()) {
                KeyHolder kh = new GeneratedKeyHolder();
                jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO timesheets (user_id, week_start, week_end, status, total_hours) VALUES (?, ?, ?, 'Draft', 0.00)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, leaderUid);
                    ps.setString(2, mondayStr);
                    ps.setString(3, sundayStr);
                    return ps;
                }, kh);
                timesheetId = DbUtil.getGeneratedId(kh);
            } else {
                timesheetId = ((Number) timesheets.get(0).get("id")).longValue();
            }

            String assigneeName = t.getAssignedToName() != null ? t.getAssignedToName() : "Tarun";
            String shortDesc = "[Pending Approval - " + assigneeName + "] " + t.getTicketNumber() + ": " + t.getTitle();
            if (shortDesc.length() > 255) {
                shortDesc = shortDesc.substring(0, 255);
            }

            // Create time card in leader's timesheet
            jdbcTemplate.update(
                    "INSERT INTO time_cards (timesheet_id, user_id, entry_date, task, hours_worked, description, short_description, status, work_type, billable) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'Submitted', 'Ticket Resolution', 'Non-Billable')",
                    timesheetId, leaderUid, todayStr, t.getTicketNumber(), hours, t.getDescription(), shortDesc
            );

            // Trigger notification
            sendNotification(leaderUid, "Timesheet Awaiting Approval",
                    "A pending timesheet entry for " + assigneeName + "'s completion of ticket " + t.getTicketNumber() + " is awaiting your review.",
                    t.getTicketNumber());
        }
    }

    @Transactional
    public void handleTimesheetApproval(Long timesheetId) {
        log.info("[WorkflowEscalation] Timesheet {} approved. Checking for pending escalations...", timesheetId);

        List<Map<String, Object>> cards = jdbcTemplate.queryForList(
                "SELECT * FROM time_cards WHERE timesheet_id = ? AND short_description LIKE '[Pending Approval - %'",
                timesheetId
        );

        if (cards.isEmpty()) {
            log.info("[WorkflowEscalation] No escalated time cards found in timesheet {}.", timesheetId);
            return;
        }

        // Get active ultra super admins
        List<Map<String, Object>> admins = jdbcTemplate.queryForList(
                "SELECT uid FROM users WHERE role = 'ultra_super_admin' AND is_active = 1"
        );

        if (admins.isEmpty()) {
            log.info("[WorkflowEscalation] No active Ultra Super Admins found to escalate to.");
            return;
        }

        String todayStr = java.time.LocalDate.now().toString();
        String mondayStr = getWeekMonday(todayStr);
        String sundayStr = getWeekSunday(todayStr);

        for (Map<String, Object> card : cards) {
            String origShortDesc = (String) card.get("short_description");
            String task = (String) card.get("task");
            String entryDate = card.get("entry_date") != null ? String.valueOf(card.get("entry_date")) : todayStr;
            Double hours = ((Number) card.get("hours_worked")).doubleValue();
            String desc = (String) card.get("description");

            String newShortDesc = origShortDesc.replace("[Pending Approval -", "[Approved - Escalated");
            if (newShortDesc.length() > 255) {
                newShortDesc = newShortDesc.substring(0, 255);
            }

            for (Map<String, Object> admin : admins) {
                String adminUid = (String) admin.get("uid");
                if (adminUid == null) continue;

                log.info("[WorkflowEscalation] Escalating approved card {} to Admin: {}", task, adminUid);

                // Get or create Admin timesheet
                List<Map<String, Object>> timesheets = jdbcTemplate.queryForList(
                        "SELECT id FROM timesheets WHERE user_id = ? AND week_start = ?",
                        adminUid, mondayStr
                );
                Long adminTsId;
                if (timesheets.isEmpty()) {
                    KeyHolder kh = new GeneratedKeyHolder();
                    jdbcTemplate.update(con -> {
                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO timesheets (user_id, week_start, week_end, status, total_hours) VALUES (?, ?, ?, 'Approved', 0.00)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, adminUid);
                        ps.setString(2, mondayStr);
                        ps.setString(3, sundayStr);
                        return ps;
                    }, kh);
                    adminTsId = DbUtil.getGeneratedId(kh);
                } else {
                    adminTsId = ((Number) timesheets.get(0).get("id")).longValue();
                }

                // Insert into admin timesheet
                jdbcTemplate.update(
                        "INSERT INTO time_cards (timesheet_id, user_id, entry_date, task, hours_worked, description, short_description, status, work_type, billable) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 'Approved', 'Ticket Resolution', 'Non-Billable')",
                        adminTsId, adminUid, entryDate, task, hours, desc, newShortDesc
                );

                // Notify Admin
                sendNotification(adminUid, "Escalation Complete: Timesheet Approved",
                        "Timesheet entry for Tarun's completion of ticket " + task + " has been approved and escalated.",
                        task);
            }
        }
    }

    private void sendNotification(String userId, String title, String message, String ticketNumber) {
        try {
            KeyHolder kh = new GeneratedKeyHolder();
            String sql = "INSERT INTO notifications (user_id, title, message, ticket_number, is_read) VALUES (?, ?, ?, ?, 0)";
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, userId);
                ps.setString(2, title);
                ps.setString(3, message);
                ps.setString(4, ticketNumber);
                return ps;
            }, kh);

            long notifId = DbUtil.getGeneratedId(kh);

            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(notifId));
            map.put("user_id", userId);
            map.put("title", title);
            map.put("message", message);
            map.put("ticket_number", ticketNumber);
            map.put("is_read", 0);
            map.put("created_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            com.connectit.core.controller.NotificationController.sendNotification(userId, map);
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
        }
    }

    private String getWeekMonday(String dateStr) {
        java.time.LocalDate d = java.time.LocalDate.parse(dateStr);
        int day = d.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        java.time.LocalDate monday = d.minusDays(day - 1);
        return monday.toString();
    }

    private String getWeekSunday(String dateStr) {
        java.time.LocalDate d = java.time.LocalDate.parse(getWeekMonday(dateStr));
        return d.plusDays(6).toString();
    }
}
