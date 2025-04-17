package edu.npic.smartBuilding.features.floor;

import edu.npic.smartBuilding.domain.Building;
import edu.npic.smartBuilding.domain.Floor;
import edu.npic.smartBuilding.domain.Room;
import edu.npic.smartBuilding.features.building.BuildingRepository;
import edu.npic.smartBuilding.features.floor.dto.FloorNameResponse;
import edu.npic.smartBuilding.features.floor.dto.FloorRequest;
import edu.npic.smartBuilding.features.floor.dto.FloorResponse;
import edu.npic.smartBuilding.mapper.FloorMapper;
import edu.npic.smartBuilding.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService{

    private final FloorMapper floorMapper;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final AuthUtil authUtil;

    @Override
    public FloorResponse findFloorById(int id) {
        Floor floor = floorRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found!")
        );
        return floorMapper.toFloorResponse(floor);
    }

    @Override
    public FloorResponse updateFloor(Integer id, FloorRequest floorRequest) {
        Floor floor = floorRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found!")
        );
        floorMapper.fromFloorRequestUpdate(floorRequest, floor);
        Floor savedFloor = floorRepository.save(floor);
        return floorMapper.toFloorResponse(savedFloor);
    }

    @Override
    public Page<FloorResponse> filterFloor(String keywords, List<Integer> buildingId, int pageSize, int pageNo) {

        if (pageNo < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be greater than 0");
        }else if (pageSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be greater than 0");
        }

        List<Integer> buildingIds = null;
        if (!buildingId.isEmpty()) {
           buildingIds = buildingId;
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Floor> floors = floorRepository.filterFloor(keywords, buildingIds, pageable);

        return floors.map(floorMapper::toFloorResponse);
    }

    @Override
    public void deleteFloor(Integer id) {
        Floor floor = floorRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found!")
        );
        floorRepository.delete(floor);
    }

    @Override
    public Page<FloorResponse> findAll(int pageNo, int pageSize) {

        if (pageNo <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be more than 0");
        }

        if (pageSize <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page Size must be more than 0");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Floor> floors = floorRepository.findAll(pageRequest);

        return floors.map(floorMapper::toFloorResponse);
    }

    @Override
    public List<FloorNameResponse> findAllName() {
        boolean isAdmin = authUtil.isAdminLoggedUser();
        boolean isManager = authUtil.isManagerLoggedUser();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Floor> floors = new ArrayList<>();

        if (isManager) {
            // For manager, return all floors
            floors = floorRepository.findAll(sort);
        } else if (isAdmin) {
            // For admin, return floors with rooms having IDs in roomIds
            floors = floorRepository.findByRooms_IdIn(roomIds);

            // After fetching the floors, filter the rooms inside each floor
            for (Floor floor : floors) {
                List<Room> filteredRooms = floor.getRooms().stream()
                        .filter(room -> roomIds.contains(Long.valueOf(room.getId())))
                        .collect(Collectors.toList());
                floor.setRooms(filteredRooms);
            }

            System.out.println(floors.get(0).getRooms());
        }


        return floors.stream().map(floorMapper::toFloorNameResponse).toList();
    }

    @Override
    public FloorResponse create(FloorRequest floorRequest) {

        Building building = buildingRepository.findById(floorRequest.buildingId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found!")
        );

        if(buildingRepository.existFloorNameInBuilding(building.getUuid(), floorRequest.name())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, floorRequest.name() + " in " + building.getName() + " already exist!");
        }

        building.setFloorQty(building.getFloorQty() + 1);

        Floor floor = floorMapper.fromFloorRequest(floorRequest);
        floor.setUuid(UUID.randomUUID().toString());
        floor.setCreatedAt(LocalDateTime.now());
        floor.setBuilding(building);
        floor.setRoomQty(0);

        floorRepository.save(floor);
        buildingRepository.save(building);

        return floorMapper.toFloorResponse(floor);
    }
}
