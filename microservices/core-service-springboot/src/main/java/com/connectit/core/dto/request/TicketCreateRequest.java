package com.connectit.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class TicketCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title cannot exceed 500 characters")
    private String title;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    private String category;
    private String incidentCategory;
    private String subcategory;
    private String type;
    private String purpose;

    private String priority;
    private String urgency;
    private String impact;
    private String caller;
    private String callerEmail;
    private String affectedUser;
    private String affectedUserEmail;
    private String reportingUserEmail;
    private String assignmentGroup;
    private String assignedTo;
    private String assignedToName;
    private String createdBy;
    private String createdByName;
    private String status;
    private String channel;
    private String service;
    private String serviceOffering;
    private String cmdbItem;
    private String companyId;
    private String watchList;
    private Map<String, Object> customFields;

    // SLA fields sent by the frontend
    private String responseDeadline;
    private String resolutionDeadline;
    private String responseSlaStartTime;
    private String resolutionSlaStartTime;
    private String responseSlaStatus;
    private String resolutionSlaStatus;
    private Number slaResolutionHours;
    private String slaPolicy;
    private String sla_name;
    private Object slaDelayMeta;
    private List<Object> slaDelayLogs;
    private Number totalPausedTime;
}
