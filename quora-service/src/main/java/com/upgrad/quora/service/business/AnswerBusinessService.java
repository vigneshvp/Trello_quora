package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerEntityDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
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
        return answerEntityDao.createAnswer(answer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final UsersEntity user,
                                          final String answerId,
                                          final String content)
            throws AnswerNotFoundException, AuthorizationFailedException {
        log.debug("[AnswerBusinessService] Edit Answer.");
        final AnswerEntity answerInDb = answerEntityDao.getAnswerByUuid(answerId);
        if (null == answerInDb) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        if (!user.equals(answerInDb.getUser())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        answerInDb.setAns(content);
        return answerEntityDao.editAnswer(answerInDb);
    }
}
