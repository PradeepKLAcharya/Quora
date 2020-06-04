package com.upgrad.quora.service.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

/**
 * @author Avinash
 *
 */

@Service
public class QuestionService {

	@Autowired
	private QuestionDao questionDao;

	/**
	 * @param question
	 * @param userAuthTokenEntity
	 * @return Question
	 * @throws AuthorizationFailedException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Question createQuestion(Question question, UserAuth userAuthTokenEntity){
		return questionDao.createQuestion(question);
	}

	/**
	 * @param userAuthTokenEntity
	 * @return List<Question>
	 * @throws AuthorizationFailedException
	 */
	public List<Question> getAllQuestions(UserAuth userAuthTokenEntity){
		return questionDao.getQuestions();
	}

}
