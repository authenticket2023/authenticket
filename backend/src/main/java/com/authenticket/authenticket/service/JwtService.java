package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Ticket;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    String extractRole(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    String generateUserToken(UserDetails userDetails);
    String generateUserToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    );
    String generateTicketToken(Ticket ticket, LocalDateTime time);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
}
