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
import com.klu.entity.*;
import com.klu.repo.*;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ExamRepo examRepo;

    @Autowired
    private StudentCourseRepo studentCourseRepo;

    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private StudentCourseContentRepo contentRepo;

    @Autowired
    private CoursesRepo coursesRepo;

    @Autowired
    private CourseContentRepo courseContentRepo;

    // ------------------- User Authentication -------------------

    public String insertUser(User user) {
        if (userRepo.findById(user.getUsername()).isPresent()) return "Username already exists";
        if (userRepo.existsByEmail(user.getEmail())) return "Email already exists";

        userRepo.save(user);
        return "Signed up successfully";
    }

    public Authenti verifyUser(LoginDetails log) {
        Optional<User> userOpt = userRepo.findById(log.getUsername());
        Authenti auth = new Authenti();

        if (userOpt.isEmpty()) {
            auth.setAuth(false);
            auth.setRole(0);
            return auth;
        }

        String decryptedPassword = new Cryptography().decryptData(userOpt.get().getPassword());
        if (decryptedPassword.equals(log.getPassword())) {
            auth.setAuth(true);
            auth.setRole(userOpt.get().getRole());
        } else {
            auth.setAuth(false);
            auth.setRole(0);
        }

        return auth;
    }

    public String sendPassword(String username) {
        try {
            Optional<User> userOpt = userRepo.findById(username);
            if (userOpt.isEmpty()) return "Error: Username not found.";

            String password = new Cryptography().decryptData(userOpt.get().getPassword());
            String email = userOpt.get().getEmail();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("learninghubfsad@gmail.com");
            message.setTo(email);
            message.setSubject("Password request for Learning Hub");
            message.setText("This is your current password: " + password);

            mailSender.send(message);
            return "Mail sent successfully";
        } catch (MailException e) {
            return "Error: Failed to send email.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // ------------------- Exams -------------------

    public String insertQuestion(List<Exam> questions) {
        if (questions == null || questions.isEmpty()) return "Error: Question list is empty.";
        examRepo.saveAll(questions);
        return "Successfully inserted";
    }

    public List<Exam> retreiveQuestions(String courseId) {
        try {
            List<Exam> questions = examRepo.findByCourseId(courseId);
            return questions == null ? Collections.emptyList() : questions;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ------------------- Student Courses -------------------

    public String addStudentCourse(StudentCourse studentCourse) {
        studentCourseRepo.save(studentCourse);
        return "Student course added successfully.";
    }

    public List<StudentCourse> getCoursesByUsername(String username) {
        try {
            List<StudentCourse> courses = studentCourseRepo.findByUsername(username);
            return courses == null ? Collections.emptyList() : courses;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String updatePercentage(String username, String courseId, double newPercentage) {
        try {
            List<StudentCourse> courses = studentCourseRepo.findByUsername(username);
            for (StudentCourse c : courses) {
                if (c.getCourseId().equals(courseId)) {
                    c.setPercentage(newPercentage);
                    studentCourseRepo.save(c);
                    return "Percentage updated successfully.";
                }
            }
            return "Course ID not found for this user.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<StudentCourse> getRegisteredCourses(String username) {
        return studentCourseRepo.findByUsername(username);
    }

    // ------------------- Notes -------------------

    public String insertNote(Notes note) {
        notesRepo.save(note);
        return "Note inserted successfully.";
    }

    public Notes getNoteByUsernameAndCourseId(String username, String courseId) {
        List<Notes> notes = notesRepo.findByUsernameAndCourseId(username, courseId);
        return notes.isEmpty() ? null : notes.get(0);
    }

    public String updateNoteByUsernameAndCourseId(String username, String courseId, String updatedContent) {
        List<Notes> notesList = notesRepo.findByUsernameAndCourseId(username, courseId);
        if (notesList.isEmpty()) return "No note found.";

        Notes note = notesList.get(0);
        note.setNote(updatedContent);
        notesRepo.save(note);
        return "Note updated successfully!";
    }

    // ------------------- Student Course Content -------------------

    public String insertContent(StudentCourseContent content) {
        contentRepo.save(content);
        return "Content inserted successfully!";
    }

    public List<StudentCourseContent> getStudentCourseContentByUsernameAndCourseId(String username, String courseId) {
        return contentRepo.findByUsernameAndCourseId(username, courseId);
    }

    public String updatePercentage(String username, String courseId, int module, double newPercentage) {
        List<StudentCourseContent> contentList = contentRepo.findByUsernameAndCourseId(username, courseId);
        for (StudentCourseContent content : contentList) {
            if (content.getModule() == module) {
                content.setPercentage(newPercentage);
                contentRepo.save(content);
                return "Percentage updated successfully!";
            }
        }
        return "Content not found for given module!";
    }

    // ------------------- Courses -------------------

    public String insertCourse(Courses course) {
        coursesRepo.save(course);
        return "Course inserted successfully!";
    }

    public List<Courses> getAllCourses() {
        return coursesRepo.findAll();
    }

    public List<Courses> getCoursesByInstructor(String username) {
        try {
            List<Courses> courses = coursesRepo.findByUsername(username);
            return courses == null ? Collections.emptyList() : courses;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Courses getCourseById(String courseId) {
        Optional<Courses> course = coursesRepo.findById(courseId);
        return course.orElse(null);
    }

    public String deleteCourseById(String courseId) {
        if (!coursesRepo.existsById(courseId)) return "Course not found.";
        coursesRepo.deleteById(courseId);
        return "Course deleted successfully!";
    }

    // ------------------- Course Content -------------------

    public String insertCourseContent(CourseContent courseContent) {
        courseContentRepo.save(courseContent);
        return "Course content inserted successfully!";
    }

    public CourseContent getCourseContentByCourseIdAndModule(String courseId, int module) {
        return courseContentRepo.findByCourseIdAndModule(courseId, module);
    }

    public List<CourseContent> getCourseContentByCourseId(String courseId) {
        try {
            return courseContentRepo.findByCourseId(courseId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
