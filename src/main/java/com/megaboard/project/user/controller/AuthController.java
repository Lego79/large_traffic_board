package com.megaboard.project.user.controller;

import com.megaboard.project.user.controller.dto.LoginRequest;
import com.megaboard.project.user.controller.dto.SignUpRequest;
import com.megaboard.project.user.service.UserAuthService;
import com.megaboard.project.user.service.dto.SessionUser;
import com.megaboard.project.user.support.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final String SIGNUP_VIEW = "auth/signup";
    private static final String LOGIN_VIEW = "auth/login";

    private final UserAuthService userAuthService;
    private final UserAuthRequestConverter requestConverter;

    public AuthController(UserAuthService userAuthService,
                          UserAuthRequestConverter requestConverter) {
        this.userAuthService = userAuthService;
        this.requestConverter = requestConverter;
    }

    @GetMapping(value ="/signup")
    public String renderSignUpForm(Model model, HttpServletRequest request) {
        if (!model.containsAttribute("signUpRequest")) {

            var value = new SignUpRequest();
            model.addAttribute("signUpRequest", new SignUpRequest());
        }
        redirectLoggedInUser(request, model);
        return SIGNUP_VIEW;
    }

    @PostMapping("/signup")
    public String signUp(
            @Valid @ModelAttribute("signUpRequest") SignUpRequest request,
            BindingResult bindingResult,
            HttpSession session
    ) {
        if (bindingResult.hasErrors()) {
            return SIGNUP_VIEW;
        }

        try {
            SessionUser sessionUser = userAuthService.register(requestConverter.toCommand(request));
            session.setAttribute(SessionConst.LOGIN_USER, sessionUser);
            return "redirect:/boards";
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("email", "duplicateEmail", e.getMessage());
            return SIGNUP_VIEW;
        }
    }

    @GetMapping("/login")
    public String renderLoginForm(
            @RequestParam(value = "redirect", required = false) String redirect,
            Model model,
            HttpServletRequest request
    ) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        String sanitizedRedirect = sanitizeRedirect(redirect);
        model.addAttribute("redirectTarget", sanitizedRedirect);

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConst.LOGIN_USER) != null && sanitizedRedirect == null) {
            return "redirect:/boards";
        }
        return LOGIN_VIEW;
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpSession session,
            Model model
    ) {
        String sanitizedRedirect = sanitizeRedirect(redirect);
        model.addAttribute("redirectTarget", sanitizedRedirect);

        if (bindingResult.hasErrors()) {
            return LOGIN_VIEW;
        }

        Optional<SessionUser> sessionUser = userAuthService.authenticate(requestConverter.toCommand(request));
        if (sessionUser.isEmpty()) {
            bindingResult.reject("invalidCredentials", "Email or password is incorrect.");
            return LOGIN_VIEW;
        }

        session.setAttribute(SessionConst.LOGIN_USER, sessionUser.get());
        return "redirect:" + (sanitizedRedirect != null ? sanitizedRedirect : "/boards");
    }

    @PostMapping("/logout")
    public String logout(
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        String sanitizedRedirect = sanitizeRedirect(redirect);
        return "redirect:" + (sanitizedRedirect != null ? sanitizedRedirect : "/boards");
    }

    private void redirectLoggedInUser(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConst.LOGIN_USER) != null) {
            model.addAttribute("alreadyLoggedIn", true);
        }
    }

    private String sanitizeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) {
            return null;
        }
        if (!redirect.startsWith("/")) {
            return null;
        }
        return redirect;
    }
}
