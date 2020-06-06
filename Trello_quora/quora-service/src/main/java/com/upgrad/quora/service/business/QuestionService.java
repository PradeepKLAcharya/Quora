package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Avinash
 *
 */

@Service
public class QuestionService {

	@Autowired
	private QuestionDao questionDao;

	@Autowired
	private UserDao userDao;

	/**
	 * @param question
	 * @param userAuthTokenEntity
	 * @return Question
	 * @throws AuthorizationFailedException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public QuestionEntity createQuestion(QuestionEntity question, UserAuthEntity userAuthTokenEntity) {

		return questionDao.createQuestion(question);
	}

	/**
	 * @param userAuthTokenEntity
	 * @return List<Question>
	 * @throws AuthorizationFailedException
	 */
	public List<QuestionEntity> getAllQuestions(String authorization) throws AuthorizationFailedException {
		UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
		validateAuthFailure(userAuthTokenEntity);
		return questionDao.getQuestions();
	}

	/**
	 * @param userAuthTokenEntity
	 * @param userid
	 * @return List<Question>
	 * @throws UserNotFoundException
	 * @throws AuthorizationFailedException
	 */
	public List<QuestionEntity> getAllQuestionsByUser(String authorization, int userid)
			throws UserNotFoundException, AuthorizationFailedException {
		UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
		validateAuthFailure(userAuthTokenEntity);
		List<QuestionEntity> listOfQuestions = questionDao.getQuestionsByUser(userid);
		if (listOfQuestions == null) {
			throw new UserNotFoundException("USR-001",
					"User with entered uuid whose question details are to be seen does not exist");
		}
		return listOfQuestions;
	}

	private void validateAuthFailure(UserAuthEntity userAuthTokenEntity) throws AuthorizationFailedException {
		if (userAuthTokenEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		}

		if (userAuthTokenEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
		}
	}

	/**
	 * * Edit the question
	 *
	 * @param accessToken accessToken of the user for valid authentication.
	 * @param questionId  id of the question to be edited.
	 * @param content     new content for the existing question.
	 * @return Question
	 * @throws AuthorizationFailedException ATHR-001 - if user token is not present
	 *                                      in DB. ATHR-002 if the user has already
	 *                                      signed out.
	 * @throws InvalidQuestionException     if the question with id doesn't exist.
	 */
	@Transactional
	public QuestionEntity editQuestion(final String accessToken, final String questionId, final String content)
			throws AuthorizationFailedException, InvalidQuestionException {
		UserAuthEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
		validateAuthFailure(userAuthEntity);
		QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
		if (questionEntity == null) {
			throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
		}
		if (!questionEntity.getUser().getUuid().equals(userAuthEntity.getUser().getUuid())) {
			throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
		}
		questionEntity.setContent(content);
		questionDao.updateQuestion(questionEntity);
		return questionEntity;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public QuestionEntity deleteQuestion(String questUuid, String token)
			throws AuthorizationFailedException, InvalidQuestionException {

		UserAuthEntity userAuthEntity = userDao.getUserAuthToken(token);
		if (userAuthEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		}

		if (userAuthEntity.getLogoutAt() != null && userAuthEntity.getLogoutAt().isAfter(userAuthEntity.getLoginAt())) {
			throw new AuthorizationFailedException("ATHR-002",
					"User is signed out.Sign in first to delete the question");
		}

		QuestionEntity questionEntity = questionDao.getQuestionById(questUuid);

		if (questionEntity == null) {
			throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
		}

		if (!questionEntity.getUser().getUuid().equals(userAuthEntity.getUser().getUuid())) {
			throw new AuthorizationFailedException("ATHR-003",
					"Only the question owner or admin can delete the question");
		}
		return questionDao.deleteQuestion(questionEntity);

	}

}
