package edu.npic.smartBuilding.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ErrorResponse <T> {
    private T error;
}
