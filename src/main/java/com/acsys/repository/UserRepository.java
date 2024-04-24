package com.acsys.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acsys.model.User;

public interface UserRepository extends JpaRepository<User, String> {


    
}
