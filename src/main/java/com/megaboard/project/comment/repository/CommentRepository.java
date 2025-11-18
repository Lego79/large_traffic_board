package com.megaboard.project.comment.repository;

import com.megaboard.project.comment.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByBoardIdAndUseYnOrderByCreatedAtAsc(Long boardId, String useYn);
}
