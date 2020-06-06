package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public Answer createAnswer(Answer answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public Answer getAnswerByUuid(String questionId) {
        try {
            return entityManager.createNamedQuery("answerEntityByUuid", Answer.class).setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre) {

            return null;
        }
    }

    public Answer editAnswerContent(final Answer answerEntity) {
        return entityManager.merge(answerEntity);
    }

    public void userAnswerDelete(final String answerId) {
        Answer answerEntity = getAnswerByUuid(answerId);
        entityManager.remove(answerEntity);
    }

    public List < Answer > getAllAnswersToQuestion(final String questionId) {
        try {
            return entityManager.createNamedQuery("answersByQuestionId", Answer.class).setParameter("uuid", questionId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
