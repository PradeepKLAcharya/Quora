package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class UserDao {
    @Autowired
    private EntityManager entityManager;

    public Users createUsers(Users userEntity){
        entityManager.persist((userEntity));
        return userEntity;
    }

    public Users getUserByEmail(final String email){
        try {
            return entityManager.createNamedQuery("userByEmail", Users.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Users getUserByUserName(final String userName){
        try {
            return entityManager.createNamedQuery("userByUserName", Users.class).setParameter("username", userName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuth createAuthToken(final UserAuth userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public UserAuth FindByAuthToken(String token) {
        try {
            return entityManager.createNamedQuery("userByAuthToken", UserAuth.class).setParameter("accessToken", token)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateUserEntity(UserAuth userEntity) {
        entityManager.merge(userEntity);
    }

    public Users getUser(String Uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", Users.class).setParameter("uuid", Uuid).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }

    //add transactional?
    public Users deleteUser(String userUuid) {
        Users user;
        try {
            user = entityManager.createNamedQuery("userByUuid", Users.class).setParameter("uuid", userUuid).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }
        entityManager.remove(user);;
        return user;

    }

}
