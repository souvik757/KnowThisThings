package net.souvikcodes.KnowThisThings.entity;

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
}