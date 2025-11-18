package com.megaboard.project.board.service.query;

import com.megaboard.project.board.repository.BoardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BoardPostReadService {

    private static final String ACTIVE = "Y";

    private final BoardRepository boardRepository;

    public BoardPostReadService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<BoardPostResponse> findRecentPosts() {
        return boardRepository.findTop20ByUseYnOrderByCreatedAtDesc(ACTIVE)
                .stream()
                .map(board -> new BoardPostResponse(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        board.getUser().getId(),
                        board.getUser().getUsername(),
                        board.getCreatedAt(),
                        board.getUpdatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public BoardPostResponse findById(Long id) {
        return boardRepository.findByIdAndUseYn(id, ACTIVE)
                .map(board -> new BoardPostResponse(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        board.getUser().getId(),
                        board.getUser().getUsername(),
                        board.getCreatedAt(),
                        board.getUpdatedAt()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }
}
