package net.souvikcodes.KnowThisThings.controller.ControllerExternalApi;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.service.IExternalApiService;
import net.souvikcodes.KnowThisThings.service.IUserService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/external")
public class ExternalApiController {

    private final IExternalApiService externalApiService;
    private final IUserService userService;

    @GetMapping("/dadjoke")
    public ResponseEntity<String> getDadJokeOfTheDay() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = userService.findByUserName(username);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized: User not found with username " + username);
        }

        String jokeOfTheDay = externalApiService.getDadJokeOfTheDay();
        return (jokeOfTheDay.isBlank() || jokeOfTheDay.isEmpty()) ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(jokeOfTheDay);
    }
}
