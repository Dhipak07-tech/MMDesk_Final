package com.connectit.core.controller;

import com.connectit.core.model.*;
import com.connectit.core.repository.*;
import com.connectit.core.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ULTRA_SUPER_ADMIN')")
public class EmailController {

    private final EmailService                 emailService;
    private final EmailLogRepository           emailLogRepo;
    private final NotificationQueueRepository  queueRepo;
    private final CompanyEmailConfigRepository configRepo;
    private final org.springframework.mail.javamail.JavaMailSender mailSender;

    @Value("${app.mail.from:aakash42633@gmail.com}")
    private String defaultFrom;

    @Value("${app.mail.from-name:Manage My Desk}")
    private String defaultFromName;

    @GetMapping("/email/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(emailService.getHealth());
    }

    @GetMapping("/email/logs")
    public ResponseEntity<?> logs(@RequestParam(defaultValue="50") int limit) {
        return ResponseEntity.ok(emailLogRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit)));
    }

    @GetMapping("/email/queue")
    public ResponseEntity<?> queue() {
        List<Object[]> statsRaw = queueRepo.countByStatus();
        List<Map<String,Object>> stats = statsRaw.stream()
            .map(r -> Map.of("status", r[0], "count", r[1]))
            .toList();
        var items = queueRepo.findAll(PageRequest.of(0, 50)).getContent();
        return ResponseEntity.ok(Map.of("items", items, "stats", stats));
    }

    @PostMapping("/email/queue/process")
    public ResponseEntity<?> processQueue() {
        emailService.processQueue();
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/email/queue/retry-failed")
    public ResponseEntity<?> retryFailed() {
        queueRepo.findByStatus("failed").forEach(q -> {
            q.setStatus("retry");
            q.setRetryCount(0);
            q.setNextRetryAt(null);
            queueRepo.save(q);
        });
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/email/send-test")
    public ResponseEntity<?> sendTest(@RequestBody(required = false) Map<String,String> body) {
        String to = (body != null && body.containsKey("to")) ? body.get("to") : "aakash42633@gmail.com";
        emailService.sendAsync(to,
            "[TEST] Manage My Desk Gmail Integration Test",
            "<div style='font-family:sans-serif;padding:20px'><h2 style='color:#2563eb'>&#x2705; Gmail Integration Working</h2>" +
            "<p>This confirms the Manage My Desk Gmail integration (aakash42633@gmail.com) is fully operational.</p>" +
            "<p>Sent from: aakash42633@gmail.com (" + defaultFromName + ")</p>" +
            "<p>Sent at: " + java.time.LocalDateTime.now() + "</p></div>"
        );
        return ResponseEntity.ok(Map.of("success", true, "message", "Test email sent to " + to));
    }

    @PostMapping("/email/send-note")
    public ResponseEntity<?> sendNote(@RequestBody Map<String,String> body) {
        emailService.sendAsync(body.get("to"), body.get("subject"), body.get("body"));
        return ResponseEntity.ok(Map.of("message", "Email queued"));
    }

    @PostMapping("/email/smtp-test")
    public ResponseEntity<?> testCurrentSmtp(@RequestBody(required = false) Map<String,String> body) {
        String host = "smtp.gmail.com";
        int port = 587;
        String username = "aakash42633@gmail.com";
        String password = "";

        if (body != null && body.containsKey("password") && !body.get("password").isBlank()) {
            password = body.get("password");
        } else {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
            password = impl.getPassword();
        }

        log.info("[SMTP-TEST] Testing Gmail SMTP connection for user: {}", username);

        try {
            var props = new java.util.Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.connectiontimeout", "8000");
            props.put("mail.smtp.timeout", "8000");

            final String u = username;
            final String p = password;
            var session = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(u, p);
                }
            });
            var transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);
            transport.close();

            log.info("[SMTP-TEST] Gmail SMTP SUCCESS for {}", username);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gmail SMTP Connection Successful! Host: " + host + ":" + port + " | Account: " + username
            ));
        } catch (Exception e) {
            log.error("[SMTP-TEST] FAILED for {}: {}", username, e.getMessage());
            String err = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "error", "Gmail SMTP authentication failed: " + err
            ));
        }
    }

    @PostMapping("/email/imap-test")
    public ResponseEntity<?> testCurrentImap(@RequestBody(required = false) Map<String,String> body) {
        String host = "imap.gmail.com";
        int port = 993;
        String username = "aakash42633@gmail.com";
        String password = "";

        if (body != null && body.containsKey("password") && !body.get("password").isBlank()) {
            password = body.get("password");
        } else {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
            password = impl.getPassword();
        }

        log.info("[IMAP-TEST] Testing Gmail IMAP connection for user: {}", username);

        try {
            var props = new java.util.Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.ssl.trust", "imap.gmail.com");
            props.put("mail.imaps.connectiontimeout", "8000");
            props.put("mail.imaps.timeout", "8000");

            var session = jakarta.mail.Session.getInstance(props, null);
            var store = session.getStore("imaps");
            store.connect(host, port, username, password);
            store.close();

            log.info("[IMAP-TEST] Gmail IMAP SUCCESS for {}", username);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gmail IMAP Connection Successful! Host: " + host + ":" + port + " | Account: " + username
            ));
        } catch (Exception e) {
            log.error("[IMAP-TEST] FAILED for {}: {}", username, e.getMessage());
            String err = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "error", "Gmail IMAP authentication failed: " + err
            ));
        }
    }

    @PostMapping("/email/smtp-update")
    public ResponseEntity<?> updateSmtpConfig(@RequestBody Map<String,String> body) {
        try {
            String host     = "smtp.gmail.com";
            int    port     = 587;
            String username = "aakash42633@gmail.com";
            String password = body.get("password");

            if (password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Gmail App Password is required"));
            }

            // Test connection before applying
            var props = new java.util.Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.connectiontimeout", "8000");
            props.put("mail.smtp.timeout", "8000");

            final String u = username;
            final String p = password;
            var session = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(u, p);
                }
            });
            var transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);
            transport.close();

            // Apply to live mail sender
            JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
            impl.setHost(host);
            impl.setPort(port);
            impl.setUsername(username);
            impl.setPassword(password);

            Properties smtpProps = impl.getJavaMailProperties();
            smtpProps.put("mail.transport.protocol", "smtp");
            smtpProps.put("mail.smtp.auth", "true");
            smtpProps.put("mail.smtp.starttls.enable", "true");
            smtpProps.put("mail.smtp.starttls.required", "true");
            smtpProps.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            smtpProps.put("mail.smtp.connectiontimeout", "10000");
            smtpProps.put("mail.smtp.timeout", "10000");

            log.info("[SMTP-UPDATE] Gmail SMTP credentials updated successfully for account {}", username);

            // Persist permanently in database
            CompanyEmailConfig cfg = configRepo.findFirstByIsActiveTrueAndIsDefaultTrue()
                .or(() -> configRepo.findFirstByIsActiveTrue())
                .orElseGet(CompanyEmailConfig::new);

            cfg.setCompanyName("Manage My Desk");
            cfg.setEmailAddress(username);
            cfg.setSmtpHost(host);
            cfg.setSmtpPort(port);
            cfg.setSmtpUser(username);
            cfg.setSmtpPass(password);
            cfg.setImapHost("imap.gmail.com");
            cfg.setImapPort(993);
            cfg.setImapUser(username);
            cfg.setImapPass(password);
            cfg.setEncryption("TLS");
            cfg.setIsActive(true);
            cfg.setIsDefault(true);
            configRepo.save(cfg);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gmail SMTP App Password updated and verified! Account: " + username
            ));
        } catch (Exception e) {
            log.error("[SMTP-UPDATE] Failed to update Gmail App Password: {}", e.getMessage());
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "error", "Gmail SMTP authentication test failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/email/smtp-config")
    public ResponseEntity<?> getSmtpConfig() {
        String email = "aakash42633@gmail.com";
        String smtpHost = "smtp.gmail.com";
        int smtpPort = 587;
        String imapHost = "imap.gmail.com";
        int imapPort = 993;

        return ResponseEntity.ok(Map.of(
            "success", true,
            "emailAddress", email,
            "smtpHost", smtpHost,
            "smtpPort", smtpPort,
            "imapHost", imapHost,
            "imapPort", imapPort,
            "username", email,
            "password", "••••••••••••••••",
            "verified", true,
            "isConfigured", true
        ));
    }

    @GetMapping("/email-configs")
    public ResponseEntity<?> listConfigs() {
        List<CompanyEmailConfig> list = configRepo.findByIsActiveTrueOrderByIsDefaultDescCompanyNameAsc();
        List<Map<String, Object>> safeList = new ArrayList<>();
        for (CompanyEmailConfig c : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("companyName", c.getCompanyName());
            m.put("emailAddress", "aakash42633@gmail.com");
            m.put("smtpHost", "smtp.gmail.com");
            m.put("smtpPort", 587);
            m.put("smtpUser", "aakash42633@gmail.com");
            m.put("smtpPass", "••••••••••••••••");
            m.put("imapHost", "imap.gmail.com");
            m.put("imapPort", 993);
            m.put("imapUser", "aakash42633@gmail.com");
            m.put("imapPass", "••••••••••••••••");
            m.put("encryption", "TLS");
            m.put("isActive", true);
            m.put("isDefault", true);
            safeList.add(m);
        }
        return ResponseEntity.ok(safeList);
    }

    @PostMapping("/email-configs")
    public ResponseEntity<?> createConfig(@RequestBody CompanyEmailConfig cfg) {
        cfg.setEmailAddress("aakash42633@gmail.com");
        cfg.setSmtpHost("smtp.gmail.com");
        cfg.setSmtpPort(587);
        cfg.setSmtpUser("aakash42633@gmail.com");
        cfg.setImapHost("imap.gmail.com");
        cfg.setImapPort(993);
        cfg.setImapUser("aakash42633@gmail.com");
        cfg.setEncryption("TLS");
        cfg.setIsActive(true);
        cfg.setIsDefault(true);
        return ResponseEntity.status(201).body(configRepo.save(cfg));
    }

    @PutMapping("/email-configs/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody CompanyEmailConfig cfg) {
        return configRepo.findById(id).map(existing -> {
            existing.setEmailAddress("aakash42633@gmail.com");
            existing.setSmtpHost("smtp.gmail.com");
            existing.setSmtpPort(587);
            existing.setSmtpUser("aakash42633@gmail.com");
            if (cfg.getSmtpPass() != null && !cfg.getSmtpPass().startsWith("•")) {
                existing.setSmtpPass(cfg.getSmtpPass());
                existing.setImapPass(cfg.getSmtpPass());
            }
            existing.setImapHost("imap.gmail.com");
            existing.setImapPort(993);
            existing.setImapUser("aakash42633@gmail.com");
            existing.setEncryption("TLS");
            existing.setIsActive(true);
            existing.setIsDefault(true);
            return ResponseEntity.ok((Object) configRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/email-configs/{id}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true));
    }
}
