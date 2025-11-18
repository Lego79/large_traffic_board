package com.megaboard.project.board.service;

import com.megaboard.project.board.domain.Board;
import com.megaboard.project.board.repository.BoardRepository;
import com.megaboard.project.board.service.command.BoardPostCommand;
import com.megaboard.project.user.domain.User;
import com.megaboard.project.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BoardPostWriteService {

    private static final String ACTIVE = "Y";

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardPostWriteService(BoardRepository boardRepository,
                                 UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long create(BoardPostCommand command) {
        User writer = userRepository.findByIdAndUseYn(command.userId(), ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only active users can create posts."));

        Board board = Board.create(
                writer,
                command.title(),
                command.content()
        );
        return boardRepository.save(board).getId();
    }
}
