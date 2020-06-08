package com.upgrad.quora.service.business;

import java.util.List;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	AnswerDao answerDao;

	@Autowired
	UserDao userDao;

	@Autowired
	QuestionDao questionDao;

	/**
	 * @param authorization
	 * @param questionId
	 * @return List<Answer>
	 * @throws AuthorizationFailedException
	 * @throws UserNotFoundException
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AnswerEntity> getAllAnswersToQuestion(String authorization, String questionId)
			throws AuthorizationFailedException, InvalidQuestionException {
		UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

		if (userAuthTokenEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		}

		if (userAuthTokenEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
		}

		if (questionDao.getQuestionByQUuid(questionId)== null) {
			throw new InvalidQuestionException("QUES-001",
					"The question with entered uuid whose details are to be seen does not exist");
		}
		return answerDao.getAllAnswersToQuestion(questionId);
	}
}