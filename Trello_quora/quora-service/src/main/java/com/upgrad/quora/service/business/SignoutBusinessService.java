package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuth;

import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;



@Service
public class SignoutBusinessService {

    @Autowired
    private UserDao userDao;

    /**
     * @param  accessToken the first {@code String} to signout a user.
     * @return List of QuestionEntity objects.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuth signOutService(String accessToken) throws SignOutRestrictedException {
        UserAuth userAuthTokenEntity = null;
        //check user sign in or not
        userAuthTokenEntity = userDao.FindByAuthToken(accessToken);
        if (userAuthTokenEntity != null) {
            final ZonedDateTime now = ZonedDateTime.now();
            userAuthTokenEntity.setLogoutAt(now);
            userAuthTokenEntity = userDao.updateUserEntity(userAuthTokenEntity);
        } else {

            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        return userAuthTokenEntity;
    }
}