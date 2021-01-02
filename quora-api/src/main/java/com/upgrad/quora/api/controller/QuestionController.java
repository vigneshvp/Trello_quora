package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
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
        final QuestionResponse response =
                new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
