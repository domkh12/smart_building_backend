package edu.npic.smartBuilding.features.analysis.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SeriesResponse(
        String name,
        List<DataResponse> data
) {
}
