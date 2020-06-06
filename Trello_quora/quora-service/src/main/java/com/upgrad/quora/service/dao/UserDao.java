package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class UserDao {
    @Autowired
    private EntityManager entityManager;

    public UsersEntity createUsers(UsersEntity userEntity){
        entityManager.persist((userEntity));
        return userEntity;
    }

    public UsersEntity getUserByEmail(final String email){
        try {
            return entityManager.createNamedQuery("userByEmail", UsersEntity.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UsersEntity getUserByUserName(final String userName){
        try {
            return entityManager.createNamedQuery("userByUserName", UsersEntity.class).setParameter("username", userName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public UserAuthEntity getUserAuthToken(String token) {
        try {
            return entityManager.createNamedQuery("userByAuthToken", UserAuthEntity.class).setParameter("accessToken", token)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    public void updateUserEntity(UserAuthEntity userEntity) {
        entityManager.merge(userEntity);
    }

    public UsersEntity getUser(String Uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UsersEntity.class).setParameter("uuid", Uuid).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }

    }

    //add transactional?
    public UsersEntity deleteUser(String userUuid) {
        UsersEntity user;
        try {
            user = entityManager.createNamedQuery("userByUuid", UsersEntity.class).setParameter("uuid", userUuid).getSingleResult();
        }catch (NoResultException nre)
        {
            return null;
        }
        entityManager.remove(user);;
        return user;

    }

}
