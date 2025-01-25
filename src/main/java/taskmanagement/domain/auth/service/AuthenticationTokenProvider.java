package taskmanagement.domain.auth.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import taskmanagement.domain.auth.exception.JwtAuthenticationFailedException;
import taskmanagement.domain.auth.exception.JwtGenerationFailedException;

/**
 * Authentication token provider.
 */
public interface AuthenticationTokenProvider {
    /**
     * Generates authentication token.
     *
     * @param authentication authentication data.
     * @return generated token.
     * @throws JwtGenerationFailedException if fails to generate the token.
     */
    String generateToken(Authentication authentication) throws JwtGenerationFailedException;

    /**
     * Parses authentication token.
     *
     * @param token token.
     * @return parsed authentication data.
     * @throws JwtAuthenticationFailedException if token parsing fails for whatever reason (ex. token is invalid, token is expired).
     */
    UsernamePasswordAuthenticationToken parseAuthentication(String token) throws JwtAuthenticationFailedException;
}
