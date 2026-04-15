package net.souvikcodes.KnowThisThings.service.UserService.TestClass;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import net.souvikcodes.KnowThisThings.entity.JournalEntry;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.exception.customexception.ResourceNotFoundException;
import net.souvikcodes.KnowThisThings.repository.IJournalEntryRepository;
import net.souvikcodes.KnowThisThings.repository.IUserRepository;
import net.souvikcodes.KnowThisThings.service.IUserService;

@SpringBootTest
public class UserServiceTest01 {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IJournalEntryRepository journalEntryRepository;

    // Track created entities for cleanup
    private List<ObjectId> createdUserIds;
    private List<String> createdJournalEntryIds;

    private static Users testUser;
    private static Users testAdminUser;
    private static JournalEntry testJournalEntry;

    @BeforeAll
    static void setupTestData() {
        System.out.println("=== Setting up test data ===");
        testUser = Users.builder()
                .username("testuser")
                .password("password123")
                .adminFlag(false)
                .build();

        testAdminUser = Users.builder()
                .username("adminuser")
                .password("adminpass123")
                .adminFlag(true)
                .build();

        testJournalEntry = JournalEntry.builder()
                .id(String.valueOf(ObjectId.get()))
                .title("Test Journal Entry")
                .content("This is a test journal entry")
                .tags(List.of("test", "sample"))
                .viewCount(0)
                .build();
    }

    @AfterAll
    static void cleanupTestData() {
        System.out.println("=== Cleaning up test data ===");
        testUser = null;
        testAdminUser = null;
        testJournalEntry = null;
    }

    @BeforeEach
    @Transactional
    void setup() {
        // Initialize tracking lists for this test
        createdUserIds = new ArrayList<>();
        createdJournalEntryIds = new ArrayList<>();
    }

    @AfterEach
    @Transactional
    void teardown() {
        // Delete only the entries created by this specific test
        if (createdUserIds != null && !createdUserIds.isEmpty()) {
            for (ObjectId userId : createdUserIds) {
                userRepository.deleteById(userId);
            }
            createdUserIds.clear();
        }

        if (createdJournalEntryIds != null && !createdJournalEntryIds.isEmpty()) {
            for (String entryId : createdJournalEntryIds) {
                journalEntryRepository.deleteById(entryId);
            }
            createdJournalEntryIds.clear();
        }
    }

    // ==================== SAVE USER TESTS ====================

    @Test
    @Transactional
    void testSaveUserSuccess() {
        Users user = Users.builder()
                .username("newuser")
                .password("password123")
                .adminFlag(false)
                .build();

        boolean result = userService.saveUser(user);

        assertTrue(result);
        Users savedUser = userRepository.findByUsername("newuser");
        assertNotNull(savedUser);
        createdUserIds.add(savedUser.getId());
        assertEquals(List.of("USER"), savedUser.getRoles());
    }

    @Test
    @Transactional
    void testSaveAdminUserSuccess() {
        Users user = Users.builder()
                .username("adminuser")
                .password("adminpass")
                .adminFlag(true)
                .build();

        boolean result = userService.saveUser(user);

        assertTrue(result);
        Users savedUser = userRepository.findByUsername("adminuser");
        assertNotNull(savedUser);
        createdUserIds.add(savedUser.getId());
        assertEquals(List.of("USER", "ADMIN"), savedUser.getRoles());
    }

