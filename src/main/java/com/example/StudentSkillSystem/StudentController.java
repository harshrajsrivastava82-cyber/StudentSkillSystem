package com.example.StudentSkillSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 1. Add a student -> POST /api/students (Q3.1 + Q2.8 Duplicate ID & Score Validation)
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student saved = studentService.addStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Retrieve all students -> GET /api/students (Q3.2)
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // 3. Search for a student by ID -> GET /api/students/{id} (Q2.4 & Q3.3)
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Student not found with ID: " + id)));
    }

    // Search by Email -> GET /api/students/email/{email} (Q2.4)
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getStudentByEmail(@PathVariable String email) {
        return studentService.getStudentByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Student not found with email: " + email)));
    }

    // Search by Course -> GET /api/students/course/{courseName} (Q2.4)
    @GetMapping("/course/{courseName}")
    public List<Student> getStudentsByCourse(@PathVariable String courseName) {
        return studentService.getStudentsByCourse(courseName);
    }

    // 4. Update student scores and skills -> PUT /api/students/{id} (Q3.4 + Q2.8 Validation)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateScoresAndSkills(@PathVariable Long id, @RequestBody Student updatedData) {
        try {
            Student updated = studentService.updateScoresAndSkills(
                    id,
                    updatedData.getProgrammingScore(),
                    updatedData.getMathematicsScore(),
                    updatedData.getTechnicalSkills()
            );
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Delete a student record -> DELETE /api/students/{id} (Q3.5)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        if (studentService.deleteStudent(id)) {
            return ResponseEntity.ok(Map.of("message", "Student with ID " + id + " deleted successfully."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Student not found with ID: " + id));
    }

    // 6. Display the merit list -> GET /api/students/merit-list (Q2.5 & Q3.6)
    @GetMapping("/merit-list")
    public List<Student> getMeritList() {
        return studentService.getMeritList();
    }

    // Extra Q2.6: Students with the same technical skills -> GET /api/students/common-skills
    @GetMapping("/common-skills")
    public Map<String, List<String>> getStudentsWithSameSkills() {
        return studentService.getStudentsGroupedByCommonSkills();
    }

    // Extra Q2.7: Programming < 50 AND Mathematics >= 75 -> GET /api/students/improve-programming
    @GetMapping("/improve-programming")
    public List<Student> getStudentsNeedingProgrammingImprovement() {
        return studentService.getWeakProgrammingStrongMathStudents();
    }
}