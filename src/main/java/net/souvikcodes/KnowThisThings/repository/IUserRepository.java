package net.souvikcodes.KnowThisThings.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import net.souvikcodes.KnowThisThings.entity.Users;

public interface IUserRepository extends MongoRepository<Users, ObjectId> {
    Users findByUsername(String username);

    void deleteById(ObjectId id);

    void deleteByUsername(String username);

}
