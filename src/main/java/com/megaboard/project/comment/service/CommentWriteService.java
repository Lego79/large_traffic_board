package com.megaboard.project.comment.service;

import com.megaboard.project.board.domain.Board;
import com.megaboard.project.board.repository.BoardRepository;
import com.megaboard.project.comment.domain.Comment;
import com.megaboard.project.comment.repository.CommentRepository;
import com.megaboard.project.comment.service.command.CommentCreateCommand;
import com.megaboard.project.user.domain.User;
import com.megaboard.project.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentWriteService {

    private static final String ACTIVE = "Y";

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public CommentWriteService(CommentRepository commentRepository,
                               BoardRepository boardRepository,
                               UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long create(CommentCreateCommand command) {
        Board board = boardRepository.findByIdAndUseYn(command.boardId(), ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));
        User user = userRepository.findByIdAndUseYn(command.userId(), ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only active users can leave comments."));

        Comment comment = Comment.create(board, user, command.content());
        return commentRepository.save(comment).getId();
    }
}
