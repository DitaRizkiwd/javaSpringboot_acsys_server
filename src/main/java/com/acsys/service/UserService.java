package com.acsys.service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.acsys.model.User;
import com.acsys.payload.LoginResponse;
import com.acsys.payload.LoginUserRequest;
import com.acsys.payload.RegisterUserRequest;
import com.acsys.payload.UserResponse;
import com.acsys.repository.UserRepository;
import com.acsys.security.BCrypt;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    // private Validator validator;
    private ValidationService validationService;

    @Autowired
    private Environment env;

    @Transactional // untuk validasi, kode dibawah akan dijalankan jika if pada
                   // constraintViolations.size() > 0 sudah terpenuhi
    public UserResponse register(RegisterUserRequest request, String token) {
        // Set<ConstraintViolation<RegisterUserRequest>> constraintViolations = validator.validate(request);

        // // if(!request.getRole().equals("admin")){
        // // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you have not
        // // allowed");
        // // }

        // if (constraintViolations.size() > 0) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ivalid request");
        // }

        validationService.validateRequest(request);




        //User allAdmin = userRepository.findFirstByRole("admin").orElse(null);
        // List<User> allUsers = userRepository.findAll();
        User allAdmin = userRepository.findFirstByRole(env.getProperty("ROLE_GET_SUPER_ADMIN")).orElse(null);
        if (allAdmin == null && !request.getRole().equals(env.getProperty("ROLE_GET_SUPER_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no admin found");
        }
        if (allAdmin != null) {
            User admin = userRepository.findFirstByToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "request not allowed"));

            // if (!admin.getRole().equals("admin")) {
                if(!admin.getRole().equals(env.getProperty("ROLE_GET_SUPER_ADMIN"))){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "request not allowed");
            }
        }

        // if (userRepository.existsById(request.getUsername())) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already register");
        // }
        if(userRepository.existByUsername(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already register");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setRole(request.getRole());
        user.setStatus(request.getStatus);

        userRepository.save(user);

        UserResponse response = new UserResponse(user.getUsername(), user.getName(), user.getRole(), user.getStatus());

        return response;
    }

    @Transactional
    public LoginResponse login(LoginUserRequest request) {
        // Set<ConstraintViolation<LoginUserRequest>> constraintViolations = validator.validate(request);

        // if (constraintViolations.size() > 0) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ivalid request");
        // }

        validationService.validateRequest(request);

        // User user = userRepository.findById(request.getUsername())
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Username or password error"));

        User user = userRepository.findFirstByUsername(request.getUsername()).orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "Username or password invalid"));

        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            if(!user.getStatus().equals(env.getProperty("STATUS_GET_ACTIVE"))){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account is inActive");
            }
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(nextExpired());

            userRepository.save(user);
            return LoginResponse.builder().username(user.getUsername()).name(user.getName()).token(user.getToken())
                    .role(user.getRole()).build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username or Password error");
        }
    }

    private Long nextExpired() {
        Instant now = Instant.now();
        Instant next = now.plusSeconds(2 * 60 * 60);
        return next.toEpochMilli();
    }

    @Transactional
    public void logout(String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid request");

        }
        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User has not login"));

        if (user.getTokenExpiredAt() < Instant.now().toEpochMilli()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has logout by system");
        }
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }
}
