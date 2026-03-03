package com.example.Dating.entities;



import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Master data representing an available interest/hobby.
 * Shared across all users.
 */
@Entity
@Table(name = "interests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interest {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}