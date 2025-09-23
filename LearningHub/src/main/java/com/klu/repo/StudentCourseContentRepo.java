package com.klu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.StudentCourseContent;

public interface StudentCourseContentRepo extends JpaRepository<StudentCourseContent, Long> {
    List<StudentCourseContent> findByUsernameAndCourseId(String username, String courseId);
    List<StudentCourseContent> findByCourseId(String courseId);
}

