package com.game.chess.Controller;

import com.game.chess.DTO.UserRequest;
import com.game.chess.DTO.UserResponse;
import com.game.chess.Model.User;
import com.game.chess.Service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getAll(){
        return ResponseEntity.ok(userService.fetchAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id){
        UserResponse user = userService.fetchById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return new ResponseEntity<>("User created Successfully",HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public  ResponseEntity<String> update(@PathVariable @NotNull(message = "User ID must not be null")
                                              @Positive(message = "User ID must be a positive number") Long id, @Valid @RequestBody UserRequest userRequest){
        userService.updateUser(id, userRequest);
        return new ResponseEntity<>("USER UPDATED SUCCESSFULLY", HttpStatus.OK);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        userService.deleteUser(id);
        return new ResponseEntity<>("USER DELETED SUCCESSFULLY!!", HttpStatus.NO_CONTENT);
    }
}
