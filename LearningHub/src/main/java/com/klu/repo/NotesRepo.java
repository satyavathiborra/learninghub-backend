package com.klu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klu.entity.Notes;

public interface NotesRepo extends JpaRepository<Notes, Long>{
	List<Notes> findByUsername(String username);
	public List<Notes> findByUsernameAndCourseId(String username, String courseId);


}
