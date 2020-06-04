package com.upgrad.quora.api.controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

/**
 * @author Avinash
 *
 */

@RestController
@RequestMapping("/")
public class QuestionController {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private UserDao userDao;

	/**
	 * @param questionRequest
	 * @param authorization
	 * @return ResponseEntity<QuestionResponse>
	 * @throws AuthorizationFailedException
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
			@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

		UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorization);
		validateAuthToken(userAuthTokenEntity);

		Question question = new Question();
		question.setContent(questionRequest.getContent());
		question.setDate(ZonedDateTime.now());
		question.setUuid(userAuthTokenEntity.getUuid());
		question.setUser(userAuthTokenEntity.getUser());

		final Question createdQuestion = questionService.createQuestion(question, userAuthTokenEntity);
		QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid())
				.status("QUESTION CREATED");
		return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
	}

	/**
	 * @param authorization
	 * @return ResponseEntity<List<QuestionDetailsResponse>>
	 * @throws AuthorizationFailedException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/question", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
			@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
		UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorization);
		validateAuthToken(userAuthTokenEntity);
		
		List<Question> listOfQuestions = questionService.getAllQuestions(userAuthTokenEntity);

		List<QuestionDetailsResponse> questionDetailsResponse = getCustomizedQuestionResponse(listOfQuestions);

		return new ResponseEntity<>(questionDetailsResponse, HttpStatus.OK);
	}

	private List<QuestionDetailsResponse> getCustomizedQuestionResponse(List<Question> listOfQuestions) {
		List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<QuestionDetailsResponse>();
		for (Question each : listOfQuestions) {
			questionDetailsResponse.add(new QuestionDetailsResponse().id(each.getUuid()).content(each.getContent()));
		}
		return questionDetailsResponse;
	}

	private void validateAuthToken(UserAuth userAuthTokenEntity) throws AuthorizationFailedException {
		if (userAuthTokenEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		}

		if (userAuthTokenEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
		}
	}
}
