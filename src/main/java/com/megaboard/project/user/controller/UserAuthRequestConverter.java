package com.megaboard.project.user.controller;

import com.megaboard.project.user.controller.dto.LoginRequest;
import com.megaboard.project.user.controller.dto.SignUpRequest;
import com.megaboard.project.user.service.command.LoginCommand;
import com.megaboard.project.user.service.command.SignUpCommand;
import org.springframework.stereotype.Component;

@Component
public class UserAuthRequestConverter {

    public SignUpCommand toCommand(SignUpRequest request) {
        return new SignUpCommand(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
    }

    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(
                request.getEmail(),
                request.getPassword()
        );
    }
}
