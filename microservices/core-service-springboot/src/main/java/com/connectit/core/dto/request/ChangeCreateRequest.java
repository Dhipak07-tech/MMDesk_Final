package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeCreateRequest {
    private String title;
    private String description;
    private String type;
    private String risk;
    private String impact;
    private String rollbackPlan;
    private String assignedTo;
    private LocalDateTime plannedStartDate;
    private LocalDateTime plannedEndDate;
    private String category;
    private String affectedServices;
}
