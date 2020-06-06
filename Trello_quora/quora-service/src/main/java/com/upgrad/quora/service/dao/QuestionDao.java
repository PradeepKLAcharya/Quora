package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public Question createQuestion(Question questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public Question getQuestionByQUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionByQUuid", Question.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List < Question > getAllQuestionsByUser(final String uuid) {
        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", Question.class).setParameter("uuid", uuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List < Question > getAllQuestions() {
        try {
            return entityManager.createNamedQuery("allQuestions", Question.class).getResultList();
        } catch (NoResultException nre) {

            return null;
        }
    }
    public Question updateQuestion(final Question questionEntity) {
        return entityManager.merge(questionEntity);
    }

    public void deleteQuestion(final String uuid) {
        Question questionEntity = getQuestionByQUuid(uuid);
        entityManager.remove(questionEntity);
    }

    public Question getQuestionById(String questionId) {
        try {
            return entityManager.createNamedQuery("questionById", Question.class).setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
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
	 * Update the question
	 *
	 * @param questionEntity question entity to be updated.
	 */
	public void updateQuestion(Question questionEntity) {
		entityManager.merge(questionEntity);
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
	 * Delete the question
	 *
	 * @param Question question entity to be deleted.
	 */
	public Question deleteQuestion(Question questionEntity) {

		entityManager.remove(questionEntity);
		return questionEntity;
	}
}
