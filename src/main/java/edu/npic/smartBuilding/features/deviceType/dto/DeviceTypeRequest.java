package edu.npic.smartBuilding.features.deviceType.dto;

import jakarta.validation.constraints.NotNull;

public record DeviceTypeRequest(
        @NotNull(message = "Name field is required")
        String name,
        String description,
        String image,
        @NotNull(message = "Controllable field is required")
        Boolean controllable
) {

}
