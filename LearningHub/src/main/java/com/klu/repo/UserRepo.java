package com.klu.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.User;

public interface UserRepo extends JpaRepository<User, String>{

	boolean existsByEmail(String email);

}
