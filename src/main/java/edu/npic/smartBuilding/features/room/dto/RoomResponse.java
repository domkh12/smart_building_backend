package edu.npic.smartBuilding.features.room.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.npic.smartBuilding.features.device.dto.DeviceResponse;
import edu.npic.smartBuilding.features.floor.dto.FloorResponse;

import java.time.LocalDateTime;
import java.util.List;


public record RoomResponse (
        Integer id,
        String name,
        Integer devicesQty,
        String image,
        @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
        LocalDateTime createdAt,
        FloorResponse floor,
        List<DeviceResponse> devices
){
}
