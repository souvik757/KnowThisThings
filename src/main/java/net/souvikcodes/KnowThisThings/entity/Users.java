package net.souvikcodes.KnowThisThings.entity;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection = "users")
@Data
public class Users {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    @NotNull
    private String username;
    @NotNull
    private String password; 
    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();
}
