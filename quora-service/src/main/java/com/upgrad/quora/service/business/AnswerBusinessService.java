package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerEntityDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerBusinessService {

    private static final Logger log = LoggerFactory.getLogger(AnswerBusinessService.class);

    private final AnswerEntityDao answerEntityDao;

    @Autowired
    public AnswerBusinessService(final AnswerEntityDao answerEntityDao) {
        this.answerEntityDao = answerEntityDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answer) {
        log.debug("[AnswerBusinessService] Create Answer.");
        return answerEntityDao.createQuestion(answer);
    }
}
