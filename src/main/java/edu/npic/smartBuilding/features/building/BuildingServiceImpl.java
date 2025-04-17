package edu.npic.smartBuilding.features.building;

import edu.npic.smartBuilding.domain.Building;
import edu.npic.smartBuilding.features.building.dto.BuildingNameResponse;
import edu.npic.smartBuilding.features.building.dto.BuildingRequest;
import edu.npic.smartBuilding.features.building.dto.BuildingResponse;
import edu.npic.smartBuilding.mapper.BuildingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService{

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Override
    public BuildingResponse getBuildingById(Integer id) {
        Building building = buildingRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found!")
        );
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    public Page<BuildingResponse> filterBuilding(String keywords, int pageSize, int pageNo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Building> buildings = buildingRepository.filterBuilding(keywords, pageRequest);
        return buildings.map(buildingMapper::toBuildingResponse);
    }

    @Override
    public void delete(Integer id) {
        Building building = buildingRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found")
        );

        buildingRepository.delete(building);
    }

    @Override
    public BuildingResponse update(Integer id, BuildingRequest buildingRequest) {
        Building building = buildingRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found")
        );

        buildingMapper.fromBuildingRequestUpdate(buildingRequest, building);
        buildingRepository.save(building);
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    public Page<BuildingResponse> findAll(int pageNo, int pageSize) {

        if (pageNo <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be more than 0");
        }

        if (pageSize <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page Size must be more than 0");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Building> buildings = buildingRepository.findAll(pageRequest);

        return buildings.map(buildingMapper::toBuildingResponse);
    }

    @Override
    public List<BuildingNameResponse> findAllName() {
        List<Building> buildings = buildingRepository.findAll();
        return buildings.stream().map(buildingMapper::toBuildingNameResponse).toList();
    }

    @Override
    public BuildingResponse create(BuildingRequest buildingRequest) {

        if(buildingRepository.existsByName(buildingRequest.name())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Building building = buildingMapper.fromBuildingRequest(buildingRequest);
        building.setUuid(UUID.randomUUID().toString());
        building.setCreatedAt(LocalDateTime.now());
        building.setFloorQty(0);
        buildingRepository.save(building);

        return buildingMapper.toBuildingResponse(building);

    }
}
