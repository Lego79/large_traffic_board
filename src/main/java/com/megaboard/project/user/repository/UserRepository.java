package com.megaboard.project.user.repository;

import com.megaboard.project.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndUseYn(Long id, String useYn);

    Optional<User> findByEmailAndUseYn(String email, String useYn);

    boolean existsByEmail(String email);
}
