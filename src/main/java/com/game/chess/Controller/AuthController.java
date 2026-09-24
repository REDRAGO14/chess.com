package com.game.chess.Controller;

import com.game.chess.DTO.UserRequest;
import com.game.chess.DTO.UserResponse;
import com.game.chess.Model.AuthRequest;
import com.game.chess.Security.JwtService;
import com.game.chess.Service.AuthService;
import com.game.chess.Util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JWTUtil jwtUtil, JwtService jwtService, AuthService authService) {
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
    public String signIn(@RequestBody AuthRequest authRequest){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
            );
            return jwtService.generateToken(authRequest);
        }catch (Exception e){
            throw e;
        }
    }
}
