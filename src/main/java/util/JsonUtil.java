package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    private static final Jsonb JSONB = JsonbBuilder.create();

    private JsonUtil() {}

    public static <T> T read(InputStream in, Class<T> cls) {
        try {
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return JSONB.fromJson(body, cls);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON body", e);
        }
    }

    public static String write(Object obj) {
        return JSONB.toJson(obj);
    }

    public static void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        String json;
        try {
            json = MAPPER.writeValueAsString(body);
        } catch (Exception e) {
            System.err.println("[JsonUtil] Serialization failed: " + e.getMessage());
            e.printStackTrace();
            json = "{\"error\":\"server_error\",\"message\":\"Response serialization failed\"}";
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
        resp.getOutputStream().flush();
    }

    public static void ok(HttpServletResponse resp, Object body) throws IOException {
        writeJson(resp, HttpServletResponse.SC_OK, body);
    }

    public static void created(HttpServletResponse resp, Object body) throws IOException {
        writeJson(resp, HttpServletResponse.SC_CREATED, body);
    }

    public static void badRequest(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, errorBody("bad_request", message));
    }

    public static void unauthorized(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED, errorBody("unauthorized", message));
    }

    public static void notFound(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_NOT_FOUND, errorBody("not_found", message));
    }

    public static void methodNotAllowed(HttpServletResponse resp) throws IOException {
        writeJson(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, errorBody("method_not_allowed", "Method not allowed"));
    }

    public static void serverError(HttpServletResponse resp, String message) throws IOException {
        writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorBody("server_error", message));
    }

    private static Map<String, String> errorBody(String error, String message) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}
