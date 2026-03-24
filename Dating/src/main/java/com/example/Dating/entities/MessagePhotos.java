package com.example.Dating.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "message_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessagePhotos {
    @Id
    @GeneratedValue
    private UUID id;

    private String imageUrl;

    private String publicId;
}
