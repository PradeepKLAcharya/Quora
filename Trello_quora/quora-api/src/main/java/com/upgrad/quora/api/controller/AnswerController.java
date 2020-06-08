package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

	@Autowired
	CreateAnswerBusinessService createAnswerBusinessService;

	@Autowired
	EditAnswerBusinessService editAnswerBusinessService;

	@Autowired
	AnswerBusinessService answerBusinessService;

	@Autowired
	DeleteAnswerBusinessService deleteAnswerBusinessService;

	@Autowired
	QuestionDao questionDao;


	@PostMapping(path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
			@PathVariable("questionId") final String questionId,
			@RequestHeader("authorization") final String authorization)
			throws AuthorizationFailedException, InvalidQuestionException {
		// Logic to handle Bearer <accesstoken>
		// User can give only Access token or Bearer <accesstoken> as input.
		String bearerToken = null;
		try {
			bearerToken = authorization.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = authorization;
		}

		// Create answer entity
		final AnswerEntity answerEntity = new AnswerEntity();
		answerEntity.setAns(answerRequest.getAnswer());

		// Return response with created answer entity
		final AnswerEntity createdAnswerEntity = createAnswerBusinessService.createAnswer(answerEntity, questionId,
				bearerToken);
		AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
		return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest,
			@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization)
			throws AuthorizationFailedException, AnswerNotFoundException {
		// Logic to handle Bearer <accesstoken>
		// User can give only Access token or Bearer <accesstoken> as input.
		String bearerToken = null;
		try {
			bearerToken = authorization.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = authorization;
		}

		// Created answer entity for further update
		AnswerEntity answerEntity = new AnswerEntity();
		answerEntity.setAns(answerEditRequest.getContent());
		answerEntity.setUuid(answerId);

		// Return response with updated answer entity
		AnswerEntity updatedAnswerEntity = editAnswerBusinessService.editAnswerContent(answerEntity, bearerToken);
		AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid())
				.status("ANSWER EDITED");
		return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
	}

	/**
	 * @param authorization
	 * @param questionId
	 * @return ResponseEntity<List<AnswerDetailsResponse>>
	 * @throws AuthorizationFailedException
	 * @throws InvalidQuestionException
	 */
	@RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<AnswerDetailsResponse>> getQuestionsByUser(
			@RequestHeader("authorization") final String authorization,
			@PathVariable("questionId") final String questionId)
			throws AuthorizationFailedException, InvalidQuestionException {


		String bearerToken = null;
		try {
			bearerToken = authorization.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = authorization;
		}

		List<AnswerEntity> listOfAnswers = answerBusinessService.getAllAnswersToQuestion(bearerToken, questionId);
		List<AnswerDetailsResponse> answerDetailsResponse = getCustomizedAnswerContent(listOfAnswers);

		return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponse, HttpStatus.OK);
	}

	// Method which customize the response and provide only the required
	// information.
	private List<AnswerDetailsResponse> getCustomizedAnswerContent(List<AnswerEntity> listOfAnswers) {
		List<AnswerDetailsResponse> answerDetailsResponse = new ArrayList<AnswerDetailsResponse>();
		for (AnswerEntity each : listOfAnswers) {
			answerDetailsResponse.add(new AnswerDetailsResponse().id(each.getUuid()).answerContent(each.getAns())
					.questionContent(each.getQuestion().getContent()));
		}
		return answerDetailsResponse;
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String ansUuid) throws AuthorizationFailedException, AnswerNotFoundException {

		String bearerToken = null;
		try {
			bearerToken = authorization.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = authorization;
		}

		// delete requested user
		deleteAnswerBusinessService.deleteAnswer(ansUuid, bearerToken);
		AnswerDeleteResponse answerRsp = new AnswerDeleteResponse().id(ansUuid).status("ANSWER DELETED");
		return new ResponseEntity<AnswerDeleteResponse>(answerRsp, HttpStatus.OK);
	}
}