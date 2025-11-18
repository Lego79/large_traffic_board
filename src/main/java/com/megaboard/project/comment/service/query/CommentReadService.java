package com.megaboard.project.comment.service.query;

import com.megaboard.project.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentReadService {

    private static final String ACTIVE = "Y";

    private final CommentRepository commentRepository;

    public CommentReadService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdAndUseYnOrderByCreatedAtAsc(boardId, ACTIVE)
                .stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        comment.getContent(),
                        comment.getCreatedAt()
                ))
                .toList();
    }
}
