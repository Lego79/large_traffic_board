package com.megaboard.project.board.service.command;

import java.util.Objects;

public record BoardPostCommand(String title, String content, Long userId) {

    public BoardPostCommand {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
    }
}
