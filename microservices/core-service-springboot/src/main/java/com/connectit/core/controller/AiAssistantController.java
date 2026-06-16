package com.connectit.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AI Assistant Controller
 * -------------------------
 * Provides read-only aggregate statistics from the existing tickets table
 * for the frontend AI trend analysis feature.
 *
 * - No new database tables are created or modified.
 * - All queries are read-only SELECT aggregations.
 * - Completely isolated from all other controllers and business logic.
 */
@RestController
@RequestMapping("/api/ai/assistant")
@RequiredArgsConstructor
public class AiAssistantController {

    private final JdbcTemplate jdbc;

    /**
     * GET /api/ai/assistant/stats
     * Returns aggregated ticket trend data for AI trend analysis.
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. Top categories by ticket count
        try {
            List<Map<String, Object>> topCategories = jdbc.queryForList(
                "SELECT COALESCE(NULLIF(category,''), 'Uncategorized') AS category, " +
                "COUNT(*) AS count " +
                "FROM tickets " +
                "GROUP BY category " +
                "ORDER BY count DESC " +
                "LIMIT 10"
            );
            result.put("topCategories", topCategories);
        } catch (Exception e) {
            result.put("topCategories", List.of());
        }

        // 2. Top priorities by ticket count
        try {
            List<Map<String, Object>> topPriorities = jdbc.queryForList(
                "SELECT COALESCE(NULLIF(priority,''), 'Unset') AS priority, " +
                "COUNT(*) AS count " +
                "FROM tickets " +
                "GROUP BY priority " +
                "ORDER BY count DESC"
            );
            result.put("topPriorities", topPriorities);
        } catch (Exception e) {
            result.put("topPriorities", List.of());
        }

        // 3. Top assignment groups
        try {
            List<Map<String, Object>> topGroups = jdbc.queryForList(
                "SELECT COALESCE(NULLIF(assignment_group,''), 'Unassigned') AS assignment_group, " +
                "COUNT(*) AS count " +
                "FROM tickets " +
                "GROUP BY assignment_group " +
                "ORDER BY count DESC " +
                "LIMIT 10"
            );
            result.put("topAssignmentGroups", topGroups);
        } catch (Exception e) {
            result.put("topAssignmentGroups", List.of());
        }

        // 4. Status breakdown
        try {
            List<Map<String, Object>> statusBreakdown = jdbc.queryForList(
                "SELECT COALESCE(NULLIF(status,''), 'Unknown') AS status, " +
                "COUNT(*) AS count " +
                "FROM tickets " +
                "GROUP BY status " +
                "ORDER BY count DESC"
            );
            result.put("statusBreakdown", statusBreakdown);
        } catch (Exception e) {
            result.put("statusBreakdown", List.of());
        }

        // 5. Tickets created per day (last 30 days)
        try {
            List<Map<String, Object>> dailyTrend = jdbc.queryForList(
                "SELECT DATE(created_at) AS date, COUNT(*) AS count " +
                "FROM tickets " +
                "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
                "GROUP BY DATE(created_at) " +
                "ORDER BY date ASC"
            );
            result.put("dailyTrend", dailyTrend);
        } catch (Exception e) {
            result.put("dailyTrend", List.of());
        }

        // 6. Top recurring titles/issues (most repeated ticket titles, simplified)
        try {
            List<Map<String, Object>> recurringIssues = jdbc.queryForList(
                "SELECT LOWER(TRIM(title)) AS normalized_title, COUNT(*) AS count " +
                "FROM tickets " +
                "GROUP BY LOWER(TRIM(title)) " +
                "HAVING count > 1 " +
                "ORDER BY count DESC " +
                "LIMIT 10"
            );
            result.put("recurringIssues", recurringIssues);
        } catch (Exception e) {
            result.put("recurringIssues", List.of());
        }

        // 7. Total ticket counts
        try {
            Integer totalTickets = jdbc.queryForObject("SELECT COUNT(*) FROM tickets", Integer.class);
            Integer openTickets = jdbc.queryForObject(
                "SELECT COUNT(*) FROM tickets WHERE status NOT IN ('Resolved','Closed','Canceled')", Integer.class);
            Integer resolvedTickets = jdbc.queryForObject(
                "SELECT COUNT(*) FROM tickets WHERE status IN ('Resolved','Closed')", Integer.class);
            result.put("totalTickets", totalTickets != null ? totalTickets : 0);
            result.put("openTickets", openTickets != null ? openTickets : 0);
            result.put("resolvedTickets", resolvedTickets != null ? resolvedTickets : 0);
        } catch (Exception e) {
            result.put("totalTickets", 0);
            result.put("openTickets", 0);
            result.put("resolvedTickets", 0);
        }

        // 8. Average resolution time (hours) by priority
        try {
            List<Map<String, Object>> avgResolution = jdbc.queryForList(
                "SELECT priority, " +
                "ROUND(AVG(TIMESTAMPDIFF(HOUR, created_at, resolved_at)), 1) AS avg_hours " +
                "FROM tickets " +
                "WHERE resolved_at IS NOT NULL AND priority IS NOT NULL " +
                "GROUP BY priority " +
                "ORDER BY priority"
            );
            result.put("avgResolutionByPriority", avgResolution);
        } catch (Exception e) {
            result.put("avgResolutionByPriority", List.of());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/ai/assistant/health
     * Simple health check for the AI assistant endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "module", "ai-assistant", "version", "1.0.0"));
    }
}
