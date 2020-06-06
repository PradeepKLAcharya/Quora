package com.upgrad.quora.api.controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
/**
 * @author Avinash
 *
 */

@RestController
@RequestMapping("/question")
public class QuestionController {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserBusinessService userBusinessService;

	/**
	 * @param questionRequest
	 * @param authorization
	 * @return ResponseEntity<QuestionResponse>
	 * @throws AuthorizationFailedException
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
	@RequestMapping(method = RequestMethod.GET, path = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
			@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
		UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorization);
		validateAuthToken(userAuthTokenEntity);

		List<Question> listOfQuestions = questionService.getAllQuestions(userAuthTokenEntity);

		List<QuestionDetailsResponse> questionDetailsResponse = getCustomizedQuestionResponse(listOfQuestions);

		return new ResponseEntity<>(questionDetailsResponse, HttpStatus.OK);
	}

	/**
	 * @param authorization
	 * @param userUuid
	 * @return ResponseEntity<List<QuestionDetailsResponse>>
	 * @throws AuthorizationFailedException
	 * @throws UserNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<QuestionDetailsResponse>> getQuestionsByUser(
			@RequestHeader("authorization") final String authorization, @PathVariable("userId") final String userUuid)
			throws AuthorizationFailedException, UserNotFoundException {

		UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorization);
		validateAuthToken(userAuthTokenEntity);
		List<Question> listOfQuestions = questionService.getAllQuestionsByUser(userAuthTokenEntity,
				userBusinessService.getUser(userUuid, authorization).getId());
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

	/**
	 * Edit a question
	 *
	 * @param accessToken         access token to authenticate user.
	 * @param questionId          id of the question to be edited.
	 * @param questionEditRequest new content for the question.
	 * @return Id and status of the question edited.
	 * @throws AuthorizationFailedException In case the access token is invalid.
	 * @throws InvalidQuestionException     if question with questionId doesn't exist.
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<QuestionResponse> editQuestion(@RequestHeader("authorization") final String accessToken, @PathVariable("questionId") final String questionId, QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {
		Question questionEntity = questionService.editQuestion(accessToken, questionId, questionEditRequest.getContent());
		QuestionResponse questionResponse = new QuestionResponse();
		questionResponse.setId(questionEntity.getUuid());
		questionResponse.setStatus("QUESTION EDITED");
		return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
	}

	/**
	 * Delete a question
	 *
	 * @param accessToken access token to authenticate user.
	 * @param questionId id of the question to be edited.
	 * @return Id and status of the question deleted.
	 * @throws AuthorizationFailedException In case the access token is invalid.
	 * @throws InvalidQuestionException if question with questionId doesn't exist.
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
	public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
			@RequestHeader("authorization") final String accessToken,
			@PathVariable("questionId") final String questionId)
			throws AuthorizationFailedException, InvalidQuestionException {

		Question questionEntity = questionService.deleteQuestion(accessToken, questionId);
		QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
		questionDeleteResponse.setId(questionEntity.getUuid());
		questionDeleteResponse.setStatus("QUESTION DELETED");
		return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
	}
}
