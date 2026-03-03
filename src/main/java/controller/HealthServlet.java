package controller;

import dto.ReservationPageDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ReservationModel;
import util.Db;
import util.JsonUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/health")
public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo(); // null or "/reservations-test"

        if ("/reservations-test".equals(path)) {
            // Hardcoded serialization test — no auth, no DB
            List<ReservationModel> list = Arrays.asList(
                new ReservationModel(1L, "John Smith", "123 Ocean Drive", "+1-305-555-0192", "Deluxe Suite", "2026-03-15", "2026-03-20"),
                new ReservationModel(2L, "Jane Doe",   "456 Beach Blvd",  "+1-305-555-0199", "Ocean View Suite", "2026-04-01", "2026-04-05")
            );
            ReservationPageDto page = new ReservationPageDto(list, 2, 1, 10);
            JsonUtil.ok(resp, page);
            return;
        }

        try {
            Db.verifyConnection();
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("status", "ok");
            payload.put("database", "up");
            JsonUtil.ok(resp, payload);
        } catch (Exception e) {
            JsonUtil.serverError(resp, "Database check failed: " + e.getMessage());
        }
    }
}
