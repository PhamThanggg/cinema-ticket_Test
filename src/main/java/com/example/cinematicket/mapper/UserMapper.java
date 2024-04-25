package com.example.cinematicket.mapper;

import com.example.cinematicket.models.User;
import com.example.cinematicket.response.UserResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);

}
