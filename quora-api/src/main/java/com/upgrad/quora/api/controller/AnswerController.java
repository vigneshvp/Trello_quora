package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    private static final Logger log = LoggerFactory.getLogger(AnswerController.class);

    private final QuestionBusinessService questionBusinessService;
    private final AnswerBusinessService answerBusinessService;

    @Autowired
    public AnswerController(final QuestionBusinessService questionBusinessService,
                            final AnswerBusinessService answerBusinessService) {
        this.questionBusinessService = questionBusinessService;
        this.answerBusinessService = answerBusinessService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "question/{questionId}/answer/create",
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization,
                                                       @PathVariable final String questionId,
                                                       final AnswerRequest answerRequest)
            throws AuthorizationFailedException, InvalidQuestionException {
        log.debug("[AnswerController] createQuestion");

        final UsersEntity user = questionBusinessService.getUser(authorization);
        final QuestionEntity question = questionBusinessService.getQuestionByUuid(questionId);

        final AnswerEntity answer = new AnswerEntity();
        answer.setUuid(UUID.randomUUID().toString());
        answer.setAns(answerRequest.getAnswer());
        answer.setDate(ZonedDateTime.now());
        answer.setUser(user);
        answer.setQuestion(question);

        final AnswerEntity createdAnswer = answerBusinessService.createAnswer(answer);
        final AnswerResponse response = new AnswerResponse()
                .id(createdAnswer.getUuid())
                .status("ANSWER CREATED");
        log.info("[AnswerController] Question Created. Id - {}", createdAnswer.getUuid());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
