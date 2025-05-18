package com.example.studyflowframework.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    public record Payload(Long userId, String newEmail, Instant until){}

    private final Map<String,Payload> store = new ConcurrentHashMap<>();

    public String createToken(Long userId, String newEmail){
        String token = UUID.randomUUID().toString();
        store.put(token,
                new Payload(userId,newEmail, Instant.now().plusSeconds(300)));  // 5 min
        return token;
    }
    public String codeOf(String token){                // uproszczony „kod” = pierwsze 6 znaków
        return token.substring(0,6).toUpperCase();
    }

    public Payload verify(String token, String code){
        Payload p = store.get(token);
        if(p==null || Instant.now().isAfter(p.until()) || !codeOf(token).equalsIgnoreCase(code))
            throw new RuntimeException("invalid-or-expired");
        store.remove(token);                            // jednorazowy
        return p;
    }
}
