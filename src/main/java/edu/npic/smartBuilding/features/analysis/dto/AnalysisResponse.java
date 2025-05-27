package edu.npic.smartBuilding.features.analysis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

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
