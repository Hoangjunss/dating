package com.example.Dating.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock private Cloudinary cloudinary;
    @Mock private Uploader uploader;
    @Mock private MultipartFile mockFile;

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        // We need to create CloudinaryService with mock Cloudinary
        // Since constructor uses real Cloudinary, we use reflection or create a test subclass
        cloudinaryService = new CloudinaryService("dummy", "dummy", "dummy");
        ReflectionTestUtils.setField(cloudinaryService, "cloudinary", cloudinary);
    }

    @Test
    void uploadFile_Success() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("url", "http://cloudinary.com/test.jpg"));
        when(mockFile.getBytes()).thenReturn(new byte[0]);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(1024L);

        Map<String, Object> result = cloudinaryService.uploadFile(mockFile, "test-folder");

        assertThat(result.get("url")).isEqualTo("http://cloudinary.com/test.jpg");
        assertThat(result.get("originalFileName")).isEqualTo("test.jpg");
    }

    @Test
    void uploadFile_IOException_ThrowsRuntimeException() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenThrow(new IOException("Upload failed"));
        when(mockFile.getBytes()).thenThrow(new IOException("File error"));

        assertThatThrownBy(() -> cloudinaryService.uploadFile(mockFile, "folder"))
                .isInstanceOf(RuntimeException.class);
    }
}