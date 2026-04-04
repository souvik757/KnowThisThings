package net.souvikcodes.KnowThisThings.service.Implementation;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.dto.JournalEntryAdminDto;
import net.souvikcodes.KnowThisThings.dto.JournalEntryDto;
import net.souvikcodes.KnowThisThings.entity.JournalEntry;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.exception.customexception.JournalEntryException;
import net.souvikcodes.KnowThisThings.exception.customexception.ResourceNotFoundException;
import net.souvikcodes.KnowThisThings.repository.IJournalEntryRepository;
import net.souvikcodes.KnowThisThings.service.IJournalEntryService;
import net.souvikcodes.KnowThisThings.service.IUserService;

@Service
@RequiredArgsConstructor
public class JournalEntryServiceImpl implements IJournalEntryService {

    private final IJournalEntryRepository journalEntryRepository;
    private final ModelMapper modelMapper;
    private final IUserService userService;

    @Override
    public JournalEntryDto createJournalEntryForUser(JournalEntryDto journalEntryDto, String username) {
        Users user = verifyUserByUsername(username);
        JournalEntry journalEntry = modelMapper.map(journalEntryDto, JournalEntry.class);
        JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);
        associateJournalWithUser(savedJournalEntry, user);
        return modelMapper.map(savedJournalEntry, JournalEntryDto.class);
    }

    @Override
    public JournalEntryDto getJournalEntryById(String id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id).orElse(null);

        return modelMapper.map(journalEntry, JournalEntryDto.class);
    }

    @Override
    public List<JournalEntryDto> getAllJournalEntries() {
        List<JournalEntry> journalEntries = journalEntryRepository.findAll();
        return journalEntries.stream()
                .map(journals -> modelMapper.map(journals, JournalEntryDto.class))
                .toList();
    }

    @Override
    public List<JournalEntryDto> getAllJournalEntriesForUser(String username) {
        Users user = verifyUserByUsername(username);
        List<JournalEntry> journalEntries = user.getJournalEntries();
        if (journalEntries == null || journalEntries.isEmpty()) {
            return List.of();
        }
        return journalEntries.stream()
                .map(entry -> modelMapper.map(entry, JournalEntryDto.class))
                .toList();
    }

    @Override
    public JournalEntryDto updateJournalEntryForUser(String username, String id, JournalEntryDto journalEntryDto) {
        Users user = verifyUserByUsername(username);
        JournalEntry existingJournalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (!user.getJournalEntries().contains(existingJournalEntry)) {
            throw new JournalEntryException("Journal entry does not belong to user: " + username);
        }

        existingJournalEntry.setTitle(journalEntryDto.getTitle());
        existingJournalEntry.setContent(journalEntryDto.getContent());

        JournalEntry updatedJournalEntry = journalEntryRepository.save(existingJournalEntry);
        return modelMapper.map(updatedJournalEntry, JournalEntryDto.class);
    }

    @Override
    public void deleteJournalEntryForUser(String username, String id) {
        Users user = verifyUserByUsername(username);
        JournalEntry existingJournalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (!user.getJournalEntries().contains(existingJournalEntry)) {
            throw new JournalEntryException("Journal entry does not belong to user: " + username);
        }

        // Remove from user's journal list
        user.getJournalEntries().remove(existingJournalEntry);
        userService.saveUser(user);
        
        // Delete from repository
        journalEntryRepository.deleteById(id);
    }

    // For admin only
    @Override
    public List<JournalEntryAdminDto> getJournalEntryByIdForAdmin() {
        List<JournalEntry> journalEntries = journalEntryRepository.findAll();
        if (journalEntries.isEmpty()) {
            throw new ResourceNotFoundException("No journal entries found");
        }
        return journalEntries.stream()
                .map(journals -> modelMapper.map(journals, JournalEntryAdminDto.class))
                .toList();
    }

    /**
     * Verify if a user exists by username
     */
    public Users verifyUserByUsername(String username) {
        return userService.findByUserName(username);
    }

    /**
     * Verify if a user exists by ID
     */
    public Users verifyUserById(ObjectId userId) {
        return userService.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found with id: " + userId)
        );
    }

    /**
     * Add journal entry to user's journal list
     */
    public void associateJournalWithUser(JournalEntry journalEntry, Users user) {
        if (user.getJournalEntries() == null) {
            user.setJournalEntries(new ArrayList<>());
        }
        user.getJournalEntries().add(journalEntry);
        userService.saveUser(user);
    }

    /**
     * Get all journals for a specific user
     */
    public List<JournalEntryDto> getJournalsByUsername(String username) {
        Users user = verifyUserByUsername(username);
        List<JournalEntry> journalEntries = user.getJournalEntries();
        if (journalEntries == null || journalEntries.isEmpty()) {
            throw new ResourceNotFoundException("No journals found for user: " + username);
        }
        return journalEntries.stream()
                .map(entry -> modelMapper.map(entry, JournalEntryDto.class))
                .toList();
    }


}