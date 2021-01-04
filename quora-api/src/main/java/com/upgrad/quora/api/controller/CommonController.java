package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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

@RestController
@RequestMapping("/")
public class CommonController {
    
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    
    private final UserBusinessService userBusinessService;
    
    @Autowired
    public CommonController(final UserBusinessService userBusinessService) {
        this.userBusinessService = userBusinessService;
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(
        @PathVariable("userId") final String userUuid,
        @RequestHeader("authorization") final String authorization)
        throws UserNotFoundException, AuthorizationFailedException {
        log.debug("[CommonController] getUser");
        final UsersEntity userEntity = userBusinessService.getUser(userUuid, authorization);
        log.debug("[CommonController] Logged-In User - {}", userEntity.getUsername());
        UserDetailsResponse userDetailsResponse =
            new UserDetailsResponse().firstName(userEntity.getFirstname())
                .lastName(userEntity.getLastname()).emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactnumber())
                .aboutMe(userEntity.getAboutme()).dob(userEntity.getDob())
                .userName(userEntity.getUsername()).country(userEntity.getCountry());
        
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
    
}
