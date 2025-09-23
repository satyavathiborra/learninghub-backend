package com.klu.service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.klu.Cryptography;
import com.klu.LoginDetails;
import com.klu.entity.CourseContent;
import com.klu.entity.Courses;
import com.klu.entity.Exam;
import com.klu.entity.Notes;
import com.klu.entity.StudentCourse;
import com.klu.entity.StudentCourseContent;
import com.klu.entity.User;
import com.klu.repo.CourseContentRepo;
import com.klu.repo.CoursesRepo;
import com.klu.repo.ExamRepo;
import com.klu.repo.NotesRepo;
import com.klu.repo.StudentCourseContentRepo;
import com.klu.repo.StudentCourseRepo;
import com.klu.repo.UserRepo;

@org.springframework.stereotype.Service
public class Service {
	@Autowired
	UserRepo repo1;
	
	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	ExamRepo examrepo;
	@Autowired
	StudentCourseRepo studentCourseRepo;
	@Autowired
	NotesRepo notesRepo;
	@Autowired
	StudentCourseContentRepo contentRepo;
	@Autowired
	CoursesRepo coursesRepo;
	@Autowired
	CourseContentRepo courseContentRepo;
	public String insertUser(User user) {
		 if (repo1.findById(user.getUsername()).isPresent()) {
	            return "Username already exists";
	        }

	        if (repo1.existsByEmail(user.getEmail())) {
	            return "Email already exists";
	        }

	        repo1.save(user);
	        return "Signed up successfully";
	}
	
	public Authenti verifyUser(LoginDetails log) {
		
		if (repo1.findById(log.getUsername()).isEmpty())  
		    return new Authenti(); // Default auth = false, role = 0  

		String or = repo1.findById(log.getUsername()).get().getPassword();  
		Cryptography c = new Cryptography();  
		String pass = c.decryptData(or);  

		Authenti a = new Authenti();
		if (pass.equals(log.getPassword())) {
		    a.setAuth(true);
		    a.setRole(repo1.findById(log.getUsername()).get().getRole());
		} else {
		    a.setAuth(false);
		    a.setRole(0); // Default or error role value
		}

		return a;

	}
	
   public String sendPassword(String username) {
       try {
           Optional<User> userOptional = repo1.findById(username);
           
           if (userOptional.isEmpty()) {
               return "Error: Username not found. Please sign up.";
           }

           String email = userOptional.get().getEmail();
           System.out.println("Mail: " + email);

           Cryptography c = new Cryptography();
           String Enpassword = userOptional.get().getPassword();
           String password = c.decryptData(Enpassword);
           System.out.println("Password: " + password);

           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom("learninghubfsad@gmail.com");
           message.setTo(email);
           message.setSubject("Password request for Learning Hub");
           message.setText("This is your current password: " + password);

           mailSender.send(message);
           return "Mail sent successfully";

       } catch (MailException e) {
           return "Error: Failed to send email. Please check your email configuration.";
       } catch (Exception e) {
           return "Error: Something went wrong. " + e.getMessage();
       }
   }
	
    
    public String insertQuestion(List<Exam> questions) {
        try {
            if (questions == null || questions.isEmpty()) {
                return "Error: Question list is empty.";
            }

            examrepo.saveAll(questions);
            return "Successfully inserted";
        } catch (Exception e) {
            return "Error: Failed to insert questions. " + e.getMessage();
        }
    }
    public List<Exam> retreiveQuestions(String courseId) {
        try {
            if (courseId == null || courseId.trim().isEmpty()) {
                throw new IllegalArgumentException("Course ID cannot be null or empty.");
            }

            List<Exam> questions = examrepo.findByCourseId(courseId);

            if (questions == null || questions.isEmpty()) {
                throw new NoSuchElementException("No questions found for the given course ID.");
            }

            return questions;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println("Error: " + e.getMessage());
            return Collections.emptyList(); // Return empty list if known errors
        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
            return Collections.emptyList(); // Return empty list if unknown error
        }
    }
    
    public String addStudentCourse(StudentCourse studentCourse) {
        try {
            if (studentCourse == null) {
                return "Error: StudentCourse data is null.";
            }

            studentCourseRepo.save(studentCourse);
            return "Student course added successfully.";
        } catch (Exception e) {
            return "Error: Failed to add student course. " + e.getMessage();
        }
    }

    // 2. Retrieve all StudentCourses by username
    public List<StudentCourse> getCoursesByUsername(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty.");
            }

            List<StudentCourse> courses = studentCourseRepo.findByUsername(username);
            
            if (courses == null || courses.isEmpty()) {
                throw new NoSuchElementException("No courses found for username: " + username);
            }

