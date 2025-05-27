package edu.npic.smartBuilding.features.room;

import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;
import edu.npic.smartBuilding.features.room.dto.RoomRequest;
import edu.npic.smartBuilding.features.room.dto.RoomResponse;
import edu.npic.smartBuilding.features.room.dto.RoomResponseGetById;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoomService {
    RoomResponseGetById getRoomById(Integer id);

    RoomResponse create(RoomRequest roomRequest);

    Page<RoomResponse> findAll(int pageNo, int pageSize);

    List<RoomNameResponse> findAllName();

    RoomResponse update(Integer id, RoomRequest roomRequest);

    Page<RoomResponse> roomFilter(int pageNo, int pageSize, List<Integer> buildingId, String keywords);

    void deleteRoom(Integer id);
}
