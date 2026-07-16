package com.connectit.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('USER', 'AGENT', 'SUB_ADMIN', 'ADMIN', 'SUPER_ADMIN', 'ULTRA_SUPER_ADMIN')")
public class DelayReportController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/delay-reports")
    public ResponseEntity<?> getDelayReports(
            @RequestParam(required = false) String employee_uid,
            @RequestParam(required = false) String manager_uid,
            @RequestParam(required = false) String user_uid,
            @RequestParam(required = false) String status) {

        String sql = "SELECT * FROM delay_reports WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (user_uid != null) {
            sql += " AND (employee_uid = ? OR manager_uid = ?)";
            params.add(user_uid);
            params.add(user_uid);
        } else {
            if (employee_uid != null) {
                sql += " AND employee_uid = ?";
                params.add(employee_uid);
            }
            if (manager_uid != null) {
                sql += " AND manager_uid = ?";
                params.add(manager_uid);
            }
        }
        if (status != null) {
            sql += " AND report_status = ?";
            params.add(status);
        }

        sql += " ORDER BY created_at DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());
        return ResponseEntity.ok(stringifyIds(rows));
    }

    @GetMapping("/delay-reports/{id}")
    public ResponseEntity<?> getDelayReportById(@PathVariable Long id) {
        String sql = "SELECT * FROM delay_reports WHERE id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);

        if (rows.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Delay report not found"));
        }

        return ResponseEntity.ok(stringifyId(new HashMap<>(rows.get(0))));
    }

    @PostMapping("/delay-reports")
    @Transactional
    public ResponseEntity<?> createDelayReport(@RequestBody Map<String, Object> body) {
        try {
            String taskIncidentId = (String) body.get("task_incident_id");
            String assignedTime = (String) body.get("assigned_time");
            String expectedCompletionTime = (String) body.get("expected_completion_time");
            String actualCompletionTime = (String) body.get("actual_completion_time");
            String totalDelayDuration = (String) body.get("total_delay_duration");
            String reasonForDelay = (String) body.get("reason_for_delay");
            String activitiesPerformed = (String) body.get("activities_performed_during_delay");
            
            String meetingDetails = (String) body.get("meeting_details");
            String technicalSystemIssues = (String) body.get("technical_system_issues");
            String additionalComments = (String) body.get("additional_comments");
            String reportStatus = body.get("report_status") != null ? (String) body.get("report_status") : "Draft";
            
            String employeeUid = (String) body.get("employee_uid");
            String employeeName = (String) body.get("employee_name");
            String managerUid = (String) body.get("manager_uid");
            String managerName = (String) body.get("manager_name");

            String attachmentUrl = (String) body.get("attachment_url");
            String attachmentName = (String) body.get("attachment_name");

            if (taskIncidentId == null || assignedTime == null || expectedCompletionTime == null || actualCompletionTime == null || reasonForDelay == null || activitiesPerformed == null || employeeUid == null || managerUid == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Required fields are missing"));
            }

            String sql = "INSERT INTO delay_reports (task_incident_id, assigned_time, expected_completion_time, actual_completion_time, total_delay_duration, reason_for_delay, activities_performed_during_delay, meeting_details, technical_system_issues, dependency_approval_wait_time, additional_comments, report_status, employee_uid, employee_name, manager_uid, manager_name, attachment_url, attachment_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, taskIncidentId);
                ps.setTimestamp(2, Timestamp.valueOf(formatDateTimeToSql(assignedTime)));
                ps.setTimestamp(3, Timestamp.valueOf(formatDateTimeToSql(expectedCompletionTime)));
                ps.setTimestamp(4, Timestamp.valueOf(formatDateTimeToSql(actualCompletionTime)));
                ps.setString(5, totalDelayDuration);
                ps.setString(6, reasonForDelay);
                ps.setString(7, activitiesPerformed);
                ps.setString(8, meetingDetails);
                ps.setString(9, technicalSystemIssues);
                if (body.get("dependency_approval_wait_time") != null && !body.get("dependency_approval_wait_time").toString().trim().isEmpty()) {
                    ps.setDouble(10, Double.valueOf(body.get("dependency_approval_wait_time").toString()));
                } else {
                    ps.setNull(10, java.sql.Types.DECIMAL);
                }
                ps.setString(11, additionalComments);
                ps.setString(12, reportStatus);
                ps.setString(13, employeeUid);
                ps.setString(14, employeeName);
                ps.setString(15, managerUid);
                ps.setString(16, managerName);
                ps.setString(17, attachmentUrl);
                ps.setString(18, attachmentName);
                return ps;
            }, keyHolder);

            long newId = keyHolder.getKey().longValue();
            return ResponseEntity.ok(Map.of("id", String.valueOf(newId), "message", "Delay report created successfully"));

        } catch (Exception e) {
            log.error("Failed to create delay report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/delay-reports/{id}")
    @Transactional
    public ResponseEntity<?> updateDelayReport(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            // Check if report exists
            String selectSql = "SELECT * FROM delay_reports WHERE id = ?";
            List<Map<String, Object>> existing = jdbcTemplate.queryForList(selectSql, id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Delay report not found"));
            }

            // Update status or other fields
            String sql = "UPDATE delay_reports SET updated_at = NOW()";
            List<Object> params = new ArrayList<>();

            if (body.containsKey("task_incident_id")) {
                sql += ", task_incident_id = ?";
                params.add(body.get("task_incident_id"));
            }
            if (body.containsKey("assigned_time") && body.get("assigned_time") != null) {
                sql += ", assigned_time = ?";
                params.add(Timestamp.valueOf(formatDateTimeToSql(body.get("assigned_time"))));
            }
            if (body.containsKey("expected_completion_time") && body.get("expected_completion_time") != null) {
                sql += ", expected_completion_time = ?";
                params.add(Timestamp.valueOf(formatDateTimeToSql(body.get("expected_completion_time"))));
            }
            if (body.containsKey("actual_completion_time") && body.get("actual_completion_time") != null) {
                sql += ", actual_completion_time = ?";
                params.add(Timestamp.valueOf(formatDateTimeToSql(body.get("actual_completion_time"))));
            }
            if (body.containsKey("total_delay_duration")) {
                sql += ", total_delay_duration = ?";
                params.add(body.get("total_delay_duration"));
            }
            if (body.containsKey("reason_for_delay")) {
                sql += ", reason_for_delay = ?";
                params.add(body.get("reason_for_delay"));
            }
            if (body.containsKey("activities_performed_during_delay")) {
                sql += ", activities_performed_during_delay = ?";
                params.add(body.get("activities_performed_during_delay"));
            }
            if (body.containsKey("meeting_details")) {
                sql += ", meeting_details = ?";
                params.add(body.get("meeting_details"));
            }
            if (body.containsKey("technical_system_issues")) {
                sql += ", technical_system_issues = ?";
                params.add(body.get("technical_system_issues"));
            }
            if (body.containsKey("dependency_approval_wait_time")) {
                sql += ", dependency_approval_wait_time = ?";
                if (body.get("dependency_approval_wait_time") != null && !body.get("dependency_approval_wait_time").toString().trim().isEmpty()) {
                    params.add(Double.valueOf(body.get("dependency_approval_wait_time").toString()));
                } else {
                    params.add(null);
                }
            }
            if (body.containsKey("additional_comments")) {
                sql += ", additional_comments = ?";
                params.add(body.get("additional_comments"));
            }
            if (body.containsKey("report_status")) {
                sql += ", report_status = ?";
                params.add(body.get("report_status"));
            }
            if (body.containsKey("rejection_reason")) {
                sql += ", rejection_reason = ?";
                params.add(body.get("rejection_reason"));
            }
            if (body.containsKey("attachment_url")) {
                sql += ", attachment_url = ?";
                params.add(body.get("attachment_url"));
            }
            if (body.containsKey("attachment_name")) {
                sql += ", attachment_name = ?";
                params.add(body.get("attachment_name"));
            }

            sql += " WHERE id = ?";
            params.add(id);

            jdbcTemplate.update(sql, params.toArray());
            return ResponseEntity.ok(Map.of("message", "Delay report updated successfully"));

        } catch (Exception e) {
            log.error("Failed to update delay report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/delay-reports/upload")
    public ResponseEntity<?> uploadDelayReportFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = "delay_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            File uploadDir = new File("./public/uploads").getAbsoluteFile();
            if (!uploadDir.exists()) uploadDir.mkdirs();
            File destination = new File(uploadDir, filename);
            file.transferTo(destination);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "file_path", "/uploads/" + filename,
                "file_name", originalFilename
            ));
        } catch (Exception e) {
            log.error("Failed to upload delay file", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    private List<Map<String, Object>> stringifyIds(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            result.add(stringifyId(new HashMap<>(row)));
        }
        return result;
    }

    private Map<String, Object> stringifyId(Map<String, Object> row) {
        Map<String, Object> m = new HashMap<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            m.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        if (m.containsKey("id") && m.get("id") != null) {
            m.put("id", String.valueOf(m.get("id")));
        }
        return m;
    }

    private String formatDateTimeToSql(Object value) {
        if (value == null) {
            return null;
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        String replaced = str.replace('T', ' ');
        if (replaced.endsWith("Z")) {
            replaced = replaced.substring(0, replaced.length() - 1);
        }
        int dotIndex = replaced.indexOf('.');
        if (dotIndex != -1) {
            replaced = replaced.substring(0, dotIndex);
        }
        if (replaced.length() == 16) {
            replaced += ":00";
        }
        if (replaced.length() > 19) {
            replaced = replaced.substring(0, 19);
        }
        return replaced;
    }
}
