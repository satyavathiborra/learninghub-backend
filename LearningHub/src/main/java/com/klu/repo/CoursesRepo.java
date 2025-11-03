package com.klu.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.Courses;

public interface CoursesRepo extends JpaRepository<Courses, String>{
	
}
