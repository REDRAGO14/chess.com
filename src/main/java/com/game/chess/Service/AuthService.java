package com.game.chess.Service;
import com.game.chess.DTO.UserRequest; import com.game.chess.DTO.UserResponse; import com.game.chess.Model.User; import com.game.chess.Repository.UserRepository;
import org.springframework.http.HttpStatus; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Service; import org.springframework.web.server.ResponseStatusException;
@Service public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(UserRequest request) {


        if (userRepository.existsByUserName(request.userName())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.email())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }


        User user = new User();

        user.setUserName(request.userName());
        user.setEmail(request.email());


        user.setPassword(
                passwordEncoder.encode(request.password())
        );


        User savedUser = userRepository.save(user);


        return new UserResponse(
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getId()
        );
    }
}