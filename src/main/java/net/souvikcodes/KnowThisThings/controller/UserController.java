package net.souvikcodes.KnowThisThings.controller;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.service.IUserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    
    private final IUserService userService;

    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        Optional<Users> user = userService.findById(objectId);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable String username) {
        Users user = userService.findByUserName(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Create new user
     */
    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        if (user == null || user.getUsername() == null) {
            return ResponseEntity.badRequest().build();
        }
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Update user
     */
    @PostMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable String id, @RequestBody Users updatedUser) {
        ObjectId objectId = new ObjectId(id);
        Optional<Users> existingUser = userService.findById(objectId);
        
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Users user = existingUser.get();
        if (updatedUser.getUsername() != null) {
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getJournalEntries() != null) {
            user.setJournalEntries(updatedUser.getJournalEntries());
        }
        
        userService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    /**
     * Delete user by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        userService.deleteById(objectId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete user by username
     */
    @DeleteMapping("/username/{username}")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        Users user = userService.findByUserName(username);
        userService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }
}
