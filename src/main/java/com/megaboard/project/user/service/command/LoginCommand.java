package com.megaboard.project.user.service.command;

import java.util.Objects;

public record LoginCommand(String email, String rawPassword) {

    public LoginCommand {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
    }
}
