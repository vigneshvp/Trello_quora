package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity createUser(final UsersEntity userEntity) {
        
        String password = userEntity.getPassword();
        if (password == null) {
            userEntity.setPassword("proman@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
        
    }

}
