package edu.npic.smartBuilding.features.message.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import edu.npic.smartBuilding.base.DeviceStatus;
import lombok.Builder;

@Builder
public record MessageRequest(
        String deviceId,
        String value,
        String messageType,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        DeviceStatus status
) {
}
