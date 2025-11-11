package com.lakshman.user_auth.repository;

import com.lakshman.user_auth.entity.SessionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionHistoryRepository extends JpaRepository<SessionHistory, Long> {
    List<SessionHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
