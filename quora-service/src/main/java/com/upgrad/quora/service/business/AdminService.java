package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthEntityDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    
    private final UserDao userDao;
    
    private final UserAuthEntityDao userAuthEntityDao;
    
    @Autowired
    public AdminService(final UserDao userDao,final UserAuthEntityDao userAuthEntityDao) {
     this.userDao = userDao;
     this.userAuthEntityDao = userAuthEntityDao;
    }
    
    public void deleteUser(final String userUuid, final String authorizationToken)
        throws UserNotFoundException,
                   AuthorizationFailedException {
        String token = authorizationToken;
        if (authorizationToken.startsWith("Bearer")) {
            token = authorizationToken.split("Bearer ")[1];
        } else if (authorizationToken.startsWith("Basic")) {
            token = authorizationToken.split("Basic ")[1];
        }
        UserAuthEntity userAuthEntity = userAuthEntityDao.getUserAuth(token);
        if (null == userAuthEntity) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        // The user has already logged out or the token has expired
        if (userAuthEntity.getLogoutAt() != null || userAuthEntity.getExpiresAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002",
                "User is signed out");
        }
        
        UsersEntity userEntity = userDao.getUser(userUuid);
        if (null == userEntity) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        if(!StringUtils.equalsIgnoreCase(userAuthEntity.getUser().getRole(),"admin")) {
            throw new AuthorizationFailedException("ATHR-003",
                "Unauthorized Access, Entered user is not an admin");
        }
        userDao.deleteUser(userEntity);
    }
    

}
