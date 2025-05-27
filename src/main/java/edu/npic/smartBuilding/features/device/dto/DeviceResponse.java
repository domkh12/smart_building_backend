package edu.npic.smartBuilding.features.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;
import edu.npic.smartBuilding.features.event.dto.EventResponse;
import edu.npic.smartBuilding.features.floor.dto.FloorResponse;
import edu.npic.smartBuilding.features.room.dto.RoomNameResponse;
import edu.npic.smartBuilding.features.room.dto.RoomResponse;
import edu.npic.smartBuilding.features.room.dto.RoomResponseForDevice;

import java.time.LocalDateTime;
import java.util.List;

public record DeviceResponse(
    Integer id,
    String name,
    String image,
    List<String> value,
    @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
    LocalDateTime createdAt,
    DeviceStatus status,
    RoomResponseForDevice room,
    DeviceTypeResponse deviceType,
    List<EventResponse> events
) {
}
