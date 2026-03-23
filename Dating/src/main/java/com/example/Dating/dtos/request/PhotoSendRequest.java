package com.example.Dating.dtos.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoSendRequest  {
    private UUID conversationId;
    private UUID senderId;
    private MultipartFile photo;
}
