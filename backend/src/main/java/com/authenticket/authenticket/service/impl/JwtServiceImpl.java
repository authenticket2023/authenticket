package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${authenticket.secret-key}")
    private String SECRET_KEY;

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token from which to extract the username.
     * @return The username contained in the token.
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user role from a JWT token.
     *
     * @param token The JWT token from which to extract the user role.
     * @return The user role contained in the token.
     */
    @Override
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    /**
     * Extracts a claim from a JWT token using a provided claims resolver function.
     *
     * @param token           The JWT token from which to extract the claim.
     * @param claimsResolver  A function to resolve the desired claim from the token's claims.
     * @return The claim extracted from the token.
     */
    @Override
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for a user based on their UserDetails and optional extra claims.
     *
     * @param userDetails The UserDetails of the user for whom the token is generated.
     * @return The generated JWT token.
     */
    @Override
    public String generateUserToken(UserDetails userDetails){
        return generateUserToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token for a user based on their UserDetails and optional extra claims.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param userDetails The UserDetails of the user for whom the token is generated.
     * @return The generated JWT token.
     */
    @Override
    public String generateUserToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis()))
                .setExpiration(new Date(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token for a given ticket with a specified expiration date.
     *
     * @param ticket          The ticket for which the token is generated.
     * @param expirationDate  The date and time when the token will expire.
     * @return A JWT representing the ticket with the specified expiration date.
     */
    @Override
    public String generateTicketToken(Ticket ticket, LocalDateTime expirationDate) {
        return Jwts
                .builder()
                .claim("role", "ticket")
                .claim("event", ticket.getOrder().getEvent().getEventName())
                .setSubject(ticket.getTicketId().toString())
                .setIssuedAt(new Date(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis()))
                .setExpiration(Timestamp.valueOf(expirationDate))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks if a JWT token is valid for a specific user's UserDetails.
     *
     * @param token The JWT token to validate.
     * @param userDetails The UserDetails of the user for whom the token is validated.
     * @return True if the token is valid for the given user; otherwise, false.
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check for expiration.
     * @return True if the token has expired; otherwise, false.
     */
    @Override
    public boolean isTokenExpired(String token){
        Date expirationDate = extractExpiration(token);
        if (expirationDate == null) {
            return false;
        }
        return expirationDate.before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token from which to extract the expiration date.
     * @return The expiration date contained in the token.
     */
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token from which to extract all claims.
     * @return All claims contained in the token.
     */
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used for JWT token validation.
     *
     * @return The signing key.
     */
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}