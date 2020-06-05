package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.entity.Users;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AdminBusinessService {
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(String userUuid, String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserAuth userAuthTokenEntity = userDao.FindByAuthToken(authorization);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.");
        }

        String role = userAuthTokenEntity.getUser().getRole();
        if(role.equals("nonadmin"))
        {
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        Users userEntity =  userDao.getUser(userUuid);
        if(userEntity == null){
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }else{
            userDao.deleteUser(userUuid);
            return userUuid;
        }

    }
}
