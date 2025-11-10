package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.RequestAudit;
import com.lakshman.user_auth.repository.RequestAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestAuditService {
    
    private final RequestAuditRepository requestAuditRepository;
    
    public RequestAuditService(RequestAuditRepository requestAuditRepository) {
        this.requestAuditRepository = requestAuditRepository;
    }
    
    @Transactional
    public void logRequest(RequestAudit audit) {
        requestAuditRepository.save(audit);
    }
}
