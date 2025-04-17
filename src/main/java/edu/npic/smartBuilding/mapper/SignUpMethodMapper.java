package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.SignUpMethod;
import edu.npic.smartBuilding.features.signUpMethod.dto.SignUpMethodResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignUpMethodMapper {

    SignUpMethodResponse toSignUpMethodResponse(SignUpMethod signUpMethod);

}
