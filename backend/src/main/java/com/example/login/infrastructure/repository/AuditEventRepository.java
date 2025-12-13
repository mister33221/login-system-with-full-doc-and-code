package com.example.login.infrastructure.repository;

import com.example.login.domain.AuditEvent;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    @Query("""
            select e from AuditEvent e
            left join fetch e.user u
            where (:username is null or e.user.username = :username)
              and (:resource is null or e.resource = :resource)
              and (:action is null or e.action = :action)
              and (:decision is null or e.decision = :decision)
              and (e.createdAt >= coalesce(:fromTime, e.createdAt))
              and (e.createdAt <= coalesce(:toTime, e.createdAt))
            order by e.createdAt desc
            """)
    List<AuditEvent> search(
            @Param("username") String username,
            @Param("resource") String resource,
            @Param("action") String action,
            @Param("decision") String decision,
            @Param("fromTime") OffsetDateTime fromTime,
            @Param("toTime") OffsetDateTime toTime);
}
