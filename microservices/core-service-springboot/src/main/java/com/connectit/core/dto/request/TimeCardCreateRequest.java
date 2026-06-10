package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeCardCreateRequest {
    private Integer timesheetId;
    private String userId;
    private LocalDate entryDate;
    private String task;
    private BigDecimal hoursWorked;
    private String description;
    private String shortDescription;
    private String startTime;
    private String endTime;
    private BigDecimal deduct;
    private String workType;
    private String billable;
    private String ticketNumber;
    private String ticketId;
    private Integer isSystemGenerated;
}
