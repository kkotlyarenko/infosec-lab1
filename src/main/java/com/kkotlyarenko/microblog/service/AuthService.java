package com.kkotlyarenko.microblog.service;

import com.kkotlyarenko.microblog.dto.AuthResponse;
import com.kkotlyarenko.microblog.dto.LoginRequest;
import com.kkotlyarenko.microblog.dto.RegisterRequest;
import com.kkotlyarenko.microblog.model.User;
import com.kkotlyarenko.microblog.repository.UserRepository;
import com.kkotlyarenko.microblog.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Аутентификация и регистрация пользователей.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Регистрирует пользователя, сохраняя только bcrypt-хэш пароля. */
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("username already taken");
        }
        String hash = passwordEncoder.encode(request.password());
        userRepository.save(new User(request.username(), hash, "USER"));
    }

    /** Проверяет учётные данные и при успехе выпускает JWT. */
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), request.password()));
        } catch (BadCredentialsException ex) {
            // Не раскрываем, что именно неверно — логин или пароль.
            throw new BadCredentialsException("invalid username or password");
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("invalid username or password"));

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, jwtService.getExpirationMs() / 1000, user.getUsername());
    }
}
