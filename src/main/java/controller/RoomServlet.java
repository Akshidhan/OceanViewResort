package controller;

import dao.RoomDao;
import dto.RoomPageDto;
import dto.RoomRequestDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.RoomModel;
import service.RoomService;
import util.JsonUtil;

import java.io.IOException;
import java.util.Collections;

/**
 * Room REST endpoints
 *
 *  POST   /api/rooms          – create
 *  GET    /api/rooms          – list (paginated, filtered)
 *  GET    /api/rooms/{id}     – get one
 *  PUT    /api/rooms/{id}     – update
 *  DELETE /api/rooms/{id}     – delete
 *
 * Query params for GET /api/rooms:
 *   page        (int,  default 1)
 *   pageSize    (int,  default 10, max 100)
 *   search      (string, searches room name)
 *   minCapacity (int,  rooms with capacity >= value)
 */
@WebServlet("/api/rooms/*")
public class RoomServlet extends HttpServlet {

    private RoomService service;

    @Override
    public void init() {
        this.service = new RoomService(new RoomDao());
    }

    // ── POST /api/rooms ─────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path != null && !path.equals("/")) {
            JsonUtil.notFound(resp, "Route not found");
            return;
        }
        try {
            RoomRequestDto body = JsonUtil.read(req.getInputStream(), RoomRequestDto.class);
            RoomModel created = service.create(body);
            JsonUtil.created(resp, created);
        } catch (IllegalArgumentException e) {
            JsonUtil.badRequest(resp, e.getMessage());
        } catch (Exception e) {
            JsonUtil.serverError(resp, e.getMessage());
        }
    }

    // ── GET /api/rooms  OR  /api/rooms/{id} ─────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            // List all with filters + pagination
            try {
                String search      = req.getParameter("search");
                Integer minCapacity = parseIntegerParam(req.getParameter("minCapacity"));
                int page     = parseIntParam(req.getParameter("page"),     1);
                int pageSize = parseIntParam(req.getParameter("pageSize"), 10);

                RoomPageDto result = service.getAll(search, minCapacity, page, pageSize);
                JsonUtil.ok(resp, result);
            } catch (Exception e) {
                JsonUtil.serverError(resp, e.getMessage());
            }
        } else {
            // Get one by id
            Long id = parseId(path, resp);
            if (id == null) return;
            try {
                RoomModel r = service.getById(id);
                JsonUtil.ok(resp, r);
            } catch (IllegalArgumentException e) {
                JsonUtil.notFound(resp, e.getMessage());
            } catch (Exception e) {
                JsonUtil.serverError(resp, e.getMessage());
            }
        }
    }

    // ── PUT /api/rooms/{id} ─────────────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Long id = parseId(path, resp);
        if (id == null) return;
        try {
            RoomRequestDto body = JsonUtil.read(req.getInputStream(), RoomRequestDto.class);
            RoomModel updated = service.update(id, body);
            JsonUtil.ok(resp, updated);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("Room not found")) {
                JsonUtil.notFound(resp, e.getMessage());
            } else {
                JsonUtil.badRequest(resp, e.getMessage());
            }
        } catch (Exception e) {
            JsonUtil.serverError(resp, e.getMessage());
        }
    }

    // ── DELETE /api/rooms/{id} ──────────────────────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Long id = parseId(path, resp);
        if (id == null) return;
        try {
            service.delete(id);
            JsonUtil.ok(resp, Collections.singletonMap("message", "deleted"));
        } catch (IllegalArgumentException e) {
            JsonUtil.notFound(resp, e.getMessage());
        } catch (Exception e) {
            JsonUtil.serverError(resp, e.getMessage());
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private Long parseId(String path, HttpServletResponse resp) throws IOException {
        if (path == null || path.equals("/")) {
            JsonUtil.badRequest(resp, "Room id is required");
            return null;
        }
        try {
            return Long.parseLong(path.substring(1));
        } catch (NumberFormatException e) {
            JsonUtil.badRequest(resp, "Invalid room id");
            return null;
        }
    }

    private int parseIntParam(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    private Integer parseIntegerParam(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}




