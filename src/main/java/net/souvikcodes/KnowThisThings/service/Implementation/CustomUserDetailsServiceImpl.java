package net.souvikcodes.KnowThisThings.service.Implementation;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.repository.IUserRepository;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {


    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);
        if (user != null) {
            // Handle null or empty roles - default to "USER"
            List<String> roles = user.getRoles() != null && !user.getRoles().isEmpty() 
                ? user.getRoles() 
                : List.of("USER");
            
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(roles.toArray(new String[0]))
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

}
