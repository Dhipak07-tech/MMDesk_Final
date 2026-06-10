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
public class ChangeResponse {
    private String id;
    private String changeNumber;
    private String title;
    private String description;
    private String type;
    private String state;
    private String risk;
    private String impact;
    private String rollbackPlan;
    private String requester;
    private String requesterName;
    private String assignedTo;
    private String assignedToName;
    private LocalDateTime plannedStartDate;
    private LocalDateTime plannedEndDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
    private String category;
    private String affectedServices;
    private String approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
