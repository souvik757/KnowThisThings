package net.souvikcodes.KnowThisThings.controller.ControllerUser;


import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.boot.security.autoconfigure.SecurityProperties.User;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import net.souvikcodes.KnowThisThings.exception.customexception.ResourceNotFoundException;
import net.souvikcodes.KnowThisThings.service.IUserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restricted")
public class UserControllerRestricted {

    private final IUserService userService;
    private final ModelMapper modelMapper;

    // GET MAPPINGS
    @GetMapping("/admin/allusers")
    public ResponseEntity<List<UsersDto>> getAllUsers() {
        List<Users> users = userService.getAll();
        List<UsersDto> userDtos = users.stream()
            .map(user -> modelMapper.map(user, UsersDto.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    // PUT MAPPINGS
    @PostMapping("/user/updateuser")
    public ResponseEntity<?> updateUser(@RequestBody UsersDto usersDto){
        String curentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Users existingUser = userService.findByUserName(curentUsername);
        if (existingUser != null) {
            if (usersDto.getUsername() != null) {
                existingUser.setUsername(usersDto.getUsername());
            }
            if (usersDto.getPassword() != null) {
                existingUser.setPassword(usersDto.getPassword());
            }
            if (usersDto.getAdminFlag() != null) {
                existingUser.setAdminFlag(usersDto.getAdminFlag());
            }
            userService.saveUser(existingUser);
        } else {
            throw new ResourceNotFoundException("User not found with username: " + curentUsername);
        }

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body("user updated successfully!");
    }


    // DELETE MAPPINGS
    @DeleteMapping("/user/deleteuser")
    public ResponseEntity<?> deleteUser(){
        String curentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Users existingUser = userService.findByUserName(curentUsername);
        if (existingUser != null) {
            userService.deleteById(existingUser.getId());
        } else {
            throw new ResourceNotFoundException("User not found with username: " + curentUsername);
        }

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body("user deleted successfully!");
    }
}
