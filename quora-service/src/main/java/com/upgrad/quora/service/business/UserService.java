package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity createUser(final UsersEntity userEntity) throws SignUpRestrictedException  {
        
        String password = userEntity.getPassword();
        UsersEntity existingUserEntity;
        existingUserEntity = userDao.getUserByUserName(userEntity.getUsername());
        if(existingUserEntity != null) {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
        existingUserEntity = userDao.getUserByEmail(userEntity.getEmail());
        if(existingUserEntity != null) {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }
        if (password == null) {
            userEntity.setPassword("quora");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
        
    }

}
