package com.example.StudentSkillSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Helper method: Validate scores between 0 and 100 (Rule 8)
    private void validateScores(double programmingScore, double mathematicsScore) {
        if (programmingScore < 0 || programmingScore > 100 ||
                mathematicsScore < 0 || mathematicsScore > 100) {
            throw new IllegalArgumentException("Scores must be between 0 and 100.");
        }
    }

    // Q3.1: Add a student (with Duplicate ID & Score Validation - Rule 8)
    public Student addStudent(Student student) {
        if (student.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (studentRepository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException("Duplicate Student ID: " + student.getStudentId() + " already exists.");
        }
        validateScores(student.getProgrammingScore(), student.getMathematicsScore());
        return studentRepository.save(student);
    }

    // Q3.2: Retrieve all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Q2.4 & Q3.3: Search student by ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // Q2.4: Search student by Email
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    // Q2.4: Search students by Course
    public List<Student> getStudentsByCourse(String courseName) {
        return studentRepository.findByCourseNameIgnoreCase(courseName);
    }

    // Q3.4: Update student scores and skills (with Score Validation - Rule 8)
    public Student updateScoresAndSkills(Long id, double programmingScore, double mathematicsScore, List<String> technicalSkills) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + id));

        validateScores(programmingScore, mathematicsScore);

        existing.setProgrammingScore(programmingScore);
        existing.setMathematicsScore(mathematicsScore);
        if (technicalSkills != null) {
            existing.setTechnicalSkills(technicalSkills);
        }
        return studentRepository.save(existing);
    }

    // Q3.5: Delete a student record
    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Q2.5 & Q3.6: Generate Merit List sorted by average score in descending order
    public List<Student> getMeritList() {
        List<Student> students = new ArrayList<>(studentRepository.findAll());
        students.sort(Comparator.comparingDouble(Student::getAverageScore).reversed());
        return students;
    }

    // Q2.6: Identify students who have the same technical skills (Using Map & Set Collections)
    public Map<String, List<String>> getStudentsGroupedByCommonSkills() {
        List<Student> allStudents = studentRepository.findAll();
        Map<String, List<String>> skillToStudentsMap = new LinkedHashMap<>();

        for (Student s : allStudents) {
            if (s.getTechnicalSkills() != null) {
                // Set removes duplicate skills within the same student
                Set<String> uniqueSkills = new HashSet<>(s.getTechnicalSkills());
                for (String skill : uniqueSkills) {
                    String normalizedSkill = skill.trim();
                    skillToStudentsMap
                            .computeIfAbsent(normalizedSkill, k -> new ArrayList<>())
                            .add(s.getStudentName() + " (ID: " + s.getStudentId() + ")");
                }
            }
        }

        // Keep only skills shared by 2 or more students
        Map<String, List<String>> commonSkills = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : skillToStudentsMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                commonSkills.put(entry.getKey(), entry.getValue());
            }
        }
        return commonSkills;
    }

    // Q2.7: Students needing improvement in programming (< 50) but Mathematics >= 75
    public List<Student> getWeakProgrammingStrongMathStudents() {
        return studentRepository.findAll().stream()
                .filter(s -> s.getProgrammingScore() < 50.0 && s.getMathematicsScore() >= 75.0)
                .collect(Collectors.toList());
    }
}