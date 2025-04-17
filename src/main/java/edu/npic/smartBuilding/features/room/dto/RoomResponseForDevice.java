package edu.npic.smartBuilding.features.room.dto;

import edu.npic.smartBuilding.features.floor.dto.FloorResponse;

public record RoomResponseForDevice(
        Integer id,
        String name,
        FloorResponse floor
) {
}
