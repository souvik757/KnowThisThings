package net.souvikcodes.KnowThisThings.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.dto.JournalEntryAdminDto;
import net.souvikcodes.KnowThisThings.dto.JournalEntryDto;
import net.souvikcodes.KnowThisThings.entity.JournalEntry;
import net.souvikcodes.KnowThisThings.exception.customexception.JournalEntryException;
import net.souvikcodes.KnowThisThings.exception.customexception.ResourceNotFoundException;
import net.souvikcodes.KnowThisThings.repository.IJournalEntryRepository;

@Service
@RequiredArgsConstructor
public class JournalEntryServiceImpl implements IJournalEntryService {

    private final IJournalEntryRepository journalEntryRepository;
    private final ModelMapper modelMapper;

    @Override
    public JournalEntryDto createJournalEntry(JournalEntryDto journalEntryDto) {

        JournalEntry journalEntry = modelMapper.map(journalEntryDto, JournalEntry.class);
        JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);

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
    public JournalEntryDto updateJournalEntry(String id, JournalEntryDto journalEntryDto) {
        JournalEntry existingJournalEntry = journalEntryRepository.findById(id)
                                            .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));
        modelMapper.map(journalEntryDto, existingJournalEntry);
        JournalEntry updatedJournalEntry = journalEntryRepository.save(existingJournalEntry);
        return modelMapper.map(updatedJournalEntry, JournalEntryDto.class);
    }

    @Override
    public void deleteJournalEntry(String id) {
        if (journalEntryRepository.existsById(id)) {
            journalEntryRepository.deleteById(id);
        }
        throw new JournalEntryException("Journal entry not found with id: " + id);
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
}
