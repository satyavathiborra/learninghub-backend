package com.klu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.StudentCourse;

public interface StudentCourseRepo extends JpaRepository<StudentCourse, Long>{
	List<StudentCourse> findByUsername(String username);
}
