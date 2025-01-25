package taskmanagement.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import taskmanagement.domain.auth.dto.LoginRequest;
import taskmanagement.domain.auth.dto.LoginResponse;
import taskmanagement.domain.auth.service.AuthenticationService;
import taskmanagement.domain.auth.service.AuthenticationTokenProvider;
import taskmanagement.domain.user.dto.RegisterUserRequest;
import taskmanagement.domain.user.exception.EmailAlreadyExistsException;
import taskmanagement.domain.user.exception.UsernameAlreadyExistsException;
import taskmanagement.domain.user.service.UserRegistrationService;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRegistrationService userRegistrationService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationTokenProvider authenticationTokenProvider;

    @Override
    public void register(RegisterUserRequest request) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        userRegistrationService.register(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) throws AuthenticationException {
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return LoginResponse.of(authenticationTokenProvider.generateToken(authentication));
    }
}
