package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public final class RequestUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RequestUtil() {}

    public static <T> T readJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        return MAPPER.readValue(req.getInputStream(), clazz);
    }
}