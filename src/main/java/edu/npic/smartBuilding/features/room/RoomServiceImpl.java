package edu.npic.smartBuilding.features.room;

import edu.npic.smartBuilding.base.Status;
import edu.npic.smartBuilding.domain.*;
import edu.npic.smartBuilding.features.device.DeviceRepository;
import edu.npic.smartBuilding.features.deviceType.DeviceTypeRepository;
import edu.npic.smartBuilding.features.event.EventRepository;
import edu.npic.smartBuilding.features.floor.FloorRepository;
import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;
import edu.npic.smartBuilding.features.room.dto.RoomRequest;
import edu.npic.smartBuilding.features.room.dto.RoomResponse;
import edu.npic.smartBuilding.mapper.DeviceMapper;
import edu.npic.smartBuilding.mapper.RoomMapper;
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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final RoomMapper roomMapper;
    private final DeviceMapper deviceMapper;
    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceRepository deviceRepository;
    private final EventRepository eventRepository;
    private final AuthUtil authUtil;

    @Override
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!")
        );
        Floor floor = floorRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found!")
        );
        floor.setRoomQty(floor.getRoomQty() - 1);
        roomRepository.delete(room);
    }

    @Override
    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!")
        );

        room.getDevices().forEach(device -> {
            List<Event> sortedLimitedEvents = device.getEvents().stream()
                    .sorted(Comparator.comparing(Event::getCreatedAt).reversed())
                    .limit(10)
                    .toList();
            device.setEvents(sortedLimitedEvents);
        });
        return roomMapper.toRoomResponse(room);
    }

    @Override
    public Page<RoomResponse> roomFilter(int pageNo, int pageSize, List<Integer> buildingId, String keywords) {
        if (pageNo < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page number");
        } else if (pageSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page size");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);

        List<Integer> buildingIds = null;
        if (!buildingId.isEmpty()) {
            buildingIds = buildingId;
        }

        Page<Room> rooms = roomRepository.roomFilter(keywords, buildingIds, pageRequest);

        return rooms.map(roomMapper::toRoomResponse);
    }

    @Override
    public RoomResponse update(Integer id, RoomRequest roomRequest) {
        Room room = roomRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!")
        );

        roomMapper.updateFromRoomRequest(roomRequest, room);

        Floor floor = floorRepository.findById(roomRequest.floorId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found!")
        );
        room.setFloor(floor);

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    @Override
    public List<RoomNameResponse> findAllName() {
        boolean isAdmin = authUtil.isAdminLoggedUser();
        boolean isManager = authUtil.isManagerLoggedUser();
        boolean isUser = authUtil.isUserLoggedUser();
        List<Long> roomIds = authUtil.roomIdOfLoggedUser();
        List<Room> rooms = new ArrayList<>();

        if (isAdmin || isUser) {
            roomIds.forEach(roomId -> {
                Room room = roomRepository.findById(roomId.intValue()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!")
                );
                rooms.add(room);
            });
        } else if (isManager) {
            rooms.addAll(roomRepository.findAll());
        }


        return rooms.stream().map(roomMapper::toRoomNameResponse).toList();
    }

    @Override
    public Page<RoomResponse> findAll(int pageNo, int pageSize) {

        if (pageNo <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be more than 0");
        }

        if (pageSize <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page Size must be more than 0");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Room> rooms = roomRepository.findAll(pageRequest);

        return rooms.map(roomMapper::toRoomResponse);
    }

    @Override
    public RoomResponse create(RoomRequest roomRequest) {

        Floor floor = floorRepository.findById(roomRequest.floorId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found!")
        );

        floor.setRoomQty(floor.getRoomQty() + 1);

        Room room = roomMapper.fromRoomRequest(roomRequest);
        room.setUuid(UUID.randomUUID().toString());
        room.setCreatedAt(LocalDateTime.now());
        room.setDevicesQty(0);
        room.setFloor(floor);

        roomRepository.save(room);
        floorRepository.save(floor);

        return roomMapper.toRoomResponse(room);
    }
}
