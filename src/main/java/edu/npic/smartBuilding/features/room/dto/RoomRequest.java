package edu.npic.smartBuilding.features.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record RoomRequest(
        @NotBlank(message = "Name is required!")
        String name,
        String image,
        @NotNull(message = "FloorId is required!")
        Integer floorId
) {
}
