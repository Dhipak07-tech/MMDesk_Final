package com.connectit.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetCreateRequest {
    private String name;
    private String type;
    private String status;
    private String owner;
    private String location;
    private String serialNumber;
    private String model;
    private String manufacturer;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiry;
    private String ipAddress;
    private String description;
}
