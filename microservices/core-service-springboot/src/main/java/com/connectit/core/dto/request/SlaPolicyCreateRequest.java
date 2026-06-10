package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlaPolicyCreateRequest {
    private String name;
    private String priority;
    private String category;
    private Integer responseTimeHours;
    private Integer resolutionTimeHours;
    private String description;
}
