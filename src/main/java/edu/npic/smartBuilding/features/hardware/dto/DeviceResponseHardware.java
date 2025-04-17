package edu.npic.smartBuilding.features.hardware.dto;

import lombok.Builder;

@Builder
public record DeviceResponseHardware(
        Integer id,
        String value
) {
}
