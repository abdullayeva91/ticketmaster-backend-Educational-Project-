package com.ticketmaster.ticketmasteruserservice.mapper;

import com.ticketmaster.ticketmasteruserservice.dto.request.UpdateProfileRequest;
import com.ticketmaster.ticketmasteruserservice.dto.request.UserRegistrationRequest;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;
import com.ticketmaster.ticketmasteruserservice.model.User;
import com.ticketmaster.ticketmasteruserservice.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRegistrationRequest registrationRequest);

    UserProfile toUserProfile(UserRegistrationRequest registrationRequest);



    @Mapping(target = "firstName", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getFirstName() : null)")
    @Mapping(target = "lastName", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getLastName() : null)")
    @Mapping(target = "phoneNumber", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getPhoneNumber() : null)")
    UserResponse toUserResponse(User user);
    void updateProfileFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile profile);
}
