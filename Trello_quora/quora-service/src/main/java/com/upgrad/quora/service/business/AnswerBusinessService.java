package com.upgrad.quora.service.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

/**
 * @author ashrimali
 *
 */
@Service
public class AnswerBusinessService {

	@Autowired
	private AnswerDao answerDao;

	@Autowired
	private UserDao userDao;

	/**
	 * @param authorization
	 * @param questionId
	 * @return List<Answer>
	 * @throws AuthorizationFailedException
	 * @throws UserNotFoundException
	 */
	public List<AnswerEntity> getAllAnswersToQuestion(String authorization, String questionId)
			throws AuthorizationFailedException, UserNotFoundException {
		UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
		if (userAuthTokenEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		}

		if (userAuthTokenEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
		}

		List<AnswerEntity> listOfAnswers = answerDao.getAllAnswersToQuestion(questionId);
		if (listOfAnswers == null) {
			throw new UserNotFoundException("USR-001",
					"User with entered uuid whose question details are to be seen does not exist");
		}
		return listOfAnswers;
	}
}