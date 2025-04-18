package edu.npic.smartBuilding.mapper;

import edu.npic.smartBuilding.domain.User;
import edu.npic.smartBuilding.features.user.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

   void fromUpdateProfileUserRequest(UpdateProfileUserRequest updateProfileUserRequest, @MappingTarget User user);

   List<FullNameResponse> toFullNameResponse(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromUpdateStatusRequest(IsOnlineRequest isOnlineRequest, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromUpdateUserRequest(UpdateUserRequest updateUserRequest, @MappingTarget User user);

    User fromCreateUserRegister(CreateUserRegister createUserRegister);

    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User fromCreateUser(CreateUser createUser);

    UserDetailResponse toUserDetailResponse(User user);

}
