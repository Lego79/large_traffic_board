package com.megaboard.project.web;

import com.megaboard.project.user.service.dto.SessionUser;
import com.megaboard.project.user.support.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object attribute = session.getAttribute(SessionConst.LOGIN_USER);
        if (attribute instanceof SessionUser sessionUser) {
            return sessionUser;
        }
        return null;
    }
}
