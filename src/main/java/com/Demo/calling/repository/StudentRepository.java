package com.Demo.calling.repository;

import com.Demo.calling.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByStudentId(String studentId);
    List<Student> findAllByOrderByStudentIdAsc();
    List<Student> findAllByOrderByScoreDesc();
}
