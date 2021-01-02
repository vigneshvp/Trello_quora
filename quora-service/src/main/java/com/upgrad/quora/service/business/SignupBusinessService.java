package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {
    
    @Autowired
    private UserAdminBusinessService userAdminBusinessService;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity signup(UsersEntity userEntity) throws SignUpRestrictedException {
        return userAdminBusinessService.createUser(userEntity);
    }
}
