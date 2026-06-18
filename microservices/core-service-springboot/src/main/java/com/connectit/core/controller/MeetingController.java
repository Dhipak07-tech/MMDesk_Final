package com.connectit.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.connectit.core.util.DbUtil;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeetingController {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Helper to serialize objects to JSON string
    private String serializeJsonField(Object val) {
        if (val == null) return "{}";
        if (val instanceof String) return (String) val;
        try {
            return objectMapper.writeValueAsString(val);
        } catch (Exception e) {
            return "{}";
        }
    }

    // GET /api/meetings
    @GetMapping("/meetings")
    public ResponseEntity<?> getMeetings(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "search", required = false) String search) {
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM meetings WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (status != null && !status.isBlank() && !status.equalsIgnoreCase("All")) {
                sql.append(" AND status = ?");
                params.add(status);
            }
            if (platform != null && !platform.isBlank() && !platform.equalsIgnoreCase("All")) {
                sql.append(" AND platform = ?");
                params.add(platform);
            }
            if (date != null && !date.isBlank() && !date.equalsIgnoreCase("All")) {
                String dLower = date.toLowerCase();
                if (dLower.contains("today")) {
                    sql.append(" AND DATE(meeting_date) = CURDATE()");
                } else if (dLower.contains("yesterday")) {
                    sql.append(" AND DATE(meeting_date) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)");
                } else if (dLower.contains("week") || dLower.contains("7")) {
                    sql.append(" AND meeting_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
                } else if (dLower.contains("month") || dLower.contains("30")) {
                    sql.append(" AND meeting_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)");
                } else {
                    sql.append(" AND DATE(meeting_date) = ?");
                    params.add(date);
                }
            }
            if (search != null && !search.isBlank()) {
                sql.append(" AND (title LIKE ? OR conducted_by LIKE ? OR attendees LIKE ? OR short_description LIKE ?)");
                String sParam = "%" + search + "%";
                params.add(sParam);
                params.add(sParam);
                params.add(sParam);
                params.add(sParam);
            }

            sql.append(" ORDER BY meeting_date DESC");

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            
            // Map JSON fields
            for (Map<String, Object> r : rows) {
                r.put("id", String.valueOf(r.get("id")));
            }
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/meetings/{id}
    @GetMapping("/meetings/{id}")
    public ResponseEntity<?> getMeeting(@PathVariable int id) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM meetings WHERE id = ?", id);
            if (rows.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Meeting not found"));
            }
            Map<String, Object> meeting = new HashMap<>(rows.get(0));
            meeting.put("id", String.valueOf(meeting.get("id")));

            // Fetch audit logs and versions for details view
            List<Map<String, Object>> logs = jdbcTemplate.queryForList(
                "SELECT * FROM meeting_audit_logs WHERE meeting_id = ? ORDER BY created_at DESC", 
                meeting.get("meeting_id")
            );
            List<Map<String, Object>> versions = jdbcTemplate.queryForList(
                "SELECT * FROM meeting_versions WHERE meeting_db_id = ? ORDER BY version DESC", 
                id
            );

            meeting.put("auditLogs", logs);
            meeting.put("versions", versions);

            return ResponseEntity.ok(meeting);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/meetings
    @PostMapping("/meetings")
    @Transactional
    public ResponseEntity<?> createMeeting(@RequestBody Map<String, Object> body) {
        try {
            String meetingId = "mtg_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            String title = (String) body.get("title");
            String meetingDateStr = (String) body.get("meetingDate");
            String platform = (String) body.get("platform");
            String conductedBy = (String) body.get("conductedBy");
            String attendees = (String) body.get("attendees");
            String absentees = (String) body.get("absentees");
            String oneLineSummary = (String) body.get("oneLineSummary");
            String shortDescription = (String) body.get("shortDescription");
            String detailedDescription = (String) body.get("detailedDescription");
            String discussionPoints = (String) body.get("discussionPoints");
            String decisionsTaken = (String) body.get("decisionsTaken");
            String actionItems = (String) body.get("actionItems");
            String responsiblePerson = (String) body.get("responsiblePerson");
            String targetDate = (String) body.get("targetDate");
            String nextSteps = (String) body.get("nextSteps");
            String remarks = (String) body.get("remarks");
            String filePath = (String) body.get("filePath");
            String fileName = (String) body.get("fileName");
            Integer fileSize = (Integer) body.getOrDefault("fileSize", 0);
            String status = (String) body.getOrDefault("status", "Draft");
            String createdBy = (String) body.get("createdBy");
            String createdByName = (String) body.get("createdByName");
            String creationMethod = (String) body.getOrDefault("creationMethod", "template");

            if (createdBy == null) createdBy = "System";
            if (createdByName == null) createdByName = "System";

            String virtualData = serializeJsonField(body.get("virtual_data_json"));
            String attendanceData = serializeJsonField(body.get("attendance_data_json"));
            String notifications = serializeJsonField(body.get("notifications_json"));
            String recording = serializeJsonField(body.get("recording_json"));
            String timeline = serializeJsonField(body.get("timeline_json"));

            KeyHolder keyHolder = new GeneratedKeyHolder();
            String insertSql = "INSERT INTO meetings (" +
                "meeting_id, title, meeting_date, platform, conducted_by, attendees, absentees, " +
                "one_line_summary, short_description, detailed_description, discussion_points, " +
                "decisions_taken, action_items, responsible_person, target_date, next_steps, remarks, " +
                "file_path, file_name, file_size, status, created_by, created_by_name, creation_method, " +
                "virtual_data_json, attendance_data_json, notifications_json, recording_json, timeline_json, created_at, updated_at" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            final String finalCreatedBy = createdBy;
            final String finalCreatedByName = createdByName;

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, meetingId);
                ps.setString(2, title);
                ps.setString(3, parseMeetingDate(meetingDateStr));
                ps.setString(4, platform);
                ps.setString(5, conductedBy);
                ps.setString(6, attendees);
                ps.setString(7, absentees);
                ps.setString(8, oneLineSummary);
                ps.setString(9, shortDescription);
                ps.setString(10, detailedDescription);
                ps.setString(11, discussionPoints);
                ps.setString(12, decisionsTaken);
                ps.setString(13, actionItems);
                ps.setString(14, responsiblePerson);
                ps.setString(15, targetDate);
                ps.setString(16, nextSteps);
                ps.setString(17, remarks);
                ps.setString(18, filePath);
                ps.setString(19, fileName);
                ps.setInt(20, fileSize != null ? fileSize : 0);
                ps.setString(21, status);
                ps.setString(22, finalCreatedBy);
                ps.setString(23, finalCreatedByName);
                ps.setString(24, creationMethod);
                ps.setString(25, virtualData);
                ps.setString(26, attendanceData);
                ps.setString(27, notifications);
                ps.setString(28, recording);
                ps.setString(29, timeline);
                return ps;
            }, keyHolder);

            int newMeetingId = (int) DbUtil.getGeneratedId(keyHolder);

            // Log Audit trail
            jdbcTemplate.update(
                "INSERT INTO meeting_audit_logs (meeting_id, action, performed_by, performed_by_name, details) VALUES (?, 'Create', ?, ?, ?)",
                meetingId, finalCreatedBy, finalCreatedByName, "MOM created and set to " + status
            );

            // Save Version 1
            saveVersion(newMeetingId, meetingId, 1, title, meetingDateStr, status, filePath, fileName, body, finalCreatedBy, finalCreatedByName, "Initial creation");

            return ResponseEntity.ok(Map.of("id", newMeetingId, "meeting_id", meetingId, "success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/meetings/{id}
    @PutMapping("/meetings/{id}")
    @Transactional
    public ResponseEntity<?> updateMeeting(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            List<Map<String, Object>> existing = jdbcTemplate.queryForList("SELECT * FROM meetings WHERE id = ?", id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Meeting not found"));
            }
            Map<String, Object> current = existing.get(0);
            String meetingId = (String) current.get("meeting_id");

            String title = body.containsKey("title") ? (String) body.get("title") : (String) current.get("title");
            String meetingDateStr = body.containsKey("meetingDate") ? (String) body.get("meetingDate") : (current.get("meeting_date") != null ? String.valueOf(current.get("meeting_date")) : null);
            String platform = body.containsKey("platform") ? (String) body.get("platform") : (String) current.get("platform");
            String conductedBy = body.containsKey("conductedBy") ? (String) body.get("conductedBy") : (String) current.get("conducted_by");
            String attendees = body.containsKey("attendees") ? (String) body.get("attendees") : (String) current.get("attendees");
            String absentees = body.containsKey("absentees") ? (String) body.get("absentees") : (String) current.get("absentees");
            String oneLineSummary = body.containsKey("oneLineSummary") ? (String) body.get("oneLineSummary") : (String) current.get("one_line_summary");
            String shortDescription = body.containsKey("shortDescription") ? (String) body.get("shortDescription") : (String) current.get("short_description");
            String detailedDescription = body.containsKey("detailedDescription") ? (String) body.get("detailedDescription") : (String) current.get("detailed_description");
            String discussionPoints = body.containsKey("discussionPoints") ? (String) body.get("discussionPoints") : (String) current.get("discussion_points");
            String decisionsTaken = body.containsKey("decisionsTaken") ? (String) body.get("decisions_taken") : (String) current.get("decisions_taken");
            String actionItems = body.containsKey("actionItems") ? (String) body.get("action_items") : (String) current.get("action_items");
            String responsiblePerson = body.containsKey("responsiblePerson") ? (String) body.get("responsiblePerson") : (String) current.get("responsible_person");
            String targetDate = body.containsKey("targetDate") ? (String) body.get("targetDate") : (String) current.get("target_date");
            String nextSteps = body.containsKey("nextSteps") ? (String) body.get("nextSteps") : (String) current.get("next_steps");
            String remarks = body.containsKey("remarks") ? (String) body.get("remarks") : (String) current.get("remarks");
            String filePath = body.containsKey("filePath") ? (String) body.get("filePath") : (String) current.get("file_path");
            String fileName = body.containsKey("fileName") ? (String) body.get("fileName") : (String) current.get("file_name");
            Integer fileSize = body.containsKey("fileSize") ? (Integer) body.get("fileSize") : (Integer) current.get("file_size");
            String status = body.containsKey("status") ? (String) body.get("status") : (String) current.get("status");

            String updatedBy = body.containsKey("updatedBy") ? (String) body.get("updatedBy") : (String) body.get("createdBy");
            String updatedByName = body.containsKey("updatedByName") ? (String) body.get("updatedByName") : (String) body.get("createdByName");
            String changeSummary = (String) body.getOrDefault("changeSummary", "MOM updated");
            boolean isAutoSave = body.containsKey("isAutoSave") && (Boolean) body.get("isAutoSave");

            if (updatedBy == null) updatedBy = "System";
            if (updatedByName == null) updatedByName = "System";

            String virtualData = body.containsKey("virtual_data_json") ? serializeJsonField(body.get("virtual_data_json")) : (String) current.get("virtual_data_json");
            String attendanceData = body.containsKey("attendance_data_json") ? serializeJsonField(body.get("attendance_data_json")) : (String) current.get("attendance_data_json");
            String notifications = body.containsKey("notifications_json") ? serializeJsonField(body.get("notifications_json")) : (String) current.get("notifications_json");
            String recording = body.containsKey("recording_json") ? serializeJsonField(body.get("recording_json")) : (String) current.get("recording_json");
            String timeline = body.containsKey("timeline_json") ? serializeJsonField(body.get("timeline_json")) : (String) current.get("timeline_json");

            int nextVer = 1;
            if (!isAutoSave) {
                List<Map<String, Object>> versions = jdbcTemplate.queryForList("SELECT COUNT(*) as cnt FROM meeting_versions WHERE meeting_db_id = ?", id);
                if (!versions.isEmpty()) {
                    nextVer = ((Number) versions.get(0).get("cnt")).intValue() + 1;
                }
            } else {
                nextVer = current.get("version") != null ? ((Number) current.get("version")).intValue() : 1;
            }

            jdbcTemplate.update(
                "UPDATE meetings SET title=?, meeting_date=?, platform=?, conducted_by=?, attendees=?, absentees=?, " +
                "one_line_summary=?, short_description=?, detailed_description=?, discussion_points=?, decisions_taken=?, " +
                "action_items=?, responsible_person=?, target_date=?, next_steps=?, remarks=?, file_path=?, file_name=?, " +
                "file_size=?, status=?, version=?, virtual_data_json=?, attendance_data_json=?, notifications_json=?, recording_json=?, timeline_json=?, updated_at=NOW() " +
                "WHERE id=?",
                title, meetingDateStr != null ? parseMeetingDate(meetingDateStr) : null, platform, conductedBy, attendees, absentees,
                oneLineSummary, shortDescription, detailedDescription, discussionPoints, decisionsTaken,
                actionItems, responsiblePerson, targetDate, nextSteps, remarks, filePath, fileName,
                fileSize, status, nextVer, virtualData, attendanceData, notifications, recording, timeline, id
            );

            // Log Audit & Version if not autosave
            if (!isAutoSave) {
                jdbcTemplate.update(
                    "INSERT INTO meeting_audit_logs (meeting_id, action, performed_by, performed_by_name, details) VALUES (?, 'Update', ?, ?, ?)",
                    meetingId, updatedBy, updatedByName, changeSummary
                );

                saveVersion(id, meetingId, nextVer, title, meetingDateStr, status, filePath, fileName, body, updatedBy, updatedByName, changeSummary);
            }

            return ResponseEntity.ok(Map.of("id", id, "meeting_id", meetingId, "success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/meetings/{id}
    @DeleteMapping("/meetings/{id}")
    @Transactional
    public ResponseEntity<?> deleteMeeting(@PathVariable int id) {
        try {
            List<Map<String, Object>> existing = jdbcTemplate.queryForList("SELECT meeting_id FROM meetings WHERE id = ?", id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Meeting not found"));
            }
            String meetingId = (String) existing.get(0).get("meeting_id");

            jdbcTemplate.update("DELETE FROM meetings WHERE id = ?", id);
            jdbcTemplate.update("DELETE FROM meeting_versions WHERE meeting_db_id = ?", id);
            jdbcTemplate.update("DELETE FROM meeting_audit_logs WHERE meeting_id = ?", meetingId);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/moms/upload
    @PostMapping("/moms/upload")
    public ResponseEntity<?> uploadMomFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
            }
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isBlank()) {
                filename = "mom_" + System.currentTimeMillis() + ".pdf";
            }
            
            File uploadDir = new File("./public/uploads").getAbsoluteFile();
            if (!uploadDir.exists()) uploadDir.mkdirs();
            File destination = new File(uploadDir, filename);
            
            java.nio.file.Files.copy(
                file.getInputStream(), 
                destination.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            Map<String, Object> res = new HashMap<>();
            res.put("file_path", "/uploads/" + filename);
            res.put("file_name", filename);
            res.put("file_size", file.getSize());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private void saveVersion(int meetingDbId, String meetingId, int version, String title, String dateStr, String status, String filePath, String fileName, Map<String, Object> body, String updatedBy, String updatedByName, String changeSummary) {
        try {
            Map<String, Object> templateData = new HashMap<>(body);
            // Remove meta parameters from template_data
            templateData.remove("title");
            templateData.remove("meetingDate");
            templateData.remove("status");
            templateData.remove("filePath");
            templateData.remove("fileName");
            templateData.remove("fileSize");
            templateData.remove("updatedBy");
            templateData.remove("updatedByName");
            templateData.remove("changeSummary");

            String tDataJson = serializeJsonField(templateData);

            jdbcTemplate.update(
                "INSERT INTO meeting_versions (meeting_db_id, meeting_id, version, title, meeting_date, status, file_path, file_name, template_data, updated_by, updated_by_name, change_summary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                meetingDbId, meetingId, version, title, dateStr != null ? parseMeetingDate(dateStr) : null, status, filePath, fileName, tDataJson, updatedBy, updatedByName, changeSummary
            );
        } catch (Exception e) {
            System.err.println("Failed to save meeting version: " + e.getMessage());
        }
    }

    private String parseMeetingDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        String cleaned = dateStr.replace("T", " ").trim();
        
        // 1. yyyy-MM-dd HH:mm:ss or yyyy-MM-dd HH:mm
        java.util.regex.Pattern p1 = java.util.regex.Pattern.compile("^(\\d{4})[-/](\\d{2})[-/](\\d{2})\\s+(\\d{2}):(\\d{2})(?::(\\d{2}))?.*");
        java.util.regex.Matcher m1 = p1.matcher(cleaned);
        if (m1.matches()) {
            String year = m1.group(1);
            String month = m1.group(2);
            String day = m1.group(3);
            String hour = m1.group(4);
            String min = m1.group(5);
            String sec = m1.group(6) != null ? m1.group(6) : "00";
            return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
        }
        
        // 2. dd-MM-yyyy HH:mm:ss or dd-MM-yyyy HH:mm
        java.util.regex.Pattern p2 = java.util.regex.Pattern.compile("^(\\d{2})[-/](\\d{2})[-/](\\d{4})\\s+(\\d{2}):(\\d{2})(?::(\\d{2}))?.*");
        java.util.regex.Matcher m2 = p2.matcher(cleaned);
        if (m2.matches()) {
            String day = m2.group(1);
            String month = m2.group(2);
            String year = m2.group(3);
            String hour = m2.group(4);
            String min = m2.group(5);
            String sec = m2.group(6) != null ? m2.group(6) : "00";
            return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
        }

        // 3. yyyy-MM-dd
        java.util.regex.Pattern p3 = java.util.regex.Pattern.compile("^(\\d{4})[-/](\\d{2})[-/](\\d{2}).*");
        java.util.regex.Matcher m3 = p3.matcher(cleaned);
        if (m3.matches()) {
            return m3.group(1) + "-" + m3.group(2) + "-" + m3.group(3) + " 00:00:00";
        }

        // 4. dd-MM-yyyy
        java.util.regex.Pattern p4 = java.util.regex.Pattern.compile("^(\\d{2})[-/](\\d{2})[-/](\\d{4}).*");
        java.util.regex.Matcher m4 = p4.matcher(cleaned);
        if (m4.matches()) {
            return m4.group(3) + "-" + m4.group(2) + "-" + m4.group(1) + " 00:00:00";
        }
        
        return cleaned;
    }
}
