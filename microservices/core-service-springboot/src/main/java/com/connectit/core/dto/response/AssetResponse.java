package com.connectit.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {
    private String id;
    private String name;
    private String type;
    private String status;
    private String owner;
    private String ownerName;
    private String location;
    private String serialNumber;
    private String model;
    private String manufacturer;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    private String ipAddress;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
