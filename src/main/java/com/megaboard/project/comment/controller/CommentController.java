package com.megaboard.project.comment.controller;

import com.megaboard.project.board.service.query.BoardPostReadService;
import com.megaboard.project.comment.controller.dto.CommentCreateRequest;
import com.megaboard.project.comment.service.CommentWriteService;
import com.megaboard.project.comment.service.query.CommentReadService;
import com.megaboard.project.user.service.dto.SessionUser;
import com.megaboard.project.user.support.SessionConst;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/boards/{boardId}/comments")
public class CommentController {

    private final CommentWriteService commentWriteService;
    private final CommentRequestConverter commentRequestConverter;
    private final BoardPostReadService boardPostReadService;
    private final CommentReadService commentReadService;

    public CommentController(CommentWriteService commentWriteService,
                             CommentRequestConverter commentRequestConverter,
                             BoardPostReadService boardPostReadService,
                             CommentReadService commentReadService) {
        this.commentWriteService = commentWriteService;
        this.commentRequestConverter = commentRequestConverter;
        this.boardPostReadService = boardPostReadService;
        this.commentReadService = commentReadService;
    }

    @PostMapping
    public String create(
            @PathVariable Long boardId,
            @Valid @ModelAttribute("commentCreateRequest") CommentCreateRequest request,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        SessionUser currentUser = getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/auth/login?redirect=/boards/" + boardId;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("boardPost", boardPostReadService.findById(boardId));
            model.addAttribute("comments", commentReadService.findByBoardId(boardId));
            return "boards/detail";
        }

        commentWriteService.create(
                commentRequestConverter.toCommand(boardId, currentUser.id(), request)
        );
        return "redirect:/boards/" + boardId;
    }

    private SessionUser getSessionUser(HttpSession session) {
        Object attribute = session.getAttribute(SessionConst.LOGIN_USER);
        if (attribute instanceof SessionUser sessionUser) {
            return sessionUser;
        }
        return null;
    }
}
