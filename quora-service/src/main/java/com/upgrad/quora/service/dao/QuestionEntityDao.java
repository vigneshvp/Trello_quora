package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionEntityDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class)
                                .getResultList();
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestionByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class)
                                .setParameter("uuid", uuid)
                                .getSingleResult();
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getAllQuestionsByUser(final UsersEntity user) {
        try {
            return entityManager.createNamedQuery("questionByUserId", QuestionEntity.class)
                                .setParameter("user", user)
                                .getResultList();
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity createQuestion(final QuestionEntity question) {
        entityManager.persist(question);
        return question;
    }

    public QuestionEntity editQuestion(final QuestionEntity question) {
        try {
            return entityManager.merge(question);
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public void deleteQuestion(final QuestionEntity question) {
        entityManager.remove(question);
    }
}
