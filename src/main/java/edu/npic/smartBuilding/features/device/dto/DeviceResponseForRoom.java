package edu.npic.smartBuilding.features.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.npic.smartBuilding.base.DeviceStatus;
import edu.npic.smartBuilding.features.deviceType.dto.DeviceTypeResponse;
import edu.npic.smartBuilding.features.room.dto.RoomResponseForDevice;

import java.time.LocalDateTime;
import java.util.List;

public record DeviceResponseForRoom(
        Integer id,
        String name,
        String image,
        List<String> value,
        @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
        LocalDateTime createdAt,
        DeviceStatus status,
        DeviceTypeResponse deviceType
) {
}