    @Test
    @Transactional
    void testSaveUserWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null));
    }

    @Test
    @Transactional
    void testSaveUserTrimsUsername() {
        Users user = Users.builder()
                .username("  trimmeduser  ")
                .password("password123")
                .adminFlag(false)
                .build();

        userService.saveUser(user);

        Users savedUser = userRepository.findByUsername("trimmeduser");
        assertNotNull(savedUser);
        createdUserIds.add(savedUser.getId());
        assertEquals("trimmeduser", savedUser.getUsername());
    }

    @Test
    @Transactional
    void testSaveUserEncodesPassword() {
        Users user = Users.builder()
                .username("pwduser")
                .password("password123")
                .adminFlag(false)
                .build();

        userService.saveUser(user);

        Users savedUser = userRepository.findByUsername("pwduser");
        assertNotNull(savedUser);
        createdUserIds.add(savedUser.getId());
        assertNotEquals("password123", savedUser.getPassword());
    }

    // ==================== UPDATE USER TESTS ====================

    @Test
    @Transactional
    void testUpdateUserSuccess() {
        Users user = Users.builder()
                .username("updateuser")
                .password("password123")
                .adminFlag(false)
                .build();
        userService.saveUser(user);

        Users retrievedUser = userRepository.findByUsername("updateuser");
        createdUserIds.add(retrievedUser.getId());
        retrievedUser.setPassword("newencodedpassword");

        boolean result = userService.updateUser(retrievedUser);

        assertTrue(result);
        assertNotNull(userRepository.findById(retrievedUser.getId()));
    }

    @Test
    @Transactional
    void testUpdateUserWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
    }

    @Test
    @Transactional
    void testUpdateUserTrimsUsername() {
        Users user = Users.builder()
                .username("oldname")
                .password("encoded")
                .adminFlag(false)
                .build();
        userService.saveUser(user);

        Users retrievedUser = userRepository.findByUsername("oldname");
        createdUserIds.add(retrievedUser.getId());
        retrievedUser.setUsername("  newname  ");
        userService.updateUser(retrievedUser);

        Users updated = userRepository.findById(retrievedUser.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("newname", updated.getUsername());
    }

    // ==================== FIND BY USERNAME TESTS ====================

    @Test
    @Transactional
    void testFindByUsernameSuccess() {
        Users user = Users.builder()
                .username("finduser")
                .password("password123")
                .adminFlag(false)
                .build();
        userService.saveUser(user);

        Users foundUser = userService.findByUserName("finduser");

        createdUserIds.add(foundUser.getId());
        assertNotNull(foundUser);
        assertEquals("finduser", foundUser.getUsername());
    }

    @Test
    @Transactional
    void testFindByUsernameNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.findByUserName("nonexistent"));
    }

    @Test
    @Transactional
    void testFindByUsernameWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.findByUserName(null));
    }

    @Test
    @Transactional
    void testFindByUsernameWithEmptyStringThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.findByUserName(""));
    }

    @Test
    @Transactional
    void testFindByUsernameWithWhitespaceThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.findByUserName("   "));
    }

    // ==================== GET ALL TESTS ====================

    @Test
    @Transactional
    void testGetAllSuccess() {
        Users user1 = Users.builder()
                .username("user1")
                .password("password123")
                .adminFlag(false)
                .build();
        Users user2 = Users.builder()
                .username("user2")
                .password("password456")
                .adminFlag(true)
                .build();

        userService.saveUser(user1);
        userService.saveUser(user2);

        List<Users> allUsers = userService.getAll();

        assertNotNull(allUsers);
        // assertEquals(2, allUsers.size());
        createdUserIds.add(allUsers.get(0).getId());
        createdUserIds.add(allUsers.get(1).getId());
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    @Transactional
    void testFindByIdSuccess() {
        Users user = Users.builder()
                .username("iduser")
                .password("password123")
                .adminFlag(false)
                .build();
        userService.saveUser(user);
        Users savedUser = userRepository.findByUsername("iduser");
        createdUserIds.add(savedUser.getId());

        Optional<Users> foundUser = userService.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("iduser", foundUser.get().getUsername());
    }

    @Test
    @Transactional
    void testFindByIdNotFound() {
        ObjectId randomId = ObjectId.get();
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(randomId));
    }

    @Test
    @Transactional
    void testFindByIdWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.findById(null));
    }

    // ==================== DELETE BY ID TESTS ====================

    @Test
    @Transactional
    void testDeleteByIdSuccess() {
        Users user = Users.builder()
                .username("deleteuser")
                .password("password123")
                .adminFlag(false)
                .build();
        userService.saveUser(user);
        Users savedUser = userRepository.findByUsername("deleteuser");

        boolean result = userService.deleteById(savedUser.getId());

        assertTrue(result);
        assertNull(userRepository.findByUsername("deleteuser"));
        // Don't add to createdUserIds since we're deleting it manually
    }

    @Test
    @Transactional
    void testDeleteByIdWithJournalEntries() {
        Users user = Users.builder()
                .username("userwithjournal")
                .password("password123")
                .adminFlag(false)
                .build();
        userService.saveUser(user);
        Users savedUser = userRepository.findByUsername("userwithjournal");

        JournalEntry entry1 = JournalEntry.builder()
                .id(String.valueOf(ObjectId.get()))
                .title("Entry 1")
                .content("Content 1")
                .tags(List.of("tag1"))
                .viewCount(0)
                .build();
        JournalEntry entry2 = JournalEntry.builder()
                .id(String.valueOf(ObjectId.get()))
                .title("Entry 2")
                .content("Content 2")
                .tags(List.of("tag2"))
                .viewCount(0)
                .build();

        journalEntryRepository.save(entry1);
        journalEntryRepository.save(entry2);
        createdJournalEntryIds.add(entry1.getId());
        createdJournalEntryIds.add(entry2.getId());

        savedUser.setJournalEntries(List.of(entry1, entry2));
        userRepository.save(savedUser);

        boolean result = userService.deleteById(savedUser.getId());

        assertTrue(result);
        assertNull(userRepository.findByUsername("userwithjournal"));
        createdJournalEntryIds.clear(); // They're already deleted
    }

    @Test
    @Transactional
    void testDeleteByIdNotFound() {
        ObjectId randomId = ObjectId.get();
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(randomId));
    }

    @Test
    @Transactional
    void testDeleteByIdWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.deleteById(null));
    }
}
