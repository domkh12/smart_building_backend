package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.Gender;
import edu.npic.smartBuilding.features.gender.dto.GenderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenderMapper {

    GenderResponse toGenderResponse(Gender gender);

}
