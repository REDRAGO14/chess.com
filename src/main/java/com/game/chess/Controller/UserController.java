package com.game.chess.Controller;

import com.game.chess.Model.User;
import com.game.chess.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.NestingKind;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("")
    public ResponseEntity<List<User>> getAll(){
        return ResponseEntity.ok(userService.fetchAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        return userService.fetchById(id)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }
    @PostMapping("")
    public ResponseEntity<String> create(@RequestBody User user){
        userService.addUser(user);
        return new ResponseEntity<>("User created Successfully",HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public  ResponseEntity<String> update(@PathVariable Long id, @RequestBody User user){
        boolean updated = userService.updateUser(id, user);
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
