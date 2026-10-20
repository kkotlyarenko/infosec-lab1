package com.kkotlyarenko.microblog.service;

import com.kkotlyarenko.microblog.dto.PostRequest;
import com.kkotlyarenko.microblog.dto.PostResponse;
import com.kkotlyarenko.microblog.dto.UserResponse;
import com.kkotlyarenko.microblog.model.Post;
import com.kkotlyarenko.microblog.model.User;
import com.kkotlyarenko.microblog.repository.PostRepository;
import com.kkotlyarenko.microblog.repository.UserRepository;
import com.kkotlyarenko.microblog.util.HtmlSanitizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Бизнес-логика для защищённых методов данных.
 */
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /** Список всех постов. Все строки проходят output-encoding. */
    @Transactional(readOnly = true)
    public List<PostResponse> listPosts() {
        return postRepository.findFeed().stream()
                .map(view -> new PostResponse(
                        view.getId(),
                        HtmlSanitizer.escape(view.getTitle()),
                        HtmlSanitizer.escape(view.getContent()),
                        HtmlSanitizer.escape(view.getAuthor()),
                        view.getCreatedAt().toString()))
                .toList();
    }

    /** Создание поста текущим аутентифицированным пользователем. */
    @Transactional
    public PostResponse createPost(PostRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("authenticated user not found");
        }
        String username = authentication.getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("authenticated user not found"));

        Post saved = postRepository.save(new Post(request.title(), request.content(), author.getId()));
        return new PostResponse(
                saved.getId(),
                HtmlSanitizer.escape(saved.getTitle()),
                HtmlSanitizer.escape(saved.getContent()),
                HtmlSanitizer.escape(author.getUsername()),
                saved.getCreatedAt().toString());
    }

    /** Публичный список пользователей (без хэшей паролей). */
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        HtmlSanitizer.escape(u.getUsername()),
                        u.getRole(),
                        u.getCreatedAt().toString()))
                .toList();
    }
}
