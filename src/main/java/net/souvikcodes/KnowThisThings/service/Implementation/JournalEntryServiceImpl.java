package net.souvikcodes.KnowThisThings.service.Implementation;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // GET MAPPINGS IMPLEMENTATIONS
    @Override
    public List<JournalEntryDto> getAllJournalEntries() {
        List<JournalEntry> journalEntries = journalEntryRepository.findAll();
        return journalEntries.stream()
                .map(journals -> modelMapper.map(journals, JournalEntryDto.class))
                .toList();
    }

    @Override
    public List<JournalEntryDto> getAllJournalEntriesForUser(String username) {
        Users user = verifyUserIsAuthenticated(username);
        List<JournalEntry> journalEntries = user.getJournalEntries();
        if (journalEntries == null || journalEntries.isEmpty()) {
            return List.of();
        }
        return journalEntries.stream()
                .map(entry -> modelMapper.map(entry, JournalEntryDto.class))
                .toList();
    }

    @Override
    public JournalEntryDto getJournalEntryById(String id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id).orElse(null);

        return modelMapper.map(journalEntry, JournalEntryDto.class);
    }



    // POST, PUT, DELETE MAPPINGS IMPLEMENTATIONS
    @Override
    @Transactional
    public JournalEntryDto createJournalEntryForUser(JournalEntryDto journalEntryDto, String username) {
        Users user = verifyUserIsAuthenticated(username);
        JournalEntry journalEntry = modelMapper.map(journalEntryDto, JournalEntry.class);
        JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);
        associateJournalWithUser(savedJournalEntry, user);
        return modelMapper.map(savedJournalEntry, JournalEntryDto.class);
    }

    @Override
    @Transactional
    public JournalEntryDto updateJournalEntryForUser(String username, String id, JournalEntryDto journalEntryDto) {
        Users user = verifyUserIsAuthenticated(username);
        JournalEntry existingJournalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (!user.getJournalEntries().stream().anyMatch(entry -> entry.getId().equals(id))) {
            throw new JournalEntryException("Journal entry does not belong to user: " + username);
        }

        existingJournalEntry.setTitle(journalEntryDto.getTitle());
        existingJournalEntry.setContent(journalEntryDto.getContent());

        JournalEntry updatedJournalEntry = journalEntryRepository.save(existingJournalEntry);
        return modelMapper.map(updatedJournalEntry, JournalEntryDto.class);
    }
    
    @Override
    @Transactional
    public void deleteJournalEntryForUser(String username, String id) {
        Users user = verifyUserIsAuthenticated(username);
        JournalEntry existingJournalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (!user.getJournalEntries().stream().anyMatch(entry -> entry.getId().equals(id))) {
            throw new JournalEntryException("Journal entry does not belong to user: " + username);
        }

        // Remove from user's journal list
        user.getJournalEntries().remove(existingJournalEntry);
        userService.updateUser(user);
        
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
    // helper implementations
    private Users verifyUserIsAuthenticated(String username){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // System.out.println("DEBUG - Authenticated user: '" + currentUsername + "'");
        // System.out.println("DEBUG - Requested user: '" + username + "'");
        
        Users user = verifyUserByUsername(username);
        
        // Compare case-insensitively to avoid auth issues
        if (!currentUsername.equalsIgnoreCase(username)) {
            throw new JournalEntryException("Unauthorized access to journal entries for user: " + username + 
                ". Authenticated as: " + currentUsername);
        }
        return user;
    }

    private Users verifyUserByUsername(String username) {
        return userService.findByUserName(username);
    }

    private void associateJournalWithUser(JournalEntry journalEntry, Users user) {
        if (user.getJournalEntries() == null) {
            user.setJournalEntries(new ArrayList<>());
        }
        user.getJournalEntries().add(journalEntry);
        userService.updateUser(user);
    }
}