package ru.mirea.coursework.defecttracker.adapter.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mirea.coursework.defecttracker.adapter.web.dto.UserResponse;
import ru.mirea.coursework.defecttracker.application.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService users;
    public AuthController(UserService users) { this.users = users; }

    public record RegisterRequest(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,64}") String username,
            @NotBlank @Size(min = 12, max = 128) String password,
            @NotBlank @Size(max = 120) String displayName) { }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(users.register(request.username(), request.password(), request.displayName()));
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserDetails principal) {
        return UserResponse.from(users.require(principal.getUsername()));
    }
}
