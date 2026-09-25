package com.game.chess.Security;

import com.game.chess.Repository.UserRepository;
import com.game.chess.Service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            UserRepository userRepository,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                .authorizeHttpRequests(auth -> auth


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register"
                        ).permitAll()


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        ).permitAll()


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users"
                        ).permitAll()


                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("ADMIN")


                        .anyRequest().authenticated()
                )


                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return new CustomUserDetailService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService()
                );

        provider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(provider);
    }
}