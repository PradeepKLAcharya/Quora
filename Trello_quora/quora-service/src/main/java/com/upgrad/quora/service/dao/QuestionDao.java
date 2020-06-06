package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public QuestionEntity getQuestionByQUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List < QuestionEntity > getAllQuestionsByUser(final String uuid) {
        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).setParameter("uuid", uuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List < QuestionEntity > getAllQuestions() {
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {

            return null;
        }
    }


    public void deleteQuestion(final String uuid) {
        QuestionEntity questionEntity = getQuestionByQUuid(uuid);
        entityManager.remove(questionEntity);
    }



		/**
	 * @return List<Question>
	 */
	public List<QuestionEntity> getQuestions() {
		try {
			return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

		/**
	 * @param userid
	 * @return List<Question>
	 */
	public List<QuestionEntity> getQuestionsByUser(Integer userid) {
		try {
			return entityManager.createNamedQuery("questionsByUser", QuestionEntity.class).setParameter("qid", userid)
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
	public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
		return entityManager.merge(questionEntity);
	}
		/**
	 * Get the question for the given id.
	 *
	 * @param questionId id of the required question.
	 * @return Question if question with given id is found else null.
	 */
	public QuestionEntity getQuestionById(final String questionId) {
		try {
			return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	/**
	 * Delete the question
	 *
	 * @param QuestionEntity question entity to be deleted.
	 */
	public QuestionEntity deleteQuestion(QuestionEntity questionEntity) {

		entityManager.remove(questionEntity);
		return questionEntity;
	}

}

