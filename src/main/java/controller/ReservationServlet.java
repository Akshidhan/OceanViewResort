package controller;

import dao.ReservationDao;
import dao.RoomDao;
import dto.ReservationPageDto;
import dto.ReservationRequestDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ReservationModel;
import service.ReservationService;
import util.JsonUtil;

import java.io.IOException;
import java.util.Collections;

/**
 * Reservation REST endpoints
 *
 *  POST   /api/reservations          – create
 *  GET    /api/reservations          – list (paginated, filtered)
 *  GET    /api/reservations/{id}     – get one
 *  PUT    /api/reservations/{id}     – update
 *  DELETE /api/reservations/{id}     – delete
 *
 * Query params for GET /api/reservations:
 *   page      (int,  default 1)
 *   pageSize  (int,  default 10, max 100)
 *   search    (string, searches guest_name / address / contact_number)
 *   roomId    (long,   exact match on room id)
 *   dateFrom  (yyyy-MM-dd, check_in >= dateFrom)
 *   dateTo    (yyyy-MM-dd, check_in <= dateTo)
 */
@WebServlet("/api/reservations/*")
public class ReservationServlet extends HttpServlet {

    private ReservationService service;

    @Override
    public void init() {
        this.service = new ReservationService(new ReservationDao(), new RoomDao());
    }

    // ── POST /api/reservations ──────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // null or "/"
        if (path != null && !path.equals("/")) {
            JsonUtil.notFound(resp, "Route not found");
            return;
        }
        try {
            ReservationRequestDto body = JsonUtil.read(req.getInputStream(), ReservationRequestDto.class);
            ReservationModel created = service.create(body);
            JsonUtil.created(resp, created);
        } catch (IllegalArgumentException e) {
            JsonUtil.badRequest(resp, e.getMessage());
        } catch (Exception e) {
            JsonUtil.serverError(resp, e.getMessage());
        }
    }

    // ── GET /api/reservations  OR  /api/reservations/{id} ──────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // null, "/", or "/{id}"

        if (path == null || path.equals("/")) {
            // List all with filters + pagination
            try {
                String search   = req.getParameter("search");
                Long roomId     = parseLongParam(req.getParameter("roomId"));
                String dateFrom = req.getParameter("dateFrom");
                String dateTo   = req.getParameter("dateTo");
                int page     = parseIntParam(req.getParameter("page"),     1);
                int pageSize = parseIntParam(req.getParameter("pageSize"), 10);

                ReservationPageDto result = service.getAll(search, roomId, dateFrom, dateTo, page, pageSize);
                JsonUtil.ok(resp, result);
            } catch (IllegalArgumentException e) {
                JsonUtil.badRequest(resp, e.getMessage());
            } catch (Exception e) {
                JsonUtil.serverError(resp, e.getMessage());
            }
        } else {
            // Get one by id
            Long id = parseId(path, resp);
            if (id == null) return;
            try {
                ReservationModel r = service.getById(id);
                JsonUtil.ok(resp, r);
            } catch (IllegalArgumentException e) {
                JsonUtil.notFound(resp, e.getMessage());
            } catch (Exception e) {
                JsonUtil.serverError(resp, e.getMessage());
            }
        }
    }

    // ── PUT /api/reservations/{id} ──────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Long id = parseId(path, resp);
        if (id == null) return;
        try {
            ReservationRequestDto body = JsonUtil.read(req.getInputStream(), ReservationRequestDto.class);
            ReservationModel updated = service.update(id, body);
            JsonUtil.ok(resp, updated);
        } catch (IllegalArgumentException e) {
            // distinguish not-found from bad-request by message prefix
            if (e.getMessage() != null && e.getMessage().startsWith("Reservation not found")) {
                JsonUtil.notFound(resp, e.getMessage());
            } else {
                JsonUtil.badRequest(resp, e.getMessage());
            }
        } catch (Exception e) {
            JsonUtil.serverError(resp, e.getMessage());
        }
    }

    // ── DELETE /api/reservations/{id} ──────────────────────────────────────
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

    /** Parses /{id} from pathInfo; sends 400 and returns null on failure. */
    private Long parseId(String path, HttpServletResponse resp) throws IOException {
        if (path == null || path.equals("/")) {
            JsonUtil.badRequest(resp, "Reservation id is required");
            return null;
        }
        try {
            return Long.parseLong(path.substring(1)); // strip leading "/"
        } catch (NumberFormatException e) {
            JsonUtil.badRequest(resp, "Invalid reservation id");
            return null;
        }
    }

    private int parseIntParam(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    private Long parseLongParam(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Long.parseLong(value.trim()); }
        catch (NumberFormatException e) { return null; }
    }
}

