package com.example.login.api;

import com.example.login.api.dto.AuditResponse;
import com.example.login.application.AuditService;
import com.example.login.domain.AuditEvent;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAuthority('audit:view')")
    public List<AuditResponse> search(
            @RequestParam(value = "user", required = false) String username,
            @RequestParam(value = "resource", required = false) String resource,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "decision", required = false) String decision,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return auditService.search(username, resource, action, decision, from, to).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AuditResponse toDto(AuditEvent e) {
        String username = e.getUser() != null ? e.getUser().getUsername() : null;
        return new AuditResponse(
                e.getId(),
                e.getCreatedAt(),
                username,
                e.getResource(),
                e.getAction(),
                e.getDecision(),
                e.getReason(),
                e.getIp(),
                e.getUserAgent());
    }
}
