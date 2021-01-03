package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private final AdminBusinessService adminBusinessService;
    
    @Autowired
    public AdminController(final AdminBusinessService adminBusinessService) {
        this.adminBusinessService = adminBusinessService;
    }
    
    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(
        @PathVariable("userId") final String userUuid,
        @RequestHeader("authorization") final String authorization)
        throws UserNotFoundException, AuthorizationFailedException {
        adminBusinessService.deleteUser(userUuid, authorization);
        UserDeleteResponse deleteResponse = new UserDeleteResponse().id(userUuid)
                                                .status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(deleteResponse, HttpStatus.OK);
    }
    
}
