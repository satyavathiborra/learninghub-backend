package com.klu.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.CourseContent;

public interface CourseContentRepo extends JpaRepository<CourseContent, Long>{
	CourseContent findByCourseIdAndModule(String courseId,int module);
}
