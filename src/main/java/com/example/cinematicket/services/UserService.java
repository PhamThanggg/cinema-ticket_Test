package com.example.cinematicket.services;

import com.example.cinematicket.component.JwtTokenUtils;
import com.example.cinematicket.dtos.UserDTO;
import com.example.cinematicket.exceptions.DataNotFoundException;
import com.example.cinematicket.exceptions.ErrorCode;
import com.example.cinematicket.exceptions.InvalidParamException;
import com.example.cinematicket.mapper.UserMapper;
import com.example.cinematicket.models.Role;
import com.example.cinematicket.models.User;
import com.example.cinematicket.repositories.RoleRepository;
import com.example.cinematicket.repositories.UserRepository;
import com.example.cinematicket.response.ApiResponse;
import com.example.cinematicket.response.AppException;
import com.example.cinematicket.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserMapper userMapper;
    @Override
    public User createUser(UserDTO userDTO)  {
        String email = userDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Role role = roleRepository.findById(userDTO.getRole())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .fullName(userDTO.getName())
                .image(userDTO.getImage())
                .gender(userDTO.getGender())
                .phone(userDTO.getPhone())
                .dateOfBirth(userDTO.getDateOfBirth())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .status(userDTO.getStatus())
                .build();
        user.setRole(role);

        // ma hoa mk
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public String login(String email, String password) throws DataNotFoundException, InvalidParamException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid email");
        }
        User user = optionalUser.get();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException("Wrong email or password");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            email, password, user.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new RuntimeException("email not found")
        );
        return userMapper.toUserResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(PageRequest request) {
        return userRepository.findAll(request).map(userMapper::toUserResponse);
    }

    @Override
    public Page<UserResponse> searchUsers(PageRequest request) {
        return null;
    }

    @Override
    public List<UserResponse> getLimitUsers(PageRequest request) {
        return userRepository.findAll(request).stream().map(userMapper::toUserResponse).toList();

    }


}
