package com.example.Dating.service;

import com.example.Dating.entities.MessagePhotos;
import com.example.Dating.repository.MessagePhotosRepository;
import com.example.Dating.service.impl.MessagePhotoServiceImpl;
import com.example.Dating.utils.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagePhotoServiceImplTest {

    @Mock private MessagePhotosRepository messagePhotosRepository;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks
    private MessagePhotoServiceImpl messagePhotoService;

    private MultipartFile mockFile;
    private MessagePhotos messagePhotos;

    @BeforeEach
    void setUp() {
        mockFile = mock(MultipartFile.class);
        messagePhotos = MessagePhotos.builder()
                .id(UUID.randomUUID())
                .imageUrl("http://cloudinary.com/msg_photo.jpg")
                .build();
    }

    @Test
    void save_ShouldUploadAndSave() {
        when(cloudinaryService.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn(Map.of("url", "http://cloudinary.com/msg_photo.jpg"));
        when(messagePhotosRepository.save(any(MessagePhotos.class))).thenReturn(messagePhotos);

        MessagePhotos result = messagePhotoService.save(mockFile);

        assertThat(result.getImageUrl()).isEqualTo("http://cloudinary.com/msg_photo.jpg");
        verify(cloudinaryService).uploadFile(mockFile, "/chat");
        verify(messagePhotosRepository).save(any(MessagePhotos.class));
    }
}