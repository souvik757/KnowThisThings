package net.souvikcodes.KnowThisThings.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import net.souvikcodes.KnowThisThings.entity.Users;

public interface IUserService {
    public void saveUser(Users user);
    public List<Users> getAll();
    public Optional<Users> findById(ObjectId id);
    public void deleteById(ObjectId id);
    public Users findByUserName(String userName);
}
