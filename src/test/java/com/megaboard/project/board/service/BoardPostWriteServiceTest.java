package com.megaboard.project.board.service;

import com.megaboard.project.board.repository.BoardRepository;
import com.megaboard.project.board.service.command.BoardPostCommand;
import com.megaboard.project.user.domain.User;
import com.megaboard.project.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BoardPostWriteService.class)
class BoardPostWriteServiceTest {

    @Autowired
    private BoardPostWriteService boardPostWriteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("Creates a post for the given active user")
    void createBoardPost() {
        User user = userRepository.save(new User("tester", "tester@example.com", "hashed"));
        BoardPostCommand command = new BoardPostCommand(
                "test title",
                "body text",
                user.getId()
        );

        Long id = boardPostWriteService.create(command);

        assertThat(id).isNotNull();
        assertThat(boardRepository.findById(id))
                .isPresent()
                .get()
                .satisfies(board -> {
                    assertThat(board.getUser().getId()).isEqualTo(user.getId());
                    assertThat(board.getTitle()).isEqualTo("test title");
                });
    }
}
