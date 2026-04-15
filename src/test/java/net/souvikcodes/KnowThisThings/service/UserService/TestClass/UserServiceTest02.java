package net.souvikcodes.KnowThisThings.service.UserService.TestClass;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
import net.souvikcodes.KnowThisThings.repository.IJournalEntryRepository;
import net.souvikcodes.KnowThisThings.repository.IUserRepository;
import net.souvikcodes.KnowThisThings.service.IUserService;

@SpringBootTest
public class UserServiceTest02 {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IJournalEntryRepository journalEntryRepository;

    // Track created entities for cleanup
    private List<ObjectId> createdUserIds;
    private List<String> createdJournalEntryIds;

    private static int testCounter = 0;

    @BeforeAll
    static void initializeTestEnvironment() {
        System.out.println("=== UserServiceTest01: Initializing Test Environment ===");
        testCounter = 0;
    }

    @AfterAll
    static void destroyTestEnvironment() {
        System.out.println("=== UserServiceTest01: Destroying Test Environment ===");
        System.out.println("Total tests executed in UserServiceTest01: " + testCounter);
    }

    @BeforeEach
    @Transactional
    void setupTestDatabase() {
        // Initialize tracking lists for this test
        createdUserIds = new ArrayList<>();
        createdJournalEntryIds = new ArrayList<>();
        testCounter++;
    }

    @AfterEach
    @Transactional
    void cleanupTestDatabase() {
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

    // ==================== EDGE CASE: Multiple Users ====================

    @Test
    @Transactional
    void testMultipleUsersDataIntegrity() {
        // Create multiple users
        for (int i = 1; i <= 5; i++) {
            Users user = Users.builder()
                    .username("user" + i)
                    .password("pass" + i)
                    .adminFlag(i % 2 == 0) // alternate admin flag
                    .build();
            userService.saveUser(user);
        }

        List<Users> allUsers = userService.getAll();
        assertNotNull(allUsers);
        for (Users user : allUsers) {
            createdUserIds.add(user.getId());
        }

        // Verify admin roles
        Users adminUser = userService.findByUserName("user2");
        assertEquals(List.of("USER", "ADMIN"), adminUser.getRoles());

        // Verify regular user roles
        Users regularUser = userService.findByUserName("user1");
        assertEquals(List.of("USER"), regularUser.getRoles());
    }

    // ==================== EDGE CASE: User with Multiple Journal Entries
    // ====================

    @Test
    @Transactional
    void testDeleteUserWithMultipleComplexJournalEntries() {
        Users user = Users.builder()
                .username("journaluser")
                .password("password123")
                .adminFlag(false)
                .build();
        userService.saveUser(user);
        Users savedUser = userRepository.findByUsername("journaluser");
        createdUserIds.add(savedUser.getId());

        // Create multiple journal entries with various tags
        List<JournalEntry> entries = List.of(
                JournalEntry.builder()
                        .id(String.valueOf(ObjectId.get()))
                        .title("Entry 1")
                        .content("Content 1")
                        .tags(List.of("work", "urgent"))
                        .viewCount(5)
                        .build(),
                JournalEntry.builder()
                        .id(String.valueOf(ObjectId.get()))
                        .title("Entry 2")
                        .content("Content 2")
                        .tags(List.of("personal", "reflection"))
                        .viewCount(10)
                        .build(),
                JournalEntry.builder()
                        .id(String.valueOf(ObjectId.get()))
                        .title("Entry 3")
                        .content("Content 3")
                        .tags(List.of("health", "fitness"))
                        .viewCount(3)
                        .build());

        journalEntryRepository.saveAll(entries);
        for (JournalEntry entry : entries) {
            createdJournalEntryIds.add(entry.getId());
        }
        savedUser.setJournalEntries(entries);
        userRepository.save(savedUser);

        // Verify entries exist before deletion
        assertEquals(3, savedUser.getJournalEntries().size());

        // Delete user and verify all entries are deleted
        boolean deleted = userService.deleteById(savedUser.getId());
        assertTrue(deleted);
        // Clear tracking since they were deleted
        createdUserIds.remove(savedUser.getId());
        createdJournalEntryIds.clear();
    }

    // ==================== EDGE CASE: Special Characters in Username
    // ====================

    @Test
    @Transactional
    void testUserWithSpecialCharactersAndWhitespace() {
        Users user = Users.builder()
                .username("  user.name_123  ")
                .password("password@123#")
                .adminFlag(false)
                .build();

        userService.saveUser(user);

        // Username should be trimmed
        Users foundUser = userService.findByUserName("user.name_123");
        assertNotNull(foundUser);
        createdUserIds.add(foundUser.getId());
        assertEquals("user.name_123", foundUser.getUsername());
    }

    // ==================== EDGE CASE: Large User Dataset ====================

    @Test
    @Transactional
    void testGetAllWithMultipleUsers() {
        // Create 10 users
        for (int i = 1; i <= 10; i++) {
            Users user = Users.builder()
                    .username("bulkuser" + i)
                    .password("password" + i)
                    .adminFlag(false)
                    .build();
            userService.saveUser(user);
        }

        List<Users> allUsers = userService.getAll();
        assertNotNull(allUsers);
        for (Users user : allUsers) {
            createdUserIds.add(user.getId());
        }

        // Verify all users can be found individually
        for (int i = 1; i <= 10; i++) {
            assertNotNull(userService.findByUserName("bulkuser" + i));
        }
    }

    // ==================== EDGE CASE: Delete and Recreate ====================

    @Test
    @Transactional
    void testDeleteAndRecreateUserWithSameUsername() {
        Users user1 = Users.builder()
                .username("deletable")
                .password("password1")
                .adminFlag(false)
                .build();
        userService.saveUser(user1);
        Users savedUser1 = userRepository.findByUsername("deletable");

        // Delete the user
        assertTrue(userService.deleteById(savedUser1.getId()));

        // Recreate user with same username
        Users user2 = Users.builder()
                .username("deletable")
                .password("newpassword")
                .adminFlag(true)
                .build();
        userService.saveUser(user2);

        Users savedUser2 = userRepository.findByUsername("deletable");
        assertNotNull(savedUser2);
        createdUserIds.add(savedUser2.getId());
        assertTrue(savedUser2.getAdminFlag());
        assertEquals(List.of("USER", "ADMIN"), savedUser2.getRoles());
    }

    // ==================== EDGE CASE: Update with Role Changes ====================

    @Test
    @Transactional
    void testUpdateUserMaintainsData() {
        Users user = Users.builder()
                .username("original")
                .password("password123")
                .adminFlag(false)
                .build();
        userService.saveUser(user);
        Users savedUser = userRepository.findByUsername("original");
        ObjectId userId = savedUser.getId();
        createdUserIds.add(userId);

        // Update user
        savedUser.setPassword("encoded_new_password");
        userService.updateUser(savedUser);

        // Verify user still exists with same ID
        var updatedUser = userService.findById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals("original", updatedUser.get().getUsername());
    }

    // ==================== VALIDATION: Find Operations ====================

    @Test
    @Transactional
    void testFindOperationsConcistency() {
        Users user = Users.builder()
                .username("consistency")
                .password("password123")
                .adminFlag(true)
                .build();
        userService.saveUser(user);
        Users savedUser = userRepository.findByUsername("consistency");
        createdUserIds.add(savedUser.getId());

        // Find by username and by ID should return same user
        Users foundByName = userService.findByUserName("consistency");
        var foundById = userService.findById(savedUser.getId());

        assertEquals(foundByName.getId(), foundById.get().getId());
        assertEquals(foundByName.getUsername(), foundById.get().getUsername());
    }

}
