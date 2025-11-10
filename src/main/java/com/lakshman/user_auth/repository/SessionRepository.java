package com.lakshman.user_auth.repository;

import com.lakshman.user_auth.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionToken(String sessionToken);
    List<Session> findByLastAccessedAtBeforeAndIsActiveTrue(LocalDateTime lastAccessedAt);
}
