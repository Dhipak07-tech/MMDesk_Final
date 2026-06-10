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
public class ActivityResponse {
    private String id;
    private String ticketId;
    private String activityType;
    private String visibilityType;
    private String createdBy;
    private String createdByName;
    private String message;
    private String metadataJson;
    private LocalDateTime createdAt;
}
