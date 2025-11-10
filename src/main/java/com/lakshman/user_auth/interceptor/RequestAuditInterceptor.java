package com.lakshman.user_auth.interceptor;

import com.lakshman.user_auth.entity.RequestAudit;
import com.lakshman.user_auth.service.RequestAuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

@Component
public class RequestAuditInterceptor implements HandlerInterceptor {
    
    private final RequestAuditService requestAuditService;
    
    public RequestAuditInterceptor(RequestAuditService requestAuditService) {
        this.requestAuditService = requestAuditService;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        try {
            Long startTime = (Long) request.getAttribute("startTime");
            long responseTime = System.currentTimeMillis() - startTime;
            
            RequestAudit audit = new RequestAudit();
            audit.setRequestPath(request.getRequestURI());
            audit.setMethod(request.getMethod());
            audit.setStatusCode(response.getStatus());
            audit.setResponseTimeMs(responseTime);
            audit.setIpAddress(getClientIP(request));
            audit.setUserAgent(request.getHeader("User-Agent"));
            
            // Get userId if authenticated
            Object userIdAttr = request.getAttribute("userId");
            if (userIdAttr != null) {
                audit.setUserId((Long) userIdAttr);
            }
            
            // Get request body if available
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String requestBody = new String(content, StandardCharsets.UTF_8);
                    audit.setRequestBody(truncate(requestBody, 5000));
                }
            }
            
            // Get response body if available
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String responseBody = new String(content, StandardCharsets.UTF_8);
                    audit.setResponseBody(truncate(responseBody, 5000));
                }
            }
            
            // Set error message if exception occurred
            if (ex != null) {
                audit.setErrorMessage(truncate(ex.getMessage(), 1000));
            }
            
            requestAuditService.logRequest(audit);
        } catch (Exception e) {
            // Don't let auditing failures break the request
            System.err.println("Failed to log request audit: " + e.getMessage());
        }
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }
}
