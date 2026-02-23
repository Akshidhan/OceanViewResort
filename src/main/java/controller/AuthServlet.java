package controller;

import dto.LoginRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.JsonUtil;
import util.RequestUtil;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

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

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginRequest body;
        try {
            body = RequestUtil.readJson(req, LoginRequest.class);
        } catch (Exception e) {
            JsonUtil.badRequest(resp, "Invalid JSON body");
            return;
        }

        if (body.username == null || body.password == null) {
            JsonUtil.badRequest(resp, "username and password are required");
            return;
        }

        // Day-1 temporary auth (replace with MySQL + hashing)
        boolean ok = body.username.equals("admin") && body.password.equals("admin123");
        if (!ok) {
            JsonUtil.unauthorized(resp, "Invalid credentials");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userId", 1);

        JsonUtil.ok(resp, Map.of("message", "ok"));
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        JsonUtil.ok(resp, Map.of("message", "logged_out"));
    }
}