package com.game.chess.Service;

import com.game.chess.DTO.UserRequest;
import com.game.chess.DTO.UserResponse;
import com.game.chess.Model.User;
import com.game.chess.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public List<UserResponse> fetchAll(){
       return  userRepository.findAll().stream()
                .map(user -> mapToUserResponse(user))
                .collect(Collectors.toList());
    }



    public Optional<UserResponse> fetchById(Long id) {
        return userRepository.findById(id)
                .map(user -> mapToUserResponse(user));
    }

    public void addUser(UserRequest userRequest) {
        User user = new User();
        updateFromUserRequest(user, userRequest);
        userRepository.save(user);
    }


    public boolean updateUser(Long id, UserRequest userRequest) {
       return userRepository.findById(id)
                .map(existingUser -> {
                    updateFromUserRequest(existingUser, userRequest);
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void updateFromUserRequest(User user, UserRequest userRequest) {
        user.setUserName(userRequest.userName());
        user.setEmail(userRequest.email());
        user.setPassword(userRequest.password());
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserName(),
                user.getEmail(),
                user.getId()
        );
    }
}

