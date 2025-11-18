package com.megaboard.project.comment.service.query;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long userId,
        String username,
        String content,
        LocalDateTime createdAt
) {
}
