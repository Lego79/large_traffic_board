package com.megaboard.project.board.controller;

import com.megaboard.project.board.controller.dto.BoardPostCreateRequest;
import com.megaboard.project.board.service.command.BoardPostCommand;
import org.springframework.stereotype.Component;

@Component
public class BoardPostRequestConverter {

    public BoardPostCommand toCommand(BoardPostCreateRequest request, Long userId) {
        return new BoardPostCommand(
                request.getTitle(),
                request.getContent(),
                userId
        );
    }
}
