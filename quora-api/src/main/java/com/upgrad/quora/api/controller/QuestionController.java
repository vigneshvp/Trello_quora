package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionBusinessService questionBusinessService;

    @Autowired
    public QuestionController(final QuestionBusinessService questionBusinessService) {
        this.questionBusinessService = questionBusinessService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/create",
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest) {
        final QuestionEntity question = new QuestionEntity();
        question.setUuid(UUID.randomUUID().toString());
        question.setContent(questionRequest.getContent());
        question.setDate(ZonedDateTime.now());

        final UsersEntity user = new UsersEntity();
        user.setId(1029);
        question.setUser(user);

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(question);
        final QuestionResponse response = new QuestionResponse()
                .id(createdQuestion.getUuid())
                .status("QUESTION CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all",
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions() {
        final List<QuestionEntity> questionsInDb = questionBusinessService.getAllQuestions();
        final List<QuestionDetailsResponse> questions = getQuestionDetailsResponses(questionsInDb);
        return ResponseEntity.ok(questions);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/edit/{questionId}",
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<QuestionEditResponse> editQuestionContent(@PathVariable final String questionId,
                                                                final QuestionEditRequest question) {
        final QuestionEntity updatedQuestion =
                questionBusinessService.editQuestionContent(questionId, question.getContent());
        final QuestionEditResponse response = new QuestionEditResponse()
                .id(updatedQuestion.getUuid())
                .status("QUESTION EDITED");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}",
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable final String questionId)
            throws InvalidQuestionException {
        questionBusinessService.deleteQuestionByUuid(questionId);
        final QuestionDeleteResponse response = new QuestionDeleteResponse()
                .id(questionId)
                .status("QUESTION DELETED");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all/{userId}",
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable final String userId)
            throws UserNotFoundException {
        final List<QuestionEntity> questionsInDb = questionBusinessService.getAllQuestionsByUser(userId);
        final List<QuestionDetailsResponse> questions = getQuestionDetailsResponses(questionsInDb);
        return ResponseEntity.ok(questions);
    }

    private List<QuestionDetailsResponse> getQuestionDetailsResponses(final List<QuestionEntity> questionsInDb) {
        final List<QuestionDetailsResponse> questions = new ArrayList<>();
        if (null != questionsInDb && !questionsInDb.isEmpty()) {
            questionsInDb.forEach(question -> questions
                    .add(new QuestionDetailsResponse().id(question.getUuid()).content(question.getContent())));
        }
        return questions;
    }
}
