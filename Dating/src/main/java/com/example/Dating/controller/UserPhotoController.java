package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.service.UserPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class UserPhotoController {

    private final UserPhotoService service;

    @PostMapping
    public UserPhotoResponse create(@RequestBody UserPhotoCreateRequest r) {
        return service.create(r);
    }

    @GetMapping("/user/{userId}")
    public List<UserPhotoResponse> getByUser(@PathVariable UUID userId) {
        return service.getByUser(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}