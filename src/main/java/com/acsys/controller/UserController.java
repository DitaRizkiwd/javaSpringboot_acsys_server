package com.acsys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.acsys.payload.LoginResponse;
import com.acsys.payload.LoginUserRequest;
import com.acsys.payload.RegisterUserRequest;
import com.acsys.payload.UserResponse;
import com.acsys.payload.WebResponse;
import com.acsys.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class UserController {


    @Autowired
    private UserService userService;

    //consume dan produces memastikan bahwa data yang kita kirim dan terima dalam bentuk json
   @PostMapping(
    path = "/api/users",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
   )
    // public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request){
    //     return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    // }
    public WebResponse<UserResponse> register(@RequestBody RegisterUserRequest request, @RequestHeader("X-API-TOKEN") String token){
        UserResponse userResponse = userService.register(request, token);
        // WebResponse<UserResponse> response = new WebResponse<UserResponse>();
        // response.setData(userResponse);
        // response.setError(null);

        return WebResponse.<UserResponse>builder().data(userResponse).error(null).build();

    }

    @PostMapping(
        path = "/api/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
       )

    public WebResponse<LoginResponse> login(@RequestBody LoginUserRequest request){
        LoginResponse response = userService.login(request);
        return WebResponse.<LoginResponse>builder().data(response).error(null).build();
    }

    @PostMapping(
        path = "/api/logout"
       )
       public WebResponse<String> logout(@RequestHeader("X-API-TOKEN") String token){
            userService.logout(token);

            return WebResponse.<String>builder().data("Logout Success").error(null).build();
       }

   
}
