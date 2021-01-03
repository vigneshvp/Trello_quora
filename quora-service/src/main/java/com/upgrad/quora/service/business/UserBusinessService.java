package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthEntityDao;
import com.upgrad.quora.service.dao.UsersEntityDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {
    
    private final UsersEntityDao usersEntityDao;
    
    private final UserAuthEntityDao userAuthEntityDao;
    
    private final PasswordCryptographyProvider cryptographyProvider;
    
    @Autowired
    public UserBusinessService(final UsersEntityDao usersEntityDao, final UserAuthEntityDao userAuthEntityDao,
        final PasswordCryptographyProvider cryptographyProvider) {
        this.usersEntityDao = usersEntityDao;
        this.userAuthEntityDao = userAuthEntityDao;
        this.cryptographyProvider = cryptographyProvider;
        
    }
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity createUser(final UsersEntity UsersEntity) throws SignUpRestrictedException {
        
        String password = UsersEntity.getPassword();
        UsersEntity existingUsersEntity;
        existingUsersEntity = usersEntityDao.getUserByUserName(UsersEntity.getUsername());
        if (existingUsersEntity != null) {
            throw new SignUpRestrictedException("SGR-001",
                "Try any other Username, this Username has already been taken");
        }
        existingUsersEntity = usersEntityDao.getUserByEmail(UsersEntity.getEmail());
        if (existingUsersEntity != null) {
            throw new SignUpRestrictedException("SGR-002",
                "This user has already been registered, try with any other emailId");
        }
        if (password == null) {
            UsersEntity.setPassword("quora");
        }
        String[] encryptedText = cryptographyProvider.encrypt(UsersEntity.getPassword());
        UsersEntity.setSalt(encryptedText[0]);
        UsersEntity.setPassword(encryptedText[1]);
        return usersEntityDao.createUser(UsersEntity);
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password)
        throws AuthenticationFailedException {
        UsersEntity userEntity = usersEntityDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        
        final String encryptedPassword = PasswordCryptographyProvider
                                             .encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthToken = new UserAuthEntity();
            userAuthToken.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(
                jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setUuid(userEntity.getUuid());
            usersEntityDao.createAuthToken(userAuthToken);
            usersEntityDao.updateUser(userEntity);
            return userAuthToken;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity logout(final String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = usersEntityDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        userAuthEntity.setLogoutAt(now);
        
        usersEntityDao.updateUserAuthEntity(userAuthEntity);
        return userAuthEntity;
    }
    
    public UsersEntity getUser(final String userUuid, final String authorizationToken)
        throws UserNotFoundException,
                   AuthorizationFailedException {
        String token = authorizationToken;
        /*if (authorizationToken.startsWith("Bearer")) {
            token = authorizationToken.split("Bearer ")[1];
        } else if (authorizationToken.startsWith("Basic")) {
            token = authorizationToken.split("Basic ")[1];
        }*/
        UserAuthEntity userAuthEntity = userAuthEntityDao.getUserAuth(token);
        if (null == userAuthEntity) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        UsersEntity userEntity = usersEntityDao.getUserByUuid(userUuid);
        if (null == userEntity) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        // The user has already logged out or the token has expired
        if (userAuthEntity.getLogoutAt() != null || userAuthEntity.getExpiresAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002",
                "User is signed out");
        }
        
        return userEntity;
    }
}

