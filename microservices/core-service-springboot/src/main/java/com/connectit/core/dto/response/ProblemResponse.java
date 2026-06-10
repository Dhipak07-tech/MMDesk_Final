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
public class ProblemResponse {
    private String id;
    private String problemNumber;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String category;
    private String rootCause;
    private String workaround;
    private String resolution;
    private String assignedTo;
    private String assignedToName;
    private String reportedBy;
    private String reportedByName;
    private Integer relatedIncidents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
}
