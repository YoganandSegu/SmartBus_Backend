package com.smartbus.repository;

import com.smartbus.entity.Student;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Student s where s.studentId = :studentId")
    Optional<Student> findByStudentIdForUpdate(@Param("studentId") String studentId);
    Optional<Student> findByUserUsername(String username);
    boolean existsByStudentId(String studentId);
    boolean existsByEmail(String email);
}
