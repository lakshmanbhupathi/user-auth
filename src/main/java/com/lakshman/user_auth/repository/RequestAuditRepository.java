package com.lakshman.user_auth.repository;

import com.lakshman.user_auth.entity.RequestAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestAuditRepository extends JpaRepository<RequestAudit, Long> {
}
