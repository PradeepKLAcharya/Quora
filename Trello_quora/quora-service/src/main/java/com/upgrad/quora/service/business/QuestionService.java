package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuth;
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
	public Question createQuestion(Question question, UserAuth userAuthTokenEntity) {
		return questionDao.createQuestion(question);
	}

	/**
	 * @param userAuthTokenEntity
	 * @return List<Question>
	 * @throws AuthorizationFailedException
	 */
	public List<Question> getAllQuestions(UserAuth userAuthTokenEntity) {
		return questionDao.getQuestions();
	}

	/**
	 * @param userAuthTokenEntity
	 * @param userid
	 * @return List<Question>
	 * @throws UserNotFoundException
	 */
	public List<Question> getAllQuestionsByUser(UserAuth userAuthTokenEntity, int userid)
			throws UserNotFoundException {
		List<Question> listOfQuestions = questionDao.getQuestionsByUser(userid);
		if (listOfQuestions == null) {
			throw new UserNotFoundException("USR-001",
					"User with entered uuid whose question details are to be seen does not exist");
		}
		return listOfQuestions;
	}

	/**
	 * * Edit the question
	 *
	 * @param accessToken accessToken of the user for valid authentication.
	 * @param questionId  id of the question to be edited.
	 * @param content     new content for the existing question.
	 * @return Question
	 * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if the user has already signed out.
	 * @throws InvalidQuestionException     if the question with id doesn't exist.
	 */
	@Transactional
	public Question editQuestion(final String accessToken, final String questionId, final String content) throws AuthorizationFailedException, InvalidQuestionException {
		UserAuth userAuthEntity = userDao.getUserAuthToken(accessToken);
		if (userAuthEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		} else if (userAuthEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
		}
		Question questionEntity = questionDao.getQuestionById(questionId);
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
	public Question deleteQuestion(String questUuid, String token) throws AuthorizationFailedException, InvalidQuestionException {

		UserAuth userAuthEntity = userDao.getUserAuthToken(token);
		if (userAuthEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		}

		if (userAuthEntity.getLogoutAt() != null && userAuthEntity.getLogoutAt().isAfter(userAuthEntity.getLoginAt())) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete the question");
		}

		Question questionEntity = questionDao.getQuestionById(questUuid);

		if (questionEntity == null) {
			throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
		}

		if (!questionEntity.getUser().getUuid().equals(userAuthEntity.getUser().getUuid())) {
			throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
		}
		return questionDao.deleteQuestion(questionEntity);

	}

}

