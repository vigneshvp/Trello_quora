package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UsersEntityDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UsersEntity getUserByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("usersByUuid", UsersEntity.class)
                                .setParameter("uuid", uuid)
                                .getSingleResult();
        } catch (final NoResultException nre) {
            return null;
        }
    }
}
