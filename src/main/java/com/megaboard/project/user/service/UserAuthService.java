package com.megaboard.project.user.service;

import com.megaboard.project.user.domain.User;
import com.megaboard.project.user.repository.UserRepository;
import com.megaboard.project.user.service.command.LoginCommand;
import com.megaboard.project.user.service.command.SignUpCommand;
import com.megaboard.project.user.service.dto.SessionUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserAuthService {

    private static final String ACTIVE = "Y";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SessionUser register(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalStateException("Email is already in use.");
        }
        User user = new User(
                command.username(),
                command.email(),
                passwordEncoder.encode(command.rawPassword())
        );
        User saved = userRepository.save(user);
        return toSessionUser(saved);
    }

    @Transactional(readOnly = true)
    public Optional<SessionUser> authenticate(LoginCommand command) {
        return userRepository.findByEmailAndUseYn(command.email(), ACTIVE)
                .filter(user -> passwordEncoder.matches(command.rawPassword(), user.getPasswordHash()))
                .map(this::toSessionUser);
    }

    private SessionUser toSessionUser(User user) {
        return new SessionUser(user.getId(), user.getUsername(), user.getEmail());
    }
}
