package edu.npic.smartBuilding.features.analysis.dto;
import lombok.Builder;

@Builder
public record AnalysisResponse(
        Long userCount,
        Long buildingCount,
        Long roomCount,
        Long deviceCount,
        PowerAnalysisResponse powerAnalysis,
        StatusDeviceResponse statusDevice
) {
}
