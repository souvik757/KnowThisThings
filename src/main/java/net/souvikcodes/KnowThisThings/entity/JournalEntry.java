package net.souvikcodes.KnowThisThings.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "journal_entries")
@Getter
@Setter
public class JournalEntry {
    @Id
    private String id;
    private String title;
    private String content;
    private List<String> tags;
    private Integer viewCount;
    @CreatedDate
    private LocalDateTime createdAt;
}