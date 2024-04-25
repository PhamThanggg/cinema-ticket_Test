package com.example.cinematicket.controller;

import com.example.cinematicket.dtos.UserDTO;
import com.example.cinematicket.dtos.UserLoginDTO;
import com.example.cinematicket.models.User;
import com.example.cinematicket.response.UserListResponse;
import com.example.cinematicket.response.UserResponse;
import com.example.cinematicket.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult result){

        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        if(!userDTO.getPassword().equals(userDTO.getRepassword())){
            return ResponseEntity.ok("Register not match");
        }

        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(user);

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginDTO userLoginDTO){
        try {
            String token = userService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
            return ResponseEntity.ok(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("login fail");
        }
    }

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getMyInfo(){
        UserResponse userResponse = userService.getMyInfo();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("")
    public ResponseEntity<UserListResponse> getAllUser(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest request = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"id"));
        Page<UserResponse> userResponse = userService.getAllUsers(request);
        int totalPage = userResponse.getTotalPages();
        List<UserResponse> users = userResponse.getContent();
        return ResponseEntity.ok(UserListResponse.builder()
                        .userResponses(users)
                        .totalPage(totalPage)
                        .build());
    }

    @GetMapping("/{total}")
    public ResponseEntity<?> getTotalUser(
            @RequestParam("total") int total
    ){
//        List<UserResponse> users = userService.getLimitUsers();
//        return ResponseEntity.ok(UserListResponse.builder()
//                .userResponses(users)
//                .totalPage(totalPage)
//                .build());
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long id, @RequestBody UserDTO userDTO){
        return ResponseEntity.ok("Update ok with userId = " + id);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long id){
        return ResponseEntity.ok("Delete ok with userId = " + id);
    }

}