            return courses;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println("Error: " + e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 3. Update percentage for a student in a specific course
    public String updatePercentage(String username, String courseId, double newPercentage) {
        try {
            if (username == null || username.trim().isEmpty() || courseId == null || courseId.trim().isEmpty()) {
                return "Error: Username or Course ID cannot be null or empty.";
            }

            List<StudentCourse> studentCourses = studentCourseRepo.findByUsername(username);

            if (studentCourses == null || studentCourses.isEmpty()) {
                return "Error: No courses found for this username.";
            }

            boolean updated = false;
            for (StudentCourse course : studentCourses) {
                if (course.getCourseId().equals(courseId)) {
                    course.setPercentage(newPercentage);
                    studentCourseRepo.save(course);
                    updated = true;
                    break;
                }
            }

            if (updated) {
                return "Percentage updated successfully.";
            } else {
                return "Error: Course ID not found for this user.";
            }
        } catch (Exception e) {
            return "Error: Failed to update percentage. " + e.getMessage();
        }
    }
    
    public String insertNote(Notes note) {
        try {
            if (note.getUsername() == null || note.getUsername().trim().isEmpty() ||
                note.getCourseId() == null || note.getCourseId().trim().isEmpty() ||
                note.getNote() == null || note.getNote().trim().isEmpty()) {
                return "Error: Fields cannot be null or empty.";
            }

            notesRepo.save(note);
            return "Note inserted successfully.";
        } catch (Exception e) {
            return "Error while inserting note: " + e.getMessage();
        }
    }
    
    public Notes getNoteByUsernameAndCourseId(String username, String courseId) {
        try {
            // Fetch notes by username
            List<Notes> userNotes = notesRepo.findByUsername(username);

            if (userNotes.isEmpty()) {
                return null; // No notes found for the user
            }

            // Find the first note that matches the courseId
            for (Notes note : userNotes) {
                if (note.getCourseId().equals(courseId)) {
                    return note; // Return the first matching note
                }
            }

            return null; // If no matching courseId was found
        } catch (Exception e) {
            return null; // Return null in case of error
        }
    }

    
    public String updateNoteByUsernameAndCourseId(String username, String courseId, String updatedContent) {
        try {
            List<Notes> notesList = notesRepo.findByUsernameAndCourseId(username, courseId);

            if (notesList.isEmpty()) {
                return "No note found for username: " + username + " and courseId: " + courseId;
            }

            Notes noteToUpdate = notesList.get(0); 
            noteToUpdate.setNote(updatedContent);

            notesRepo.save(noteToUpdate);
            return "Note updated successfully!";
        } catch (Exception e) {
            return "Failed to update note: " + e.getMessage();
        }
    }
    public String insertContent(StudentCourseContent content) {
        try {
            contentRepo.save(content);
            return "Content inserted successfully!";
        } catch (Exception e) {
            return "Failed to insert content: " + e.getMessage();
        }
    }

    // 2. Retrieve all contents for a username and courseId
    public List<StudentCourseContent> retrieveContentByUsernameAndCourseId(String username, String courseId) {
        try {
            return contentRepo.findByUsernameAndCourseId(username, courseId);
        } catch (Exception e) {
            return List.of();  // empty list if error
        }
    }

    // 3. Update percentage based on username, courseId, and module
    public String updatePercentage(String username, String courseId, int module, double newPercentage) {
        try {
            List<StudentCourseContent> contentList = contentRepo.findByUsernameAndCourseId(username, courseId);

            for (StudentCourseContent content : contentList) {
                if (content.getModule() == module) {
                    content.setPercentage(newPercentage);
                    contentRepo.save(content);
                    return "Percentage updated successfully!";
                }
            }
            return "Content not found for given module!";
        } catch (Exception e) {
            return "Failed to update content: " + e.getMessage();
        }
    }
    public String insertCourse(Courses course) {
        try {
            coursesRepo.save(course);
            return "Course inserted successfully!";
        } catch (Exception e) {
            return "Failed to insert course: " + e.getMessage();
        }
    }

    // Retrieve all Courses
    public List<Courses> getAllCourses() {
        try {
            return coursesRepo.findAll();
        } catch (Exception e) {
            return List.of(); // Empty list if error
        }
    }

    // Retrieve a specific Course by courseId
    public Courses getCourseById(String courseId) {
        try {
            Optional<Courses> course = coursesRepo.findById(courseId);
            return course.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    // Delete a Course by courseId
    public String deleteCourseById(String courseId) {
        try {
            if (coursesRepo.existsById(courseId)) {
                coursesRepo.deleteById(courseId);
                return "Course deleted successfully!";
            } else {
                return "Course not found with ID: " + courseId;
            }
        } catch (Exception e) {
            return "Failed to delete course: " + e.getMessage();
        }
    }
 // Insert new CourseContent
    public String insertCourseContent(CourseContent courseContent) {
        try {
            courseContentRepo.save(courseContent);
            return "Course Content inserted successfully!";
        } catch (Exception e) {
            return "Failed to insert Course Content: " + e.getMessage();
        }
    }

    // Retrieve CourseContent by courseId and module
    public CourseContent getCourseContentByCourseIdAndModule(String courseId, int module) {
        try {
            return courseContentRepo.findByCourseIdAndModule(courseId, module);
        } catch (Exception e) {
            return null;
        }
    }
    
    
    

	
}
