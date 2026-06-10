package com.connectit.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeCardResponse {
    private String id;
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
    private String status;
    private Integer elapsedSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
