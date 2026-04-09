package net.souvikcodes.KnowThisThings.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.souvikcodes.KnowThisThings.entity.JournalEntry;

@Repository
public interface IJournalEntryRepository extends MongoRepository<JournalEntry, String> {

}
