package taskmanagement.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import taskmanagement.domain.auth.config.SecurityConfig;
import taskmanagement.domain.auth.service.AuthenticationTokenProvider;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_TOKEN_PREFIX = "Bearer ";

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private final AuthenticationTokenProvider authenticationTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            parseAuthenticationToken(request).ifPresent(token -> {
                UsernamePasswordAuthenticationToken authentication = authenticationTokenProvider.parseAuthentication(token);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        } catch (Exception e) {
            log.error("Failed to authenticate user using JWT token:", e);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> parseAuthenticationToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .filter(StringUtils::hasText)
                .filter(s -> s.startsWith(AUTHENTICATION_TOKEN_PREFIX))
                .map(s -> s.substring(AUTHENTICATION_TOKEN_PREFIX.length()));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI().substring(contextPath.length());
        for (String publicEndpoint : SecurityConfig.PUBLIC_ENDPOINTS) {
            if (matchEndpoint(requestUri, publicEndpoint)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchEndpoint(String requestUri, String publicEndpoint) {
        return new AntPathMatcher().match(publicEndpoint, requestUri);
    }
}
