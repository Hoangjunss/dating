package com.example.Dating.service;

import com.example.Dating.entities.MessagePhotos;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface MessagePhotoService {
    MessagePhotos save(MultipartFile file);
}
