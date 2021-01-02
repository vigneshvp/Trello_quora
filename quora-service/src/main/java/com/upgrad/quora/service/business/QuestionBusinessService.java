package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionEntityDao;
import com.upgrad.quora.service.dao.UsersEntityDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    private final QuestionEntityDao questionEntityDao;
    private final UsersEntityDao usersEntityDao;

    @Autowired
    public QuestionBusinessService(final QuestionEntityDao questionEntityDao,
                                   final UsersEntityDao usersEntityDao) {
        this.questionEntityDao = questionEntityDao;
        this.usersEntityDao = usersEntityDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity question) {
        return questionEntityDao.createQuestion(question);
    }

    public List<QuestionEntity> getAllQuestions() {
        return questionEntityDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final String uuid, final String content) {
        final QuestionEntity questionInDb = questionEntityDao.getQuestionByUuid(uuid);
        questionInDb.setContent(content);
        return questionEntityDao.editQuestion(questionInDb);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestionByUuid(final String uuid) throws InvalidQuestionException {
        final QuestionEntity questionInDb = questionEntityDao.getQuestionByUuid(uuid);
        if (null == questionInDb) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        questionEntityDao.deleteQuestion(questionInDb);
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String userId) throws UserNotFoundException {
        final UsersEntity user = usersEntityDao.getUserByUuid(userId);
        if (null == user) {
            throw new UserNotFoundException("USR-001",
                                            "User with entered uuid whose question details are to be seen does not " +
                                            "exist");
        }
        return questionEntityDao.getAllQuestionsByUser(user);
    }
}
