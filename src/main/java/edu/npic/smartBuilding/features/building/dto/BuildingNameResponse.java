package edu.npic.smartBuilding.features.building.dto;

import edu.npic.smartBuilding.features.floor.dto.FloorNameResponse;
import edu.npic.smartBuilding.features.floor.dto.FloorNameResponseForBuilding;

import java.util.List;

public record BuildingNameResponse(
        Integer id,
        String name,
        List<FloorNameResponseForBuilding> floors
) {
}
