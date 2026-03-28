package net.souvikcodes.KnowThisThings.service;

import java.util.List;

import net.souvikcodes.KnowThisThings.dto.JournalEntryAdminDto;
import net.souvikcodes.KnowThisThings.dto.JournalEntryDto;


public interface IJournalEntryService {
    public JournalEntryDto createJournalEntry(JournalEntryDto journalEntryDto) ;
    public JournalEntryDto getJournalEntryById(String id) ;
    public List<JournalEntryDto> getAllJournalEntries() ;
    public JournalEntryDto updateJournalEntry(String id, JournalEntryDto journalEntryDto) ;
    public void deleteJournalEntry(String id) ;
    // For admin only
    public List<JournalEntryAdminDto> getJournalEntryByIdForAdmin() ;
}
