package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
        try {
            return entityManager.createNamedQuery("userByUuid", UsersEntity.class)
                       .setParameter("uuid", userUuid)
                       .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    public UsersEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UsersEntity.class)
                       .setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    public UsersEntity getUserByUserName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUserName", UsersEntity.class)
                       .setParameter("username", userName).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }
    
    public void updateUser(final UsersEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }
    
    public void updateUserAuthEntity(final UserAuthEntity updatedUserAuthEntity) {
        entityManager.merge(updatedUserAuthEntity);
    }
    
    
    public UserAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager
                       .createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class)
                       .setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Transactional
    public void deleteUser(final UsersEntity userEntity) {
        entityManager.remove(userEntity);
    }
    
}
