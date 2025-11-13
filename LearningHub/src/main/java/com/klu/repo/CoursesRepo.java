package com.klu.repo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.Courses;

public interface CoursesRepo extends JpaRepository<Courses, String>{
	List<Courses> findByUsername(String username);

	
}
