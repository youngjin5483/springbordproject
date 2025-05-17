package com.example.bordjroject.repository;

import com.example.bordjroject.Entity.login_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<login_Entity, Long> {
    Optional<login_Entity> findByUsername(String username);
}
