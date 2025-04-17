package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.Floor;
import edu.npic.smartBuilding.features.floor.dto.FloorNameResponse;
import edu.npic.smartBuilding.features.floor.dto.FloorRequest;
import edu.npic.smartBuilding.features.floor.dto.FloorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    void fromFloorRequestUpdate(FloorRequest floorRequest, @MappingTarget Floor floor);

    FloorNameResponse toFloorNameResponse(Floor floor);

    Floor fromFloorRequest(FloorRequest floorRequest);

    FloorResponse toFloorResponse(Floor floor);
}
