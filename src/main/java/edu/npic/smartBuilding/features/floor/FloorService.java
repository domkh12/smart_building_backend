package edu.npic.smartBuilding.features.floor;

import edu.npic.smartBuilding.features.floor.dto.FloorNameResponse;
import edu.npic.smartBuilding.features.floor.dto.FloorRequest;
import edu.npic.smartBuilding.features.floor.dto.FloorResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FloorService {

    FloorResponse findFloorById(int id);

    FloorResponse create(FloorRequest floorRequest);

    Page<FloorResponse> findAll(int pageNo, int pageSize);

    List<FloorNameResponse> findAllName();

    void deleteFloor(Integer id);

    FloorResponse updateFloor(Integer id, FloorRequest floorRequest);

    Page<FloorResponse> filterFloor(String keywords, List<Integer> buildingId, int pageSize, int pageNo);
}
