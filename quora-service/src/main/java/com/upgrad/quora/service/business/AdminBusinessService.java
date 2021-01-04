package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthEntityDao;
import com.upgrad.quora.service.dao.UsersEntityDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBusinessService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminBusinessService.class);
    
    
    private final UsersEntityDao usersEntityDao;
    
    private final UserAuthEntityDao userAuthEntityDao;
    
    @Autowired
    public AdminBusinessService(final UsersEntityDao usersEntityDao,
        final UserAuthEntityDao userAuthEntityDao) {
        this.usersEntityDao = usersEntityDao;
        this.userAuthEntityDao = userAuthEntityDao;
    }
    
    public void deleteUser(final String userUuid, final String authorizationToken)
        throws UserNotFoundException,
                   AuthorizationFailedException {
        log.debug("[AdminrBusinessService] Delete User");
        String token = authorizationToken;
        /*
        if (authorizationToken.startsWith("Bearer")) {
            token = authorizationToken.split("Bearer ")[1];
        } else if (authorizationToken.startsWith("Basic")) {
            token = authorizationToken.split("Basic ")[1];
        }*/
        UserAuthEntity userAuthEntity = userAuthEntityDao.getUserAuth(token);
        if (null == userAuthEntity) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        
        if (!StringUtils.equalsIgnoreCase(userAuthEntity.getUser().getRole(), "admin")) {
            throw new AuthorizationFailedException("ATHR-003",
                "Unauthorized Access, Entered user is not an admin");
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
        
        usersEntityDao.deleteUser(userEntity);
    }
    
    
}
