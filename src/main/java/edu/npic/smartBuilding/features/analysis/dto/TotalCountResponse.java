package edu.npic.smartBuilding.features.analysis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record TotalCountResponse(long totalUserCount,
                                 long totalDeviceCount,
                                 long totalBuildingCount,
                                 long totalRoomCount,
                                 long totalFloorCount
) {
}
