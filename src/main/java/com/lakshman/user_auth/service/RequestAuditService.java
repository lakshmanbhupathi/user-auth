package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.RequestAudit;

public interface RequestAuditService {
    void logRequest(RequestAudit audit);
}
