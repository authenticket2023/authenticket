package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Ticket;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    String extractRole(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    String generateToken(UserDetails userDetails);
    String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    );
    String generateToken(Ticket ticket, LocalDateTime time);

    String generateToken(Map<String, Object> extraClaims, Ticket ticket);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
}
