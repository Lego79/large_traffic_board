package com.megaboard.project.user.service.dto;

import java.util.Objects;

public record SessionUser(Long id, String username, String email) {

    public SessionUser {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(email, "email must not be null");
    }
}
