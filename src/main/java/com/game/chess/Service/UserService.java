package com.game.chess.Service;

import com.game.chess.DTO.UserRequest;
import com.game.chess.DTO.UserResponse;
import com.game.chess.Exception.UserNameAlreadyInUseException;
import com.game.chess.Exception.UserNotFound;
import com.game.chess.Model.User;
import com.game.chess.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;


    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public List<UserResponse> fetchAll(){
       return  userRepository.findAll().stream()
                .map(user -> mapToUserResponse(user))
                .collect(Collectors.toList());
    }



    public UserResponse fetchById(Long id) {
        return userRepository.findById(id)
                .map(user -> mapToUserResponse(user))
                .orElseThrow(() -> new UserNotFound("user not found"));
    }

    public void addUser(UserRequest userRequest) {
        User user = new User();
        updateFromUserRequest(user, userRequest);

         User matchUser = userRepository.findByUserName(user.getUserName()).orElse(null);
        if(matchUser == null){
            userRepository.save(user);
        }else{
            throw new UserNameAlreadyInUseException("Username has been already taken try another username");
        }


    }


    public void updateUser(Long id, UserRequest userRequest) {
        userRepository.findById(id)
                .map(existingUser -> {
                    updateFromUserRequest(existingUser, userRequest);
                    userRepository.save(existingUser);
                    return true;
                }).orElseThrow(()-> new UserNotFound("user not found"));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            userRepository.deleteById(id);
        }else{
            throw  new UserNotFound("User Not Found");
        }

    }

    private void updateFromUserRequest(User user, UserRequest userRequest) {
        user.setUserName(userRequest.userName());
        user.setEmail(userRequest.email());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserName(),
                user.getEmail(),
                user.getId()
        );
        );
    }
}

