package com.example.cinematicket.services;

import com.example.cinematicket.dtos.UserDTO;
import com.example.cinematicket.dtos.UserLoginDTO;
import com.example.cinematicket.exceptions.DataNotFoundException;
import com.example.cinematicket.exceptions.InvalidParamException;
import com.example.cinematicket.models.User;
import com.example.cinematicket.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IUserService {
    User createUser(UserDTO userDTO);

    String login(String email, String password) throws DataNotFoundException, InvalidParamException;

    UserResponse getMyInfo();

    Page<UserResponse> getAllUsers(PageRequest request);

    Page<UserResponse> searchUsers(PageRequest request);

    List<UserResponse> getLimitUsers(PageRequest request);

}
