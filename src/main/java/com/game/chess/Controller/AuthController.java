package com.game.chess.Controller;

import com.game.chess.DTO.UserRequest;
import com.game.chess.DTO.UserResponse;
import com.game.chess.Model.AuthRequest;
import com.game.chess.Model.User;
import com.game.chess.Security.JwtService;
import com.game.chess.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuthService authService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authService = authService;
    }



    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody UserRequest request) {

        return authService.register(request);
    }

   

    @PostMapping("/login")
    public String login(
            @Valid @RequestBody AuthRequest authRequest) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.username(),
                                authRequest.password()
                        )
                );

        User user = (User) authentication.getPrincipal();

        return jwtService.generateToken(user.getUsername());
    }
}