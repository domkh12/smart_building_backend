package edu.npic.smartBuilding.features.gender;

import edu.npic.smartBuilding.features.gender.dto.GenderResponse;

import java.util.List;

public interface GenderService {
    List<GenderResponse> findAll();
}
