package edu.npic.smartBuilding.features.analysis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record SeriesResponse<T>(
        String name,
        List<Double> data
) {
}
