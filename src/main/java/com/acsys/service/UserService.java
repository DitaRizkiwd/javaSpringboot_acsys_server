package com.acsys.service;

import javax.xml.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.acsys.model.User;
import com.acsys.payload.RegisterUserRequest;
import com.acsys.payload.UserResponse;
import com.acsys.repository.UserRepository;
import com.acsys.security.BCrypt;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // @Autowired
    // private Validator validator;

    public UserResponse register(RegisterUserRequest request){
        if (userRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Register");

        }
        //create new user
        //set user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        //save user
        userRepository.save(user);
        
        //
        UserResponse response = new UserResponse(user.getUsername(), user.getName());
        return response;
    }

    
}
