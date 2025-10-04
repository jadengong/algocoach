package com.algocoach.aspect;

import com.algocoach.annotation.RateLimited;
import com.algocoach.config.RateLimitingConfig;
import com.algocoach.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitingAspect {

    @Autowired
    private RateLimitingConfig rateLimitingConfig;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        HttpServletRequest request = getCurrentHttpRequest();
        String clientIp = getClientIpAddress(request);
        String key = rateLimited.key().isEmpty() ? clientIp : rateLimited.key();
        
        if (rateLimitingConfig.isAllowed(key, rateLimited.value())) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitException("Rate limit exceeded. Try again later.");
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}
