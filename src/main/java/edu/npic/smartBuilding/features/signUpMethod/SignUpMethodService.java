package edu.npic.smartBuilding.features.signUpMethod;

import edu.npic.smartBuilding.features.signUpMethod.dto.SignUpMethodResponse;

import java.util.List;

public interface SignUpMethodService {
    List<SignUpMethodResponse> findAll();
}
