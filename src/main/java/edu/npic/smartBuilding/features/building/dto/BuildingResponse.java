package edu.npic.smartBuilding.features.building.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record BuildingResponse(
        Integer id,
        String name,
        String image,
        @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
        LocalDateTime createdAt,
        Integer floorQty) {
}
