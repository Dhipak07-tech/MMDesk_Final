package com.connectit.core;

import com.connectit.core.util.RoleUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TimesheetVisibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    void testGetViewableRoles() {
        // Ultra Super Admin sees everything below
        List<String> ultraView = RoleUtil.getViewableRoles("ultra_super_admin");
        assertEquals(List.of("super_admin", "admin", "sub_admin", "agent", "user"), ultraView);

        // Super Admin
        List<String> superView = RoleUtil.getViewableRoles("super_admin");
        assertEquals(List.of("admin", "sub_admin", "agent", "user"), superView);

        // Administrator
        List<String> adminView = RoleUtil.getViewableRoles("admin");
        assertEquals(List.of("sub_admin", "agent", "user"), adminView);

        // Sub Admin
        List<String> subAdminView = RoleUtil.getViewableRoles("sub_admin");
        assertEquals(List.of("agent", "user"), subAdminView);

        // Support Agent
        List<String> agentView = RoleUtil.getViewableRoles("agent");
        assertEquals(List.of("user"), agentView);

        // User
        List<String> userView = RoleUtil.getViewableRoles("user");
        assertTrue(userView.isEmpty());

        // Case insensitivity
        List<String> adminViewUpper = RoleUtil.getViewableRoles("ADMIN");
        assertEquals(List.of("sub_admin", "agent", "user"), adminViewUpper);

        // Invalid role
        List<String> invalidView = RoleUtil.getViewableRoles("non_existent_role");
        assertTrue(invalidView.isEmpty());
    }

    @Test
    @WithMockUser(username = "agent_test_uid", roles = "AGENT")
    void testDirectApiAccessRestrictions() throws Exception {
        // Ensure test users are in the database with their correct roles
        jdbcTemplate.update("INSERT INTO users (uid, email, name, role, is_active) VALUES ('agent_test_uid', 'agent@test.com', 'Agent', 'agent', 1) " +
                            "ON DUPLICATE KEY UPDATE role='agent'");
        jdbcTemplate.update("INSERT INTO users (uid, email, name, role, is_active) VALUES ('user_test_uid', 'user@test.com', 'User', 'user', 1) " +
                            "ON DUPLICATE KEY UPDATE role='user'");
        jdbcTemplate.update("INSERT INTO users (uid, email, name, role, is_active) VALUES ('admin_test_uid', 'admin@test.com', 'Admin', 'admin', 1) " +
                            "ON DUPLICATE KEY UPDATE role='admin'");

        // 1. Agent should be able to view User's timesheets (200 OK)
        mockMvc.perform(get("/api/timesheets").param("user_id", "user_test_uid"))
               .andExpect(status().isOk());

        // 2. Agent should NOT be able to view Admin's timesheets (403 Forbidden)
        mockMvc.perform(get("/api/timesheets").param("user_id", "admin_test_uid"))
               .andExpect(status().isForbidden());

        // 3. Agent should NOT be able to view another Agent's timesheets (403 Forbidden)
        jdbcTemplate.update("INSERT INTO users (uid, email, name, role, is_active) VALUES ('agent2_test_uid', 'agent2@test.com', 'Agent 2', 'agent', 1) " +
                            "ON DUPLICATE KEY UPDATE role='agent'");
        mockMvc.perform(get("/api/timesheets").param("user_id", "agent2_test_uid"))
               .andExpect(status().isForbidden());

        // 4. Agent should be able to view their own timesheets (200 OK)
        mockMvc.perform(get("/api/timesheets").param("user_id", "agent_test_uid"))
               .andExpect(status().isOk());
    }
}
