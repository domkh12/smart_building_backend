package edu.npic.smartBuilding.features.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record EventResponse(
        Integer id,
        String value,
        @JsonFormat(pattern = "dd/MMM/yyyy hh:mma")
        LocalDateTime createdAt
) {
}
