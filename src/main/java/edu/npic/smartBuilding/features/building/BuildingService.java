package edu.npic.smartBuilding.features.building;

import edu.npic.smartBuilding.features.building.dto.BuildingNameResponse;
import edu.npic.smartBuilding.features.building.dto.BuildingRequest;
import edu.npic.smartBuilding.features.building.dto.BuildingResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BuildingService {

    BuildingResponse getBuildingById(Integer id);

    BuildingResponse create(BuildingRequest buildingRequest);

    Page<BuildingResponse> findAll(int pageNo, int pageSize);

    List<BuildingNameResponse> findAllName();

    BuildingResponse update(Integer id, BuildingRequest buildingRequest);

    void delete(Integer id);

    Page<BuildingResponse> filterBuilding(String keywords, int pageSize, int pageNo);
}
