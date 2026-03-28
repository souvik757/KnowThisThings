package net.souvikcodes.KnowThisThings.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.dto.JournalEntryAdminDto;
import net.souvikcodes.KnowThisThings.dto.JournalEntryDto;
import net.souvikcodes.KnowThisThings.service.IJournalEntryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/journal-entries")
public class JournalEntryContoller {
    
    private final IJournalEntryService journalEntryService ;

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDto> getJournalEntryById(@PathVariable String id) {
        JournalEntryDto journalEntry = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(journalEntry);
    }

    @GetMapping
    public ResponseEntity<List<JournalEntryDto>> getAllJournalEntries() {
        return ResponseEntity.ok(journalEntryService.getAllJournalEntries());
    }

    @PostMapping
    public ResponseEntity<JournalEntryDto> createJournalEntry(@Valid @RequestBody JournalEntryDto journalEntryDto) {
        JournalEntryDto createdJournalEntry = journalEntryService.createJournalEntry(journalEntryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJournalEntry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalEntryDto> updateJournalEntry(@PathVariable String id, @Valid @RequestBody JournalEntryDto journalEntryDto) {
        JournalEntryDto updatedJournalEntry = journalEntryService.updateJournalEntry(id, journalEntryDto);
        return ResponseEntity.ok(updatedJournalEntry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJournalEntry(@PathVariable String id) {
        journalEntryService.deleteJournalEntry(id);
        return ResponseEntity.noContent().build();
    }


    // To see Journal with Id , for admin only
    @GetMapping("/admin")
    public ResponseEntity<List<JournalEntryAdminDto>> getJournalEntryByIdForAdmin() {
        List<JournalEntryAdminDto> journalEntries = journalEntryService.getJournalEntryByIdForAdmin();
        return ResponseEntity.ok(journalEntries);
    }
}
