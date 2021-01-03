package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UsersEntity;
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
@RequestMapping("/")
public class CommonController {
    
    private final UserService userService;
    
    @Autowired
    public CommonController(final UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(
        @PathVariable("userId") final String userUuid,
        @RequestHeader("authorization") final String authorization)
        throws UserNotFoundException, AuthorizationFailedException {
        final UsersEntity userEntity = userService.getUser(userUuid, authorization);
        UserDetailsResponse userDetailsResponse =
            new UserDetailsResponse().firstName(userEntity.getFirstname())
                .lastName(userEntity.getLastname()).emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactnumber())
                .aboutMe(userEntity.getAboutme()).dob(userEntity.getDob())
                .userName(userEntity.getUsername()).country(userEntity.getCountry());
        
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
    
}
