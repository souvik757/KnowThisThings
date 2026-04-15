package net.souvikcodes.KnowThisThings.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import net.souvikcodes.KnowThisThings.entity.Users;

public interface IUserService {
    public boolean saveUser(Users user);
    public boolean updateUser(Users user);
    public List<Users> getAll();
    public Optional<Users> findById(ObjectId id);
    public boolean deleteById(ObjectId id);
    public Users findByUserName(String userName);
}
