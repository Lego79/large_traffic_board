package com.megaboard.project.comment.controller;

import com.megaboard.project.comment.controller.dto.CommentCreateRequest;
import com.megaboard.project.comment.service.command.CommentCreateCommand;
import org.springframework.stereotype.Component;

@Component
public class CommentRequestConverter {

    public CommentCreateCommand toCommand(Long boardId, Long userId, CommentCreateRequest request) {
        return new CommentCreateCommand(boardId, userId, request.getContent());
    }
}
