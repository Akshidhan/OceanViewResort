package controller;

import dao.UserDao;
import dto.LoginRequestDto;
import exception.AuthException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.userModel;
import service.AuthService;
import util.JsonUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        this.authService =  new AuthService(new UserDao());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // "/login" or "/logout"
        if (path == null) {
            JsonUtil.notFound(resp, "Unknown auth route");
            return;
        }

        switch (path) {
            case "/login" -> login(req, resp);
            case "/logout" -> logout(req, resp);
            default -> JsonUtil.notFound(resp, "Unknown auth route: " + path);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // "/me"
        if ("/me".equals(path)) {
            handleMe(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            LoginRequestDto body = JsonUtil.read(req.getInputStream(), LoginRequestDto.class);
            userModel user = authService.authenticate(body.username, body.password);

            // Session fixation protection: invalidate old, create new
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();

            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            // session timeout
            session.setMaxInactiveInterval(60 * 30); // 30 minutes

            JsonUtil.ok(resp, Collections.singletonMap("message", "ok"));
        } catch (AuthException e) {
            JsonUtil.unauthorized(resp, e.getMessage());
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        JsonUtil.ok(resp, Collections.singletonMap("message", "logged_out"));
    }

    private void handleMe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null) {
            JsonUtil.unauthorized(resp, "Not logged in");
            return;
        }

        long userId = (long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "ok");
        payload.put("id",  userId);
        payload.put("username", username);
        JsonUtil.ok(resp, payload);
    }
}
