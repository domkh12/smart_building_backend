package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.Building;
import edu.npic.smartBuilding.features.building.dto.BuildingNameResponse;
import edu.npic.smartBuilding.features.building.dto.BuildingRequest;
import edu.npic.smartBuilding.features.building.dto.BuildingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    void fromBuildingRequestUpdate(BuildingRequest buildingRequest, @MappingTarget Building building);

    BuildingNameResponse toBuildingNameResponse(Building building);

    Building fromBuildingRequest(BuildingRequest buildingRequest);

    BuildingResponse toBuildingResponse(Building building);
}
