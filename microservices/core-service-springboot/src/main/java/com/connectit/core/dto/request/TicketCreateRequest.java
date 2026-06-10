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
public class TicketCreateRequest {
    private String title;
    private String description;
    private String category;
    private String subcategory;
    private String priority;
    private String urgency;
    private String impact;
    private String caller;
    private String affectedUser;
    private String assignmentGroup;
    private String assignedTo;
    private Map<String, Object> customFields;
}
