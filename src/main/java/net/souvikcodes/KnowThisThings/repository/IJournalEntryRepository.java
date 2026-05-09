package net.souvikcodes.KnowThisThings.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.souvikcodes.KnowThisThings.entity.JournalEntry;

public interface IJournalEntryRepository extends MongoRepository<JournalEntry, String> {

}
