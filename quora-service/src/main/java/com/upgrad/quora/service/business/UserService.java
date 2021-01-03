package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthEntityDao;
import com.upgrad.quora.service.dao.UserDao;
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
public class UserService {
    
    private final UserDao userDao;
    
    private final UserAuthEntityDao userAuthEntityDao;
    
    private final PasswordCryptographyProvider cryptographyProvider;
    
    @Autowired
    public UserService(final UserDao userDao, final UserAuthEntityDao userAuthEntityDao,
        final PasswordCryptographyProvider cryptographyProvider) {
        this.userDao = userDao;
        this.userAuthEntityDao = userAuthEntityDao;
        this.cryptographyProvider = cryptographyProvider;
        
    }
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity createUser(final UsersEntity UsersEntity) throws SignUpRestrictedException {
        
        String password = UsersEntity.getPassword();
        UsersEntity existingUsersEntity;
        existingUsersEntity = userDao.getUserByUserName(UsersEntity.getUsername());
        if (existingUsersEntity != null) {
            throw new SignUpRestrictedException("SGR-001",
                "Try any other Username, this Username has already been taken");
        }
        existingUsersEntity = userDao.getUserByEmail(UsersEntity.getEmail());
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
        return userDao.createUser(UsersEntity);
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password)
        throws AuthenticationFailedException {
        UsersEntity userEntity = userDao.getUserByUserName(username);
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
            userDao.createAuthToken(userAuthToken);
            userDao.updateUser(userEntity);
            return userAuthToken;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity logout(final String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        userAuthEntity.setLogoutAt(now);
        userDao.updateUserAuthEntity(userAuthEntity);
        return userAuthEntity;
    }
    
    
}

