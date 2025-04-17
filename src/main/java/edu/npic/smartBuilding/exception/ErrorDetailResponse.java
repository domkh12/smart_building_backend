package edu.npic.smartBuilding.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ErrorDetailResponse <T> {
    private T code;
    private T description;
}
