package com.megaboard.project.user.service.command;

import java.util.Objects;

public record SignUpCommand(String username, String email, String rawPassword) {

    public SignUpCommand {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
    }
}
