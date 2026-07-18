package com.connectit.core.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_screenshots", indexes = {
    @Index(name = "idx_tkt_ss_ticket_id", columnList = "ticket_id"),
    @Index(name = "idx_tkt_ss_ticket_number", columnList = "ticket_number")
})
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketScreenshot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "ticket_number", length = 50)
    private String ticketNumber;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(name = "screenshot_url", columnDefinition = "TEXT")
    private String screenshotUrl;

    @Column(name = "screenshot_data", columnDefinition = "LONGTEXT")
    private String screenshotData;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "captured_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime capturedAt = LocalDateTime.now();
}
