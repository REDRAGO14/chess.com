package com.game.chess.Service;

import com.game.chess.Model.User;
import com.game.chess.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public List<User> fetchAll(){
        return userRepository.findAll();
    }

    public Optional<User> fetchById(Long id) {
        return userRepository.findById(id);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public boolean updateUser(Long id, User user) {
       return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUserName(user.getUserName());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setUpdatedAt(user.getUpdatedAt());
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
