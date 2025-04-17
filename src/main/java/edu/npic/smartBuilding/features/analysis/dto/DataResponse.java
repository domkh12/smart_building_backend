package edu.npic.smartBuilding.features.analysis.dto;

import lombok.Builder;

@Builder
public record DataResponse(
        Long x,
        Double y
) {
}
