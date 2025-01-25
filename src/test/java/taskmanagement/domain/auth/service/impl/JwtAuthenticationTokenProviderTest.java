package taskmanagement.domain.auth.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import taskmanagement.domain.auth.exception.JwtGenerationFailedException;
import taskmanagement.domain.user.config.UserConfig;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationTokenProviderTest {

    private static final long TOKEN_EXPIRATION_MS = 600000L;
    public static final String TOKEN_AUTHORITIES_KEY = "auth";
    public static final String TOKEN_SECRET_KEY = "kVZ01JWk+zT9A8pNU9pDQeOaflR+vNRy5LJWqqoz8RI=";
    private JwtAuthenticationTokenProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtAuthenticationTokenProvider(TOKEN_EXPIRATION_MS, TOKEN_SECRET_KEY, TOKEN_AUTHORITIES_KEY, null);
        jwtProvider.init();
    }

    @Test
    void generateToken_shouldGenerateToken() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "testUser",
                null,
                Set.of(new SimpleGrantedAuthority(UserConfig.Role.USER.name()))
        );

        String token = jwtProvider.generateToken(authentication);

        assertThat(token).isNotNull();
        assertThat(Jwts.parser().verifyWith(jwtProvider.createSigningKey()).build().parseSignedClaims(token)).isNotNull();
    }

    @Test
    void generateToken_whenInvalidAuth_shouldThrowException() {
        // Arrange
        Authentication invalidAuth = mock(Authentication.class);
        when(invalidAuth.getName()).thenThrow(new RuntimeException("Failed to extract name"));

        // Act & Assert
        assertThatThrownBy(() -> jwtProvider.generateToken(invalidAuth))
                .isInstanceOf(JwtGenerationFailedException.class);
    }

    @Test
    void parseAuthentication_shouldParseToken() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "testUser",
                null,
                Set.of(new SimpleGrantedAuthority(UserConfig.Role.USER.name()))
        );

        String token = jwtProvider.generateToken(authentication);

        UsernamePasswordAuthenticationToken authenticationFromToken = jwtProvider.parseAuthentication(token);
        assertThat(authenticationFromToken.getPrincipal()).isEqualTo(authentication.getPrincipal());
        assertThat(authenticationFromToken.getAuthorities()).isEqualTo(authentication.getAuthorities());
    }

    @Test
    void parseAuthentication_whenNoClaims_shouldParseTokenWithEmptyAuthorities() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "testUser",
                null,
                null
        );

        String token = jwtProvider.generateToken(authentication);

        UsernamePasswordAuthenticationToken authenticationFromToken = jwtProvider.parseAuthentication(token);
        assertThat(authenticationFromToken.getPrincipal()).isEqualTo(authentication.getPrincipal());
        assertThat(authenticationFromToken.getAuthorities()).isEqualTo(authentication.getAuthorities());
    }

    @Test
    void parseAuthentication_whenInvalidToken_shouldThrowException() {
        var invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZTEiLCJpYXQiOjE3Mzc3NzIyNTYsImV4cCI6MTczNzg1ODY1NiwiYXV0b3JpdGllcyI6IkRFTEVURSxSRUFELFdSSVRFIn0.W7hL-Si_xFHAVD_aU2Yy0LJS2TUTI7LrFzRBvdPNHa";
        assertThatThrownBy(() -> jwtProvider.parseAuthentication(invalidToken))
                .isInstanceOf(taskmanagement.domain.auth.exception.JwtAuthenticationFailedException.class);
    }

    @Test
    void parseAuthentication_whenExpiredToken_shouldThrowException() {
        var invalidToken = generateTestExpiredToken();
        assertThatThrownBy(() -> jwtProvider.parseAuthentication(invalidToken))
                .isInstanceOf(taskmanagement.domain.auth.exception.JwtAuthenticationFailedException.class);
    }

    private String generateTestExpiredToken() throws JwtGenerationFailedException {
        Date issuedDate = new Date(new Date().getTime() - TOKEN_EXPIRATION_MS);
        return Jwts.builder()
                .subject("subject")
                .issuedAt(issuedDate)
                .expiration(new Date(issuedDate.getTime() + TOKEN_EXPIRATION_MS))
                .claim(TOKEN_AUTHORITIES_KEY, "ADMIN,USER")
                .signWith(createTestSigningKey())
                .compact();
    }

    private SecretKey createTestSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}