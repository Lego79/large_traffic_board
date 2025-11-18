package com.megaboard.project.board.controller;

import com.megaboard.project.board.controller.dto.BoardPostCreateRequest;
import com.megaboard.project.board.service.BoardPostWriteService;
import com.megaboard.project.board.service.query.BoardPostReadService;
import com.megaboard.project.comment.controller.dto.CommentCreateRequest;
import com.megaboard.project.comment.service.query.CommentReadService;
import com.megaboard.project.user.service.dto.SessionUser;
import com.megaboard.project.user.support.SessionConst;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/boards")
public class BoardPostController {

    private static final String LIST_VIEW = "boards/list";
    private static final String FORM_VIEW = "boards/form";
    private static final String DETAIL_VIEW = "boards/detail";

    private final BoardPostWriteService boardPostWriteService;
    private final BoardPostRequestConverter requestConverter;
    private final BoardPostReadService boardPostReadService;
    private final CommentReadService commentReadService;

    public BoardPostController(BoardPostWriteService boardPostWriteService,
                               BoardPostRequestConverter requestConverter,
                               BoardPostReadService boardPostReadService,
                               CommentReadService commentReadService) {
        this.boardPostWriteService = boardPostWriteService;
        this.requestConverter = requestConverter;
        this.boardPostReadService = boardPostReadService;
        this.commentReadService = commentReadService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("boardPosts", boardPostReadService.findRecentPosts());
        return LIST_VIEW;
    }

    @GetMapping("/{boardId}")
    public String detail(@PathVariable Long boardId, Model model) {
        model.addAttribute("boardPost", boardPostReadService.findById(boardId));
        model.addAttribute("comments", commentReadService.findByBoardId(boardId));
        if (!model.containsAttribute("commentCreateRequest")) {
            model.addAttribute("commentCreateRequest", new CommentCreateRequest());
        }
        return DETAIL_VIEW;
    }

    @GetMapping("/new")
    public String renderWriteForm(Model model, HttpSession session) {
        if (getSessionUser(session) == null) {
            return "redirect:/auth/login?redirect=/boards/new";
        }
        if (!model.containsAttribute("boardPostCreateRequest")) {
            model.addAttribute("boardPostCreateRequest", new BoardPostCreateRequest());
        }
        return FORM_VIEW;
    }

    @PostMapping
    public String submit(
            @Valid @ModelAttribute("boardPostCreateRequest") BoardPostCreateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
        SessionUser currentUser = getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/auth/login?redirect=/boards/new";
        }
        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }

        Long createdId = boardPostWriteService.create(requestConverter.toCommand(request, currentUser.id()));
        redirectAttributes.addFlashAttribute("boardCreated", true);
        redirectAttributes.addFlashAttribute("boardPostId", createdId);
        return "redirect:/boards";
    }

    private SessionUser getSessionUser(HttpSession session) {
        Object attribute = session.getAttribute(SessionConst.LOGIN_USER);
        if (attribute instanceof SessionUser sessionUser) {
            return sessionUser;
        }
        return null;
    }
}
