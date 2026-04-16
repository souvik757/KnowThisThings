package net.souvikcodes.KnowThisThings.service.Implementation;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.entity.JournalEntry;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.exception.customexception.ResourceNotFoundException;
import net.souvikcodes.KnowThisThings.repository.IJournalEntryRepository;
import net.souvikcodes.KnowThisThings.repository.IUserRepository;
import net.souvikcodes.KnowThisThings.service.IUserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IJournalEntryRepository journalEntryRepository;
    private static final PasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    @Override
    @Transactional
    public boolean saveUser(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        user.setUsername(user.getUsername().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(
                user.getAdminFlag() ? List.of("USER", "ADMIN") : List.of("USER"));
        userRepository.save(user);
        return true;
    }

    // updating user without encoding password (for updates, when password is
    // already encoded)

    @Override
    @Transactional
    public boolean updateUser(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        user.setUsername(user.getUsername().trim());
        // password is already encoded
        userRepository.save(user);

        return true;
    }

    @Override
    public Users findByUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        Users user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + userName);
        }
        return user;
    }

    @Override
    public List<Users> getAll() {

        List<Users> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users;
    }

    @Override
    public Optional<Users> findById(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Optional<Users> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    @Override
    @Transactional
    public boolean deleteById(ObjectId id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        // delete every journal entry of the user as well
        List<JournalEntry> journalEntries = findById(id).get().getJournalEntries();
        journalEntryRepository.deleteAll(journalEntries);
        userRepository.deleteById(id);
        return true;
    }

}
