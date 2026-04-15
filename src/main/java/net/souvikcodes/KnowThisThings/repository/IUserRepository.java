package net.souvikcodes.KnowThisThings.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.souvikcodes.KnowThisThings.entity.Users;

@Repository
public interface IUserRepository extends MongoRepository<Users, ObjectId> {
    Users findByUsername(String username);
    void deleteById(ObjectId id);
    void deleteByUsername(String username);

}
