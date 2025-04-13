package com.phuquocchamp.backend.domain.authentication.filter;


import com.phuquocchamp.backend.domain.authentication.model.AuthUser;
import com.phuquocchamp.backend.domain.authentication.service.AuthenticationService;
import com.phuquocchamp.backend.domain.authentication.utils.JsonWebToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Component
public class AuthenticationFilter extends HttpFilter {
    private final List<String> unsecuredEndpoints = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/send-password-reset-token"
    );

    private final JsonWebToken jsonWebToken;
    private final AuthenticationService authenticationService;

    public AuthenticationFilter(AuthenticationService authenticationService, JsonWebToken jsonWebToken) {
        this.authenticationService = authenticationService;
        this.jsonWebToken = jsonWebToken;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();

        if(unsecuredEndpoints.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        try{
            String authorizationHeader = request.getHeader("Authorization");
            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new ServletException("Token missing in Authorization header");
            }
            String token = authorizationHeader.substring(7);

            if(jsonWebToken.isTokenExpired(token)) {
                throw new ServletException("Token expired");
            }

            String email = jsonWebToken.getEmailFromToken(token);
            AuthUser user = authenticationService.getUser(email);
            request.setAttribute("authenticated-user", user);
            chain.doFilter(request, response);
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid authentication token, or token missing.\"}");
        }

    }
}
