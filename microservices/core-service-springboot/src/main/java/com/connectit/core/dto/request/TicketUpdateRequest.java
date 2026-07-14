package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketUpdateRequest {
    private String status;
    private String priority;
    private String urgency;
    private String impact;
    private String assignmentGroup;
    private String assignedTo;
    private String assignedToName;
    private String resolutionCode;
    private String resolutionNotes;
    private String resolutionMethod;
    private String onHoldReason;
    private String closureReason;
    private Map<String, Object> customFields;
    private List<Object> history;
}
