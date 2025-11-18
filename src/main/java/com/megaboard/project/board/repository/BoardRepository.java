package com.megaboard.project.board.repository;

import com.megaboard.project.board.domain.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = "user")
    List<Board> findTop20ByUseYnOrderByCreatedAtDesc(String useYn);

    @EntityGraph(attributePaths = {"user"})
    Optional<Board> findByIdAndUseYn(Long id, String useYn);
}
