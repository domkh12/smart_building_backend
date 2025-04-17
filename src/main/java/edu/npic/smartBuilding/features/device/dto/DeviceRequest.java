package edu.npic.smartBuilding.features.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceRequest(
        @NotBlank(message = "Name is required!")
        String name,
        String image,
        @NotNull(message = "DeviceTypeId is required!")
        Integer deviceTypeId,
        @NotNull(message = "RoomId is required!")
        Integer roomId

) {
}
