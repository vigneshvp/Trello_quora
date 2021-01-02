package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public UsersEntity createUser(UsersEntity usersEntity) {
        entityManager.persist(usersEntity);
        return usersEntity;
    }
    
    public UsersEntity getUser(final String userUuid) {
        return entityManager.createNamedQuery("userByUuid", UsersEntity.class)
                   .setParameter("uuid", userUuid)
                   .getSingleResult();
    }
    
    public UsersEntity getUserByEmail(final String email) {
        return entityManager.createNamedQuery("userByEmail", UsersEntity.class)
                   .setParameter("email", email).getSingleResult();
    }
    
    public UsersEntity getUserByUserName(final String userName) {
        return entityManager.createNamedQuery("userByUserName", UsersEntity.class)
                   .setParameter("username", userName).getSingleResult();
    }
    
    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }
    
    public void updateUser(final UsersEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }
    
    
    public UserAuthEntity getUserAuthToken(final String accessToken) {
        return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class)
                   .setParameter("accessToken", accessToken).getSingleResult();
    }
    
}
