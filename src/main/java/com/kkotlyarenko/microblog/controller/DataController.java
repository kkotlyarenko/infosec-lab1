package com.kkotlyarenko.microblog.controller;

import com.kkotlyarenko.microblog.dto.PostRequest;
import com.kkotlyarenko.microblog.dto.PostResponse;
import com.kkotlyarenko.microblog.dto.UserResponse;
import com.kkotlyarenko.microblog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Защищённые методы доступа к данным.
 * Все маршруты требуют валидный JWT (см. SecurityConfig).
 */
@RestController
public class DataController {

    private final PostService postService;

    public DataController(PostService postService) {
        this.postService = postService;
    }

    /** Публичная проверка живости сервиса. */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    /** GET /api/data — список пользователей (только для аутентифицированных). */
    @GetMapping("/api/data")
    public ResponseEntity<List<UserResponse>> getData() {
        return ResponseEntity.ok(postService.listUsers());
    }

    /** GET /api/posts — список постов (только для аутентифицированных). */
    @GetMapping("/api/posts")
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(postService.listPosts());
    }

    /** POST /api/posts — создание поста текущим пользователем. */
    @PostMapping("/api/posts")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(request));
    }
}
