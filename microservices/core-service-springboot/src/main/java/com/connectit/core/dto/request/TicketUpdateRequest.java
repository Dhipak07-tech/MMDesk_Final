package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequest {
    private String status;
    private String priority;
    private String urgency;
    private String impact;
    private String assignmentGroup;
    private String assignedTo;
    private String resolutionCode;
    private String resolutionNotes;
    private String resolutionMethod;
    private String onHoldReason;
    private Map<String, Object> customFields;
}
