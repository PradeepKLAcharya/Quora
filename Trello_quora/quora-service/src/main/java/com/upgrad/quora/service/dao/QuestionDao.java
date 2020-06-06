package com.upgrad.quora.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.upgrad.quora.service.entity.Question;

/**
 * @author Avinash
 *
 */
@Repository
public class QuestionDao {

	@Autowired
	private EntityManager entityManager;

	/**
	 * @param question
	 * @return Question
	 */
	public Question createQuestion(final Question question) {

		entityManager.persist(question);
		return question;
	}

	/**
	 * @return List<Question>
	 */
	public List<Question> getQuestions() {
		try {
			return entityManager.createNamedQuery("allQuestions", Question.class).getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * @param userid
	 * @return List<Question>
	 */
	public List<Question> getQuestionsByUser(Integer userid) {
		try {
			return entityManager.createNamedQuery("questionsByUser", Question.class).setParameter("qid", userid)
					.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Get the question for the given id.
	 *
	 * @param questionId id of the required question.
	 * @return Question if question with given id is found else null.
	 */
	public Question getQuestionById(final String questionId) {
		try {
			return entityManager.createNamedQuery("getQuestionById", Question.class).setParameter("uuid", questionId).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Update the question
	 *
	 * @param questionEntity question entity to be updated.
	 */
	public void updateQuestion(Question questionEntity) {
		entityManager.merge(questionEntity);
	}

	/**
	 * Delete the question
	 *
	 * @param Question question entity to be deleted.
	 */
	public Question deleteQuestion(Question questionEntity) {

		entityManager.remove(questionEntity);
		return questionEntity;
	}

}
