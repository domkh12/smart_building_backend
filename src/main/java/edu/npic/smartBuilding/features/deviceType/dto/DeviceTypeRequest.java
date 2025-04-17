package edu.npic.smartBuilding.features.deviceType.dto;

public record DeviceTypeRequest(
        String name,
        String description,
        String image,
        Boolean controllable
) {

}
