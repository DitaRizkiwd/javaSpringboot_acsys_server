package com.acsys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acsys.model.User;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findFirstByToken(String token);

    
}
