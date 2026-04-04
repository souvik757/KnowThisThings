package net.souvikcodes.KnowThisThings.service;

import java.util.List;

import net.souvikcodes.KnowThisThings.dto.JournalEntryAdminDto;
import net.souvikcodes.KnowThisThings.dto.JournalEntryDto;


public interface IJournalEntryService {
    public JournalEntryDto createJournalEntryForUser(JournalEntryDto journalEntryDto, String username) ;
    public JournalEntryDto getJournalEntryById(String id) ;
    public List<JournalEntryDto> getAllJournalEntries() ;
    public JournalEntryDto updateJournalEntryForUser(String username, String id, JournalEntryDto journalEntryDto) ;
    public void deleteJournalEntryForUser(String username, String id) ;
    public List<JournalEntryDto> getAllJournalEntriesForUser(String username) ;




    // For admin only
    public List<JournalEntryAdminDto> getJournalEntryByIdForAdmin() ;
}
