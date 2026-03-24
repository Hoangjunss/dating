package com.example.Dating.repository;


import com.example.Dating.entities.MessagePhotos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessagePhotosRepository  extends JpaRepository<MessagePhotos, UUID> {
}
