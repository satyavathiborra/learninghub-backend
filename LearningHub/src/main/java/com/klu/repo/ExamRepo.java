package com.klu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.Exam;

public interface ExamRepo extends JpaRepository<Exam, Long>{
	
	List<Exam> findByCourseId(String courseId);

	
}
