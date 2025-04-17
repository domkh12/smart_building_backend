package edu.npic.smartBuilding.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String EXPECTED_API_KEY = "your-secret-api-key";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/v1/hardware")) {
            String apiKey = request.getHeader(API_KEY_HEADER);
            if (apiKey == null || !apiKey.equals(EXPECTED_API_KEY)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
