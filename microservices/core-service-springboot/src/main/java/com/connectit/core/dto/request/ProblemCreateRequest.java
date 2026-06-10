package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCreateRequest {
    private String title;
    private String description;
    private String priority;
    private String category;
    private String assignedTo;
}
