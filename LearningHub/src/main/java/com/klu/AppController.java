package com.klu;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klu.entity.*;
import com.klu.service.Authenti;
import com.klu.service.PasswordSender;
import com.klu.service.Service;

@CrossOrigin(origins = "*")
@RestController
public class AppController {

    @Autowired
    private Service service;

    @GetMapping("/check")
    public String fun1() {
        return "Welcome to Learning Hubb";
    }

    @PostMapping("/register")
    public String fun2(@RequestBody User user) {
        Cryptography cryp = new Cryptography();
        user.setPassword(cryp.encryptData(user.getPassword()));
        return service.insertUser(user);
    }

    @PostMapping("/verifyUser")
    public Authenti check(@RequestBody LoginDetails log) {
        return service.verifyUser(log);
    }

    @PostMapping("/sendPassword")
    public String sendPassword(@RequestBody PasswordSender p) {
        return service.sendPassword(p.getPassword());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }
        return ResponseEntity.ok(service.sendPassword(username));
    }

    // Insert questions
    @PostMapping("/questions")
    public String insertQuestions(@RequestBody List<Exam> questions) {
        return service.insertQuestion(questions);
    }

    // Retrieve questions by courseId
    @GetMapping("/questions/{courseId}")
    public List<Exam> retrieveQuestions(@PathVariable String courseId) {
        return service.retreiveQuestions(courseId);
    }

    // Add student course
    @PostMapping("/student-course")
    public String addStudentCourse(@RequestBody StudentCourse studentCourse) {
        return service.addStudentCourse(studentCourse);
    }

    // Get student courses by username
    @GetMapping("/student-courses/{username}")
    public List<StudentCourse> getCoursesByUsername(@PathVariable String username) {
        return service.getCoursesByUsername(username);
    }

    // Update student course percentage
    @PutMapping("/student-course/update-percentage")
    public String updateStudentCoursePercentage(@RequestParam String username, @RequestParam String courseId,
                                                @RequestParam double newPercentage) {
        return service.updatePercentage(username, courseId, newPercentage);
    }

    @PostMapping("/notes")
    public String insertNote(@RequestBody Notes note) {
        return service.insertNote(note);
    }

    @GetMapping("/notes")
    public Notes getNote(@RequestParam String username, @RequestParam String courseId) {
        return service.getNoteByUsernameAndCourseId(username, courseId);
    }

    @PutMapping("/notes/update")
    public String updateNote(@RequestParam String username, @RequestParam String courseId,
                             @RequestParam String updatedContent) {
        return service.updateNoteByUsernameAndCourseId(username, courseId, updatedContent);
    }

    @PostMapping("/student-course-content")
    public String insertContent(@RequestBody StudentCourseContent content) {
        return service.insertContent(content);
    }

    // ✅ Corrected method: filter content by username AND courseId
    @GetMapping("/student-course-content")
    public List<StudentCourseContent> getStudentCourseContent(
            @RequestParam String username,
            @RequestParam String courseId) {
        return service.getStudentCourseContentByUsernameAndCourseId(username, courseId);
    }

    @PutMapping("/student-course-content/update-percentage")
    public String updateContentPercentage(@RequestParam String username, @RequestParam String courseId,
                                          @RequestParam int module, @RequestParam double newPercentage) {
        return service.updatePercentage(username, courseId, module, newPercentage);
    }

    @PostMapping("/courses")
    public String insertCourse(@RequestBody Courses course) {
        return service.insertCourse(course);
    }

    @GetMapping("/courses")
    public List<Courses> getCoursesByInstructor(@RequestParam(required = false) String username) {
        if (username != null) {
            return service.getCoursesByInstructor(username);
        }
        return service.getAllCourses();
    }
    

    @GetMapping("/courses/{courseId}")
    public Courses getCourseById(@PathVariable String courseId) {
        return service.getCourseById(courseId);
    }
    @GetMapping("/all-courses")
    public List<Courses> getAllCourses() {
        return service.getAllCourses();
    }


    @DeleteMapping("/courses/{courseId}")
    public String deleteCourse(@PathVariable String courseId) {
        return service.deleteCourseById(courseId);
    }

    @PostMapping("/course-content")
    public String insertCourseContent(@RequestBody CourseContent courseContent) {
        return service.insertCourseContent(courseContent);
    }

    @GetMapping("/course-content")
    public CourseContent getCourseContent(@RequestParam String courseId, @RequestParam int module) {
        return service.getCourseContentByCourseIdAndModule(courseId, module);
    }

    @GetMapping("/registered-courses")
    public List<StudentCourse> getRegisteredCourses(@RequestParam String username) {
        return service.getRegisteredCourses(username);
    }

}
