package com.example.login.application;

import com.example.login.domain.AuditEvent;
import com.example.login.domain.SessionToken;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.AuditEventRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    @Transactional
    public void record(User user, SessionToken session, String resource, String action, String decision, String reason, String ip, String userAgent) {
        AuditEvent evt = new AuditEvent();
        evt.setUser(user);
        evt.setSession(session);
        evt.setResource(resource);
        evt.setAction(action);
        evt.setDecision(decision);
        evt.setReason(reason);
        evt.setIp(ip);
        evt.setUserAgent(userAgent);
        evt.setCreatedAt(OffsetDateTime.now());
        auditEventRepository.save(evt);
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> search(String username, String resource, String action, String decision, OffsetDateTime from, OffsetDateTime to) {
        return auditEventRepository.search(username, resource, action, decision, from, to);
    }
}
