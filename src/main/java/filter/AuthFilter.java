package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.JsonUtil;

import java.io.IOException;
import java.util.Set;

@WebFilter("/api/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC = Set.of(
            "/api/health",
            "/api/auth/login"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();
        String ctx = req.getContextPath();
        if (ctx != null && !ctx.isBlank()) path = path.substring(ctx.length());

        if (PUBLIC.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.unauthorized(resp, "Login required");
            return;
        }

        chain.doFilter(request, response);
    }
}