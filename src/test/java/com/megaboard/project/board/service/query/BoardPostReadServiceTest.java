package com.megaboard.project.board.service.query;

import com.megaboard.project.board.domain.Board;
import com.megaboard.project.board.repository.BoardRepository;
import com.megaboard.project.user.domain.User;
import com.megaboard.project.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BoardPostReadService.class)
class BoardPostReadServiceTest {

    @Autowired
    private BoardPostReadService boardPostReadService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Retrieves up to 20 posts ordered by newest first")
    void findRecentPosts() {
        User user = userRepository.save(new User("tester", "tester@example.com", "hashed"));

        IntStream.range(0, 25).forEach(i ->
                boardRepository.save(Board.create(
                        user,
                        "title " + i,
                        "content " + i
                ))
        );

        List<BoardPostResponse> responses = boardPostReadService.findRecentPosts();

        assertThat(responses).hasSize(20);
        assertThat(responses).isSortedAccordingTo(
                (a, b) -> b.createdAt().compareTo(a.createdAt())
        );
        assertThat(responses.get(0).username()).isEqualTo("tester");
    }

    @Test
    @DisplayName("Reads a single post by identifier")
    void findById() {
        User user = userRepository.save(new User("writer", "writer@example.com", "hashed"));
        Board saved = boardRepository.save(Board.create(
                user,
                "single title",
                "content"
        ));

        BoardPostResponse response = boardPostReadService.findById(saved.getId());

        assertThat(response.id()).isEqualTo(saved.getId());
        assertThat(response.title()).isEqualTo("single title");
        assertThat(response.username()).isEqualTo("writer");
    }
}
