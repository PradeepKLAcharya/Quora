package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
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
    public UsersEntity deleteUser(String userUuid, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.");
        }

        if(Objects.equals("nonadmin", userAuthTokenEntity.getUser().getRole()))
        {
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        UsersEntity userEntity =  userDao.getUser(userUuid);
        if(userEntity == null){
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }

        userEntity =  userDao.deleteUser(userUuid);
        return userEntity;

    }
}
