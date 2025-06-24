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
        System.out.println("Interceptor triggered: " + request.getRequestURI());
        System.out.println("Using strategy: " + strategy);

        if ("v2".equalsIgnoreCase(strategy)) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing or invalid Authorization header");
                return false;
            }

            String token = authHeader.substring(7);

            if (token.split("\\.").length == 3) {
                // JWT
                if (!jwtUtil.validateToken(token)) {
                    response.setStatus(401);
                    response.getWriter().write("Invalid or expired JWT");
                    return false;
                }

                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                String tier = claims.get("tier", String.class);

                if (!rateLimiterServiceV2.isAllowed(username, tier)) {
                    response.setStatus(429);
                    response.getWriter().write("Too Many Requests - Tier Rate limit exceeded");
                    return false;
                }

                return true;

            } else {
                // UUID ApiToken
                ApiToken apiToken = apiTokenRepo.findByToken(token).orElse(null);
                if (apiToken == null || apiToken.isRevoked() || apiToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                    response.setStatus(401);
                    response.getWriter().write("Invalid or expired API token");
                    return false;
                }

                if (!rateLimiterServiceV2.isAllowed(apiToken.getToken(), apiToken.getTier())) {
                    response.setStatus(429);
                    response.getWriter().write("Too Many Requests - Token tier rate limit exceeded");
                    return false;
                }

                return true;
            }
        }

        response.setStatus(500);
        response.getWriter().write("Unsupported rate limiting strategy");
        return false;
    }
}
