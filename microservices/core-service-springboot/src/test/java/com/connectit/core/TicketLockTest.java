package com.connectit.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TicketLockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser(username = "agent_test_uid", roles = "AGENT")
    void testLockedTicketRestrictions() throws Exception {
        // Ensure test users are in the database
        jdbcTemplate.update("INSERT INTO users (uid, email, name, role, is_active) VALUES ('agent_test_uid', 'agent@test.com', 'Agent', 'agent', 1) " +
                            "ON DUPLICATE KEY UPDATE role='agent'");
        jdbcTemplate.update("INSERT INTO users (uid, email, name, role, is_active) VALUES ('admin_test_uid', 'admin@test.com', 'Admin', 'admin', 1) " +
                            "ON DUPLICATE KEY UPDATE role='admin'");

        // Insert a Resolved ticket
        jdbcTemplate.update("INSERT INTO tickets (id, ticket_number, status, title, description, assigned_to) " +
                            "VALUES (99999, 'INC99999', 'Resolved', 'Test Lock', 'Should be locked', 'agent_test_uid') " +
                            "ON DUPLICATE KEY UPDATE status='Resolved', assigned_to='agent_test_uid'");

        // 1. Try to add activity to Resolved ticket -> should fail with 400
        mockMvc.perform(post("/api/tickets/99999/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"Test comment\"}"))
               .andExpect(status().isBadRequest());

        // 2. Try to update fields of Resolved ticket -> should fail with 400
        mockMvc.perform(put("/api/tickets/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated title\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin_test_uid", roles = "ADMIN")
    void testAdminReopenLockedTicket() throws Exception {
        // Ensure test users are in the database
        jdbcTemplate.update("INSERT INTO users (uid, email, name, role, is_active) VALUES ('admin_test_uid', 'admin@test.com', 'Admin', 'admin', 1) " +
                            "ON DUPLICATE KEY UPDATE role='admin'");

        // Insert a Closed ticket
        jdbcTemplate.update("INSERT INTO tickets (id, ticket_number, status, title, description) " +
                            "VALUES (99998, 'INC99998', 'Closed', 'Test Reopen', 'Should be reopened by admin') " +
                            "ON DUPLICATE KEY UPDATE status='Closed'");

        // Admin reopens the ticket -> should succeed (200 OK)
        mockMvc.perform(put("/api/tickets/99998")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"In Progress\"}"))
               .andExpect(status().isOk());
    }
}
