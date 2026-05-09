package net.souvikcodes.KnowThisThings.repository;

import java.util.List;

import net.souvikcodes.KnowThisThings.entity.Users;

public interface ICustomUserRepository {
    public List<Users> findUsersForSA();
}
