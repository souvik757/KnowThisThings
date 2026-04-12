package net.souvikcodes.KnowThisThings.controller.ControllerUser;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
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
import net.souvikcodes.KnowThisThings.dto.UsersDto;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.service.IUserService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    
    private final IUserService userService;
    private final ModelMapper modelMapper;

    // POST MAPPINGS
    // to create a new user -- unauthenticated endpoint
    @PostMapping
    public ResponseEntity<UsersDto> createUser(@RequestBody UsersDto userDto) {
        if (userDto == null || userDto.getUsername() == null) {
            return ResponseEntity.badRequest().build();
        }
        Users user = convertToEntity(userDto);
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(user));
    }



    // GET MAPPINGS

    @GetMapping("/{id}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        Optional<Users> user = userService.findById(objectId);
        return user.map(u -> ResponseEntity.ok(convertToDto(u)))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsersDto> getUserByUsername(@PathVariable String username) {
        Users user = userService.findByUserName(username);
        return ResponseEntity.ok(convertToDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        userService.deleteById(objectId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/username/{username}")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        Users user = userService.findByUserName(username);
        userService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    // ── Helper methods to convert between Entity and DTO ──
    private UsersDto convertToDto(Users user) {
        return modelMapper.map(user, UsersDto.class);
    }

    private Users convertToEntity(UsersDto dto) {
        return modelMapper.map(dto, Users.class);
    }
}
