package com.megaboard.project.board.service.query;

import java.time.LocalDateTime;

public record BoardPostResponse(
        Long id,
        String title,
        String content,
        Long userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
