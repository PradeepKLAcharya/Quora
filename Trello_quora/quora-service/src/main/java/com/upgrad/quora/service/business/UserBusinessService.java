package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.entity.Users;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;

    public Users getUser(String userUuid, String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }
        Users userEntity =  userDao.getUser(userUuid);
        if(userEntity == null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }
        return userEntity;
    }
}
