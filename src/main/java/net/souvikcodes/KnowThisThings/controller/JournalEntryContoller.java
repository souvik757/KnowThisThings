package net.souvikcodes.KnowThisThings.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // GET MAPPINGS
   // to get all journal entries of all users, Paginated entries will be added.
    @GetMapping("all")
    public ResponseEntity<List<JournalEntryDto>> getAllJournalEntries() {
        return ResponseEntity.ok(journalEntryService.getAllJournalEntries());
    }
    // to get all journal entries of a user
    @GetMapping("user")
    public ResponseEntity<List<JournalEntryDto>> getAllJournalEntriesForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName() ;
        return ResponseEntity.ok(journalEntryService.getAllJournalEntriesForUser(username));
    }
    // it will be usefull to fetch a certain journal entry by id
    @GetMapping("/user/{id}")
    public ResponseEntity<JournalEntryDto> getJournalEntryById(@PathVariable String id) {
        JournalEntryDto journalEntry = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(journalEntry);
    }


    // POST MAPPINGS
    @PostMapping("user")
    public ResponseEntity<JournalEntryDto> createJournalEntryForUser(@Valid @RequestBody JournalEntryDto journalEntryDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName() ;
        JournalEntryDto createdJournalEntry = journalEntryService.createJournalEntryForUser(journalEntryDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJournalEntry);
    }
    // PUT MAPPINGS
    @PutMapping("/user/{id}")
    public ResponseEntity<JournalEntryDto> updateJournalEntryForUser(@PathVariable String id, @Valid @RequestBody JournalEntryDto journalEntryDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName() ;
        JournalEntryDto updatedJournalEntry = journalEntryService.updateJournalEntryForUser(username, id, journalEntryDto);
        return ResponseEntity.ok(updatedJournalEntry);
    }
    // DELETE MAPPINGS
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteJournalEntryForUser(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName() ;
        journalEntryService.deleteJournalEntryForUser(username, id);
        return ResponseEntity.noContent().build();
    }










































    // To see Journal with Id , for admin only
    @GetMapping("/admin")
    public ResponseEntity<List<JournalEntryAdminDto>> getJournalEntryByIdForAdmin() {
        List<JournalEntryAdminDto> journalEntries = journalEntryService.getJournalEntryByIdForAdmin();
        return ResponseEntity.ok(journalEntries);
    }

    // I will change it later to give this a proper endpoint and functionality, for now this is just to give an idea of how it can work. This will be used to delete any journal entry by admin.

    @GetMapping("/search")
    public ResponseEntity<List<JournalEntryDto>> searchJournals(@RequestParam String query) {
        List<JournalEntryDto> allJournals = journalEntryService.getAllJournalEntries();
        List<JournalEntryDto> filtered = allJournals.stream()
            .filter(j -> j.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        j.getContent().toLowerCase().contains(query.toLowerCase()) ||
                        (j.getTags() != null && j.getTags().stream()
                            .anyMatch(t -> t.toLowerCase().contains(query.toLowerCase()))))
            .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<JournalEntryDto>> getTrendingJournals() {
        List<JournalEntryDto> allJournals = journalEntryService.getAllJournalEntries();
        List<JournalEntryDto> trending = allJournals.stream()
            .sorted((a, b) -> {
                Integer viewA = a.getViewCount() != null ? a.getViewCount() : 0;
                Integer viewB = b.getViewCount() != null ? b.getViewCount() : 0;
                return Integer.compare(viewB, viewA);
            })
            .limit(10)
            .collect(Collectors.toList());
        return ResponseEntity.ok(trending);
    }

    /*
    I will make a proper chat system later, for now this is just a simple implementation to give an idea of how it can work. It will be a simple rule based system that will respond to certain keywords in the message. I will add more functionality to it later.
    */

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String response = generateChatResponse(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(response, request.getConversationId()));
    }

    private String generateChatResponse(String message) {
        String lowerMsg = message.toLowerCase();
        if (lowerMsg.contains("trending") || lowerMsg.contains("popular")) {
            return "The trending journals are sorted by view count. Check the 'Trending' tab to see the most read entries.";
        }
        if (lowerMsg.contains("search")) {
            return "You can search journals using keywords. The search looks through titles, content, and tags.";
        }
        if (lowerMsg.contains("create") || lowerMsg.contains("new")) {
            return "To create a new journal, go to the 'New journal' tab, fill in the title, content, and tags, then click 'Publish'.";
        }
        if (lowerMsg.contains("delete")) {
            return "To delete a journal, go to the 'Edit / Delete' tab, enter the journal ID, and click the 'Delete' button.";
        }
        if (lowerMsg.contains("edit") || lowerMsg.contains("update")) {
            return "To edit a journal, go to the 'Edit / Delete' tab, fetch the journal by ID, make your changes, and click 'Save changes'.";
        }
        return "I can help you with journals! Try asking about trending, searching, creating, editing, or deleting journals.";
    }

    public static class ChatRequest {
        private String message;
        private String conversationId;
        public ChatRequest() {}
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    }

    public static class ChatResponse {
        private String response;
        private String conversationId;
        public ChatResponse(String response, String conversationId) {
            this.response = response;
            this.conversationId = conversationId;
        }
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    }
}
