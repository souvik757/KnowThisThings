package net.souvikcodes.KnowThisThings.repository.Implementation;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import net.souvikcodes.KnowThisThings.Util.Constants.DbConst;
import net.souvikcodes.KnowThisThings.entity.Users;
import net.souvikcodes.KnowThisThings.repository.ICustomUserRepository;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements ICustomUserRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Users> findUsersForSA() {
        Query query = new Query();
        query.addCriteria(Criteria.where(DbConst.HAS_SENTIMENT_ANALYSIS).exists(true).ne(false));
        query.addCriteria(Criteria.where(DbConst.EMAIL).exists(true).ne(""));
        query.addCriteria(Criteria.where(DbConst.ROLE).in(DbConst.ROLES[0]));
        return mongoTemplate.find(query, Users.class);
    }
}
