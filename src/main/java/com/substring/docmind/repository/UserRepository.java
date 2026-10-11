package com.substring.docmind.repository;

import com.substring.docmind.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmail(String email);

    Boolean existsByUsernameIgnoreCase(String username);
    Boolean existsByEmail(String email);
}
