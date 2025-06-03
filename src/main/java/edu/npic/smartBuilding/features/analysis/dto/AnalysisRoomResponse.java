package edu.npic.smartBuilding.features.analysis.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AnalysisRoomResponse(
        String deviceName,
        Boolean controllable,
        List<String> xAxis,
        List<SeriesResponse> series
) {
}