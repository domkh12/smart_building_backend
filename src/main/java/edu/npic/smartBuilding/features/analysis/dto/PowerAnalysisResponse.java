package edu.npic.smartBuilding.features.analysis.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PowerAnalysisResponse(
        String totalPower,
        List<SeriesResponse> series
) {
}
