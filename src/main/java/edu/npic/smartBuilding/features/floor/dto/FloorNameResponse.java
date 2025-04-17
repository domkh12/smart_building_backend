package edu.npic.smartBuilding.features.floor.dto;

import edu.npic.smartBuilding.features.building.dto.BuildingNameResponse;
import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;

import java.util.List;

public record FloorNameResponse(
        Integer id,
        String name,
        List<RoomNameResponse> rooms,
        BuildingNameResponse building
) {
}
