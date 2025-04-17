package edu.npic.smartBuilding.features.floor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.npic.smartBuilding.features.building.dto.BuildingResponse;
import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;

import java.time.LocalDateTime;
import java.util.List;

public record FloorResponse(
        Integer id,
        String name,
        @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
        LocalDateTime createdAt,
        String image,
        Integer roomQty,
        BuildingResponse building
) {
}
