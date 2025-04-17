package edu.npic.smartBuilding.features.building.dto;

import jakarta.validation.constraints.NotBlank;

public record BuildingRequest(
        @NotBlank(message = "name is required!")
        String name,
        String image
) {
}
