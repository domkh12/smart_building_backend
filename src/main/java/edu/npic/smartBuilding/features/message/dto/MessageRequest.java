package edu.npic.smartBuilding.features.message.dto;


import edu.npic.smartBuilding.base.MessageType;

public record MessageRequest(
        String deviceId,
        String value,
        String username,
        MessageType messageType
) {
}
