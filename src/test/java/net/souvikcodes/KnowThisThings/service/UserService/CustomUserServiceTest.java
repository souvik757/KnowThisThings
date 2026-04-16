package net.souvikcodes.KnowThisThings.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.repository.IUserRepository;
import net.souvikcodes.KnowThisThings.service.Implementation.CustomUserDetailsServiceImpl;

public class CustomUserServiceTest {

    @InjectMocks
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Mock
    private IUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loadUserByUsernameTest01() {
        // Setting up mock data
        String username = "testuser";
        String password = "testpassword";
        Users user = Users.builder().username(username).password(password).roles(List.of("USER", "ADMIN")).build();

        when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(user);

        // Test the loadUserByUsername method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Checking the results
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());

    }

    @Test
    public void loadUserByUsernameTest02() {
        // Setting up mock data
        String username = "testuser";
        String password = "testpassword";
        Users user = Users.builder().username(username).password(password).build();

        when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(user);
        // Test the loadUserByUsername method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        assertNotNull(userDetails);

    }

    @Test
    public void loadUserByUsernameTest03() {
        when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("nonexistent"));
    }
}
