package net.souvikcodes.KnowThisThings.controller.ControllerUser;


import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    // PUT MAPPINGS
    @PostMapping("/updateuser")
    public ResponseEntity<?> updateUser(@RequestBody UsersDto usersDto){
        String curentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Users existingUser = userService.findByUserName(curentUsername);
        if (existingUser != null) {
            existingUser.setUsername(usersDto.getUsername());
            existingUser.setPassword(usersDto.getPassword());
            userService.saveUser(existingUser);
        } else {
            throw new ResourceNotFoundException("User not found with username: " + curentUsername);
        }

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body("user updated successfully!");
    }

    // DELETE MAPPINGS
    @DeleteMapping("/deleteuser")
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
