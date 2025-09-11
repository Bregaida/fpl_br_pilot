package br.com.fplbr.pilot.auth.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PendingRegistrationStore {
    public static class PendingData {
        public Map<String, Object> data;
        public String secretBase32;
        public OffsetDateTime expiresAt;
    }

    private final ConcurrentHashMap<String, PendingData> emailToPending = new ConcurrentHashMap<>();

    public void put(String email, Map<String, Object> data, String secret, OffsetDateTime expiresAt) {
        PendingData p = new PendingData();
        p.data = data; p.secretBase32 = secret; p.expiresAt = expiresAt;
        emailToPending.put(email.toLowerCase(), p);
    }

    public PendingData get(String email) {
        PendingData p = emailToPending.getOrDefault(email.toLowerCase(), null);
        if (p != null && p.expiresAt.isAfter(OffsetDateTime.now())) return p;
        if (p != null) emailToPending.remove(email.toLowerCase());
        return null;
    }

    public void remove(String email) { emailToPending.remove(email.toLowerCase()); }
}


