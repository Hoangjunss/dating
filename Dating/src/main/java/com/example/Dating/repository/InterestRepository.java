package com.example.Dating.repository;

import com.example.Dating.entities.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterestRepository extends JpaRepository<Interest, UUID> {

    boolean existsByName(String name);
}