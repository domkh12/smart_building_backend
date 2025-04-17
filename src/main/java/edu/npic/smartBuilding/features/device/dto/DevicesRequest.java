package edu.npic.smartBuilding.features.device.dto;

import java.util.List;

public record DevicesRequest(
        List<DeviceRequest> devices
) {
}
