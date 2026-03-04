package controller;

import dao.BillDao;
import dao.ReservationDao;
import dto.BillPageDto;
import dto.BillRequestDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BillModel;
import service.BillService;
import util.JsonUtil;

import java.io.IOException;
import java.util.Collections;

/**
 * Bill REST endpoints
 *
 *  POST   /api/bills          – create a bill for a reservation
 *  GET    /api/bills          – list all bills (paginated, optionally filtered by reservationId)
 *  GET    /api/bills/{id}     – get one bill by id
 *  DELETE /api/bills/{id}     – delete a bill
 *
 *  Update is intentionally NOT supported (bills are immutable once created).
 *
 * Query params for GET /api/bills:
 *   reservationId  (long,  filter bills for a specific reservation)
 *   page           (int,   default 1)
 *   pageSize       (int,   default 10, max 100)
 */
@WebServlet("/api/bills/*")
public class BillServlet extends HttpServlet {

    private BillService service;

    @Override
    public void init() {
        this.service = new BillService(new BillDao(), new ReservationDao());
    }

    // ── POST /api/bills ──────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path != null && !path.equals("/")) {
            JsonUtil.notFound(resp, "Route not found");
            return;
        }
        try {
            BillRequestDto body = JsonUtil.read(req.getInputStream(), BillRequestDto.class);
            BillModel created = service.create(body);
            JsonUtil.created(resp, created);
        } catch (IllegalArgumentException e) {
            JsonUtil.badRequest(resp, e.getMessage());
        } catch (Exception e) {
            JsonUtil.serverError(resp, e.getMessage());
        }
    }

    // ── GET /api/bills  OR  /api/bills/{id} ─────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            // List all with optional filter + pagination
            try {
                Long reservationId = parseLongParam(req.getParameter("reservationId"));
                int page     = parseIntParam(req.getParameter("page"),     1);
                int pageSize = parseIntParam(req.getParameter("pageSize"), 10);

                BillPageDto result = service.getAll(reservationId, page, pageSize);
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
                BillModel bill = service.getById(id);
                JsonUtil.ok(resp, bill);
            } catch (IllegalArgumentException e) {
                JsonUtil.notFound(resp, e.getMessage());
            } catch (Exception e) {
                JsonUtil.serverError(resp, e.getMessage());
            }
        }
    }

    // ── DELETE /api/bills/{id} ───────────────────────────────────────────────
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

    // ── PUT is intentionally not supported – bills are immutable ─────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonUtil.methodNotAllowed(resp);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Long parseId(String path, HttpServletResponse resp) throws IOException {
        if (path == null || path.equals("/")) {
            JsonUtil.badRequest(resp, "Bill id is required");
            return null;
        }
        try {
            return Long.parseLong(path.substring(1));
        } catch (NumberFormatException e) {
            JsonUtil.badRequest(resp, "Invalid bill id");
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

