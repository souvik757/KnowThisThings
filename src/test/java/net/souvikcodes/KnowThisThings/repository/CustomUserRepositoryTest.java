package net.souvikcodes.KnowThisThings.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.repository.Implementation.CustomUserRepositoryImpl;

public class CustomUserRepositoryTest {

    @InjectMocks
    private CustomUserRepositoryImpl customUserRepository;
    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findUsersForSATest() {
        // Setting up mock data
        String username = "testuser";
        String password = "testpassword";
        String email = "demo@outlook.com";
        Users user = Users.builder().username(username).password(password)
                .email(email).optedSentimentAnalysis(true).roles(List.of("USER", "ADMIN")).build();
        // Mocking the MongoTemplate behavior
        when(mongoTemplate.find(ArgumentMatchers.any(), ArgumentMatchers.eq(Users.class))).thenReturn(List.of(user));

        // Test the findUsersForSA method
        List<Users> users = customUserRepository.findUsersForSA();
        // Checking the results
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(email, users.get(0).getEmail());
    }

}
