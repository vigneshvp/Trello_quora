package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerEntityDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createQuestion(final AnswerEntity answer) {
        entityManager.persist(answer);
        return answer;
    }
}
