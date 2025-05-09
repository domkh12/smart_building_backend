package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.Room;
import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;
import edu.npic.smartBuilding.features.room.dto.RoomRequest;
import edu.npic.smartBuilding.features.room.dto.RoomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    void updateFromRoomRequest(RoomRequest roomRequest, @MappingTarget Room room);

    RoomNameResponse toRoomNameResponse(Room room);

    Room fromRoomRequest(RoomRequest roomRequest);

    RoomResponse toRoomResponse(Room room);
}
