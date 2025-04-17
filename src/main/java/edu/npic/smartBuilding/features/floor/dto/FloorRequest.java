package edu.npic.smartBuilding.features.floor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FloorRequest(
        @NotBlank(message = "Name is required!")
        String name,
        String image,
        @NotNull(message = "BuildingId is required!")
        Integer buildingId
) {
}
