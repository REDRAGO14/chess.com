package com.game.chess.Controller;

import com.game.chess.DTO.UserRequest;
import com.game.chess.DTO.UserResponse;
import com.game.chess.Model.User;
import com.game.chess.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getAll(){
        return ResponseEntity.ok(userService.fetchAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id){
        return userService.fetchById(id)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }
    @PostMapping("")
    public ResponseEntity<String> create(@RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return new ResponseEntity<>("User created Successfully",HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public  ResponseEntity<String> update(@PathVariable Long id, @RequestBody UserRequest userRequest){
        boolean updated = userService.updateUser(id, userRequest);
        if(updated) {
            return new ResponseEntity<>("USER UPDATED SUCCESSFULLY", HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("USER DELETED SUCCESSFULLY!!");
    }
}
