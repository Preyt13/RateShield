//Interceptor to capture requests and call isAllowed() function.
// isAllowed() under RateLimitService decides if further requests are allowed


package com.RateShield.interceptor;

import com.RateShield.auth.JwtUtil;
import com.RateShield.legacy.RateLimiterServiceV1;
import com.RateShield.ratelimiter.service.RateLimiterServiceV2;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimiterServiceV2 rateLimiterServiceV2;

    @Autowired
    private RateLimiterServiceV1 rateLimiterServiceV1;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${rate.limit.strategy:v2}") // default to v2 if not set
    private String strategy;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Interceptor triggered: " + request.getRequestURI());
        System.out.println("Using strategy: " + strategy);
        if ("v1".equalsIgnoreCase(strategy)) {
            // Legacy IP-based strategy
            String clientIp = extractClientIp(request);
            if (!rateLimiterServiceV1.isAllowed(clientIp)) {
                response.setStatus(429);
                response.getWriter().write("Too Many Requests - IP Rate limit exceeded");
                return false;
            }
            return true;

        } else if ("v2".equalsIgnoreCase(strategy)) {
            //JWT + Tier-based strategy
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing or invalid Authorization header");
                return false;
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                response.setStatus(401);
                response.getWriter().write("Invalid or expired token");
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
            response.setStatus(500);
            response.getWriter().write("Unsupported rate limiting strategy");
            return false;
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
