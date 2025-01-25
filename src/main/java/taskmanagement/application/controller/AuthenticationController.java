package taskmanagement.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import taskmanagement.domain.auth.service.AuthenticationService;
import taskmanagement.domain.auth.dto.LoginRequest;
import taskmanagement.domain.auth.dto.LoginResponse;
import taskmanagement.domain.user.dto.RegisterUserRequest;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/auth")
@Tag(name = "Authentication controller")
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping(path = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register new user endpoint")
    public void register(@RequestBody @Valid RegisterUserRequest request) {
        authService.register(request);
    }

    @PostMapping(value = "/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login user endpoint")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

}
