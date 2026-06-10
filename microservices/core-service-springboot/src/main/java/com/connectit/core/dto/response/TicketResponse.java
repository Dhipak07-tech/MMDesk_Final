package com.connectit.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private String id;
    private String ticketNumber;
    private String caller;
    private String callerEmail;
    private String category;
    private String incidentCategory;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String impact;
    private String urgency;
    private String channel;
    private String assignmentGroup;
    private String assignedTo;
    private String assignedToName;
    private String createdBy;
    private String createdByName;
    private Integer points;
    private LocalDateTime responseDeadline;
    private LocalDateTime resolutionDeadline;
    private LocalDateTime firstResponseAt;
    private LocalDateTime resolvedAt;
    private String responseSlaStatus;
    private String resolutionSlaStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
