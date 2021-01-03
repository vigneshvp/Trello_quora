package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Api(value = "Signup")
public class UserController {
    
    @Autowired
    private UserBusinessService userBusinessService;
    
    @ApiOperation(value = "Signup a user to the quora trello application", response = ResponseEntity.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "USER SUCCESSFULLY REGISTERED"),
        @ApiResponse(code = 409, message = "CONFLICT")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        
        final UsersEntity userEntity = new UsersEntity();
        
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstname(signupUserRequest.getFirstName());
        userEntity.setLastname(signupUserRequest.getLastName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("1234abc");
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setContactnumber(signupUserRequest.getContactNumber());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutme(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        
        
        final UsersEntity createdUserEntity = userBusinessService.createUser(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }
    
    @ApiOperation(value = "Signin to the quora trello application", response = ResponseEntity.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "USER SUCCESSFULLY REGISTERED"),
        @ApiResponse(code = 404, message = "RESOURCE NOT FOUND")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        
        
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        
        UserAuthEntity userAuthToken = userBusinessService
                                           .authenticate(decodedArray[0], decodedArray[1]);
        UsersEntity user = userAuthToken.getUser();
        
        SigninResponse signinResponse = new SigninResponse().id(user.getUuid())
                                                            .message("SIGNED IN SUCCESSFULL");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }
    
    
    @ApiOperation(value = "Sign-off from the quora trello application", response = ResponseEntity.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "SIGNED OUT SUCCESSFULLY"),
        @ApiResponse(code = 404, message = "RESOURCE NOT FOUND")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> logoff(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        UserAuthEntity userAuthToken = userBusinessService.logout(authorization);
        UsersEntity user = userAuthToken.getUser();
        SignoutResponse signoutResponse = new SignoutResponse().id(user.getUuid())
                                            .message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
