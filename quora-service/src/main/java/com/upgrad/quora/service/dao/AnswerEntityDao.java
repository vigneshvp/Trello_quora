package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerEntityDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity getAnswerByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class)
                                .setParameter("uuid", uuid)
                                .getSingleResult();
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public List<AnswerEntity> getAnswersByQuestionId(final QuestionEntity question) {
        try {
            return entityManager.createNamedQuery("answerByQuestionId", AnswerEntity.class)
                                .setParameter("question", question)
                                .getResultList();
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity createAnswer(final AnswerEntity answer) {
        entityManager.persist(answer);
        return answer;
    }

    public AnswerEntity editAnswer(final AnswerEntity answer) {
        try {
            return entityManager.merge(answer);
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public void deleteQuestion(final AnswerEntity answer) {
        entityManager.remove(answer);
    }
}
