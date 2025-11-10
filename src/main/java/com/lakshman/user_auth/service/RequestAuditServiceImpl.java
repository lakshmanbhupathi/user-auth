package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.RequestAudit;
import com.lakshman.user_auth.repository.RequestAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestAuditServiceImpl implements RequestAuditService {

    private final RequestAuditRepository requestAuditRepository;

    public RequestAuditServiceImpl(RequestAuditRepository requestAuditRepository) {
        this.requestAuditRepository = requestAuditRepository;
    }

    @Transactional
    @Override
    public void logRequest(RequestAudit audit) {
        requestAuditRepository.save(audit);
    }
}
