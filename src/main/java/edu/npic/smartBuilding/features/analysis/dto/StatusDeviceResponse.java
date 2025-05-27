package edu.npic.smartBuilding.features.analysis.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record StatusDeviceResponse(
        List<Long> series,
        List<String> labels
) {
}
