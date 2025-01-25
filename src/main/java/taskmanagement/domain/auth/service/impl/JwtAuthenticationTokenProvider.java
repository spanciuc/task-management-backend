package taskmanagement.domain.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import taskmanagement.domain.auth.exception.JwtAuthenticationFailedException;
import taskmanagement.domain.auth.exception.JwtGenerationFailedException;
import taskmanagement.domain.auth.service.AuthenticationTokenProvider;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Service
class JwtAuthenticationTokenProvider implements AuthenticationTokenProvider {

    private static final String AUTHORITIES_DELIMITER = ",";

    @Value("${security.jwt.expiration}")
    private long tokenExpirationMs;

    @Value("${security.jwt.secret.key}")
    private String tokenSecretKey;

    @Value("${security.jwt.authorities.key}")
    private String tokenAuthoritiesKey;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        this.signingKey = createSigningKey();
    }

    @Override
    public String generateToken(Authentication authentication) throws JwtGenerationFailedException {
        String authoritiesStr = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(AUTHORITIES_DELIMITER));
        Date issuedAt = new Date();

        try {
            return Jwts.builder()
                    .subject((authentication.getName()))
                    .issuedAt(issuedAt)
                    .expiration(new Date(issuedAt.getTime() + tokenExpirationMs))
                    .claim(tokenAuthoritiesKey, authoritiesStr)
                    .signWith(signingKey)
                    .compact();
        } catch (Exception e) {
            throw new JwtGenerationFailedException(e);
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken parseAuthentication(String token) throws JwtAuthenticationFailedException {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = claimsJws
                    .getPayload();
            Collection<GrantedAuthority> authorities = parseAuthorities(claims);
            String authenticationName = claims.getSubject();
            return new UsernamePasswordAuthenticationToken(authenticationName, null, authorities);
        } catch (Exception e) {
            throw new JwtAuthenticationFailedException(e);
        }
    }

    SecretKey createSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Collection<GrantedAuthority> parseAuthorities(Claims claims) {
        String authoritiesStr = claims.get(tokenAuthoritiesKey, String.class);

        if (authoritiesStr == null || authoritiesStr.isEmpty()) {
            return Collections.emptySet();
        }

        return Arrays.stream(authoritiesStr.split(AUTHORITIES_DELIMITER))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
