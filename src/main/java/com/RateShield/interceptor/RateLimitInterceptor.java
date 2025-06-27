package com.RateShield.interceptor;

import com.RateShield.model.ApiToken;
import com.RateShield.ratelimiter.service.RateLimiterServiceV2;
import com.RateShield.repository.ApiTokenRepository;
import com.RateShield.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimiterServiceV2 rateLimiterServiceV2;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ApiTokenRepository apiTokenRepo;

    @Value("${rate.limit.strategy:v2}")
    private String strategy;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Interceptor triggered for: " + request.getRequestURI());
        System.out.println("Using strategy: " + strategy);

        if (!"v2".equalsIgnoreCase(strategy)) {
            response.setStatus(500);
            response.getWriter().write("Unsupported rate limiting strategy");
            return false;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);

        if (token.split("\\.").length == 3) {
            // JWT token (admin or internal clients)
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(401);
                response.getWriter().write("Invalid or expired JWT");
                return false;
            }

            Claims claims = jwtUtil.extractAllClaims(token);
            UUID orgId = UUID.fromString(claims.get("orgId", String.class));
            String tokenId = claims.getSubject();

            if (!rateLimiterServiceV2.isAllowed(orgId, tokenId)) {
                response.setStatus(429);
                response.getWriter().write("Too Many Requests - Org plan limit exceeded");
                return false;
            }

            return true;

        } else {
            // UUID-based API token (bots)
            ApiToken apiToken = apiTokenRepo.findByToken(token).orElse(null);

            if (apiToken == null || apiToken.isRevoked() || apiToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                response.setStatus(401);
                response.getWriter().write("Invalid or expired API token");
                return false;
            }

            //  Scope validation
            String requestPath = request.getRequestURI().replaceAll("/+$", "");
            List<String> allowedPaths = Arrays.stream(apiToken.getScopes().split(","))
                                              .map(String::trim)
                                              .map(scope -> scope.replaceAll("/+$", ""))  // Normalize
                                              .collect(Collectors.toList());

            System.out.println("Request path: " + requestPath);
            System.out.println("Allowed scopes: " + allowedPaths);

            if (!allowedPaths.contains(requestPath)) {
                response.setStatus(403);
                response.getWriter().write("Access denied: insufficient scope for this endpoint");
                return false;
            }

            // Rate limit check after scope
            UUID orgId = apiToken.getOrgId();
            String tokenId = apiToken.getToken();

            if (!rateLimiterServiceV2.isAllowed(orgId, tokenId)) {
                response.setStatus(429);
                response.getWriter().write("Too Many Requests - Org plan limit exceeded");
                return false;
            }

            return true;
        }
    }
}
