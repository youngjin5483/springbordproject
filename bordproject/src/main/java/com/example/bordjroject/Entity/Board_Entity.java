package com.example.bordjroject.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Data
@NoArgsConstructor
public class Board_Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

    private String username;

    private String filename;

    @Column(nullable = false)
    private String createdAt = java.time.LocalDateTime.now().toString();

    @PrePersist
    public void prePersist() {
        this.createdAt = String.valueOf(LocalDateTime.now());
    }

}
