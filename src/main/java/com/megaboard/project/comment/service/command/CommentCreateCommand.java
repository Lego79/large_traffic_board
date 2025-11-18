package com.megaboard.project.comment.service.command;

import java.util.Objects;

public record CommentCreateCommand(Long boardId, Long userId, String content) {

    public CommentCreateCommand {
        Objects.requireNonNull(boardId, "boardId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(content, "content must not be null");
    }
}
