package com.example.Dating.service;

import com.example.Dating.entities.MessagePhotos;
import com.example.Dating.repository.MessagePhotosRepository;
import com.example.Dating.utils.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessagePhotoServiceImpl implements MessagePhotoService {
    private final MessagePhotosRepository messagePhotosRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public MessagePhotos save(MultipartFile file) {
        Map<String, Object> imageUrl = cloudinaryService.uploadFile(file, "/chat");
        String url = (String) imageUrl.get("url");

        MessagePhotos photo = MessagePhotos.builder()
                .imageUrl(url)
                .build();

        return messagePhotosRepository.save(photo);
    }
}
