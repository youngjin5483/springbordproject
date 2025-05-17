package com.example.bordjroject.repository;

import com.example.bordjroject.Entity.Board_Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BordRepository extends JpaRepository<Board_Entity, Integer> {
    List<Board_Entity> findByUsername(String username);
}
