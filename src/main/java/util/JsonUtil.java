package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {}

    public static void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        MAPPER.writeValue(resp.getWriter(), body);
    }

    public static void ok(HttpServletResponse resp, Object body) throws IOException {
        writeJson(resp, HttpServletResponse.SC_OK, body);
    }

    public static void created(HttpServletResponse resp, Object body) throws IOException {
        writeJson(resp, HttpServletResponse.SC_CREATED, body);
    }

    public static void badRequest(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                "error", "bad_request",
                "message", message
        ));
    }

    public static void unauthorized(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED, Map.of(
                "error", "unauthorized",
                "message", message
        ));
    }

    public static void notFound(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_NOT_FOUND, Map.of(
                "error", "not_found",
                "message", message
        ));
    }

    public static void methodNotAllowed(HttpServletResponse resp) throws IOException {
        writeJson(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, Map.of(
                "error", "method_not_allowed",
                "message", "Method not allowed"
        ));
    }

    public static void serverError(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                "error", "server_error",
                "message", message
        ));
    }
}