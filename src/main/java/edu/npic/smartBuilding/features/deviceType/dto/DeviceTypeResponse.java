package edu.npic.smartBuilding.features.deviceType.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record DeviceTypeResponse(
        Integer id,
        String name,
        String description,
        String image,
        @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
        LocalDateTime createdAt,
        Boolean controllable
) {
}
