package com.example.demo.repository;

import com.example.demo.domain.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    Optional<VerificationCode> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email); //del code

    List<VerificationCode> findByEmail(String email);
}
