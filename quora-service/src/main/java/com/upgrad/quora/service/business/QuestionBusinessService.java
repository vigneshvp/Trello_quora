package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionEntityDao;
import com.upgrad.quora.service.dao.UserAuthEntityDao;
import com.upgrad.quora.service.dao.UsersEntityDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {

    private static final Logger log = LoggerFactory.getLogger(QuestionBusinessService.class);

    private final QuestionEntityDao questionEntityDao;
    private final UsersEntityDao usersEntityDao;
    private final UserAuthEntityDao userAuthEntityDao;

    @Autowired
    public QuestionBusinessService(final QuestionEntityDao questionEntityDao,
                                   final UsersEntityDao usersEntityDao,
                                   final UserAuthEntityDao userAuthEntityDao) {
        this.questionEntityDao = questionEntityDao;
        this.usersEntityDao = usersEntityDao;
        this.userAuthEntityDao = userAuthEntityDao;
    }

    public UsersEntity getUser(final String authorizationToken) throws AuthorizationFailedException {
        String token = authorizationToken;
        if (authorizationToken.startsWith("Bearer")) {
            token = authorizationToken.split("Bearer ")[1];
        } else if (authorizationToken.startsWith("Basic")) {
            token = authorizationToken.split("Basic ")[1];
        }
        final UserAuthEntity userAuth = userAuthEntityDao.getUserAuth(token);

        if (null == userAuth) {
            log.error("AuthorizationFailedException : ATHR-001. User has not signed in");
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (null != userAuth.getLogoutAt() || userAuth.getExpiresAt().isBefore(ZonedDateTime.now())) {
            log.error("AuthorizationFailedException : ATHR-002. User is signed out.Sign in first to post a question");
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        return userAuth.getUser();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity question) {
        log.debug("[QuestionBusinessService] Create Question.");
        return questionEntityDao.createQuestion(question);
    }

    public List<QuestionEntity> getAllQuestions() {
        log.debug("[QuestionBusinessService] Get All Questions.");
        return questionEntityDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final UsersEntity user,
                                              final String uuid,
                                              final String content)
            throws InvalidQuestionException, AuthorizationFailedException {
        log.debug("[QuestionBusinessService] Edit Question.");

        final QuestionEntity questionInDb = questionEntityDao.getQuestionByUuid(uuid);
        if (null == questionInDb) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (!questionInDb.getUser().equals(user)) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionInDb.setContent(content);
        return questionEntityDao.editQuestion(questionInDb);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestionByUuid(final UsersEntity user, final String uuid)
            throws InvalidQuestionException, AuthorizationFailedException {
        log.debug("[QuestionBusinessService] Delete Question.");

        final QuestionEntity questionInDb = questionEntityDao.getQuestionByUuid(uuid);
        if (null == questionInDb) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (!user.equals(questionInDb.getUser()) && !user.getRole().equalsIgnoreCase("admin")) {
            throw new AuthorizationFailedException("ATHR-003",
                                                   "Only the question owner or admin can delete the question");
        }

        questionEntityDao.deleteQuestion(questionInDb);
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String userId) throws UserNotFoundException {
        log.debug("[QuestionBusinessService] Get all questions by user.");

        final UsersEntity user = usersEntityDao.getUserByUuid(userId);
        if (null == user) {
            throw new UserNotFoundException("USR-001",
                                            "User with entered uuid whose question details are to be seen does not " +
                                            "exist");
        }
        return questionEntityDao.getAllQuestionsByUser(user);
    }
}
