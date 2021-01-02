package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    private final QuestionDao questionDao;

    @Autowired
    public QuestionBusinessService(final QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity question) {
        return questionDao.createQuestion(question);
    }

    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final String uuid, final String content) {
        final QuestionEntity questionInDb = questionDao.getQuestionByUuid(uuid);
        questionInDb.setContent(content);

        return questionDao.editQuestion(questionInDb);
    }
}
