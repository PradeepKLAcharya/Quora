package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

	@Autowired
	private SignupBusinessService signupBusinessService;

	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest)
			throws SignUpRestrictedException {
		final UsersEntity userEntity = new UsersEntity();
		userEntity.setUuid(UUID.randomUUID().toString());
		userEntity.setAboutme(signupUserRequest.getAboutMe());
		userEntity.setCountry(signupUserRequest.getCountry());
		userEntity.setDob(signupUserRequest.getDob());
		userEntity.setFirstName(signupUserRequest.getFirstName());
		userEntity.setLastName(signupUserRequest.getLastName());
		userEntity.setEmail(signupUserRequest.getEmailAddress());
		userEntity.setPassword(signupUserRequest.getPassword());
		userEntity.setContactnumber(signupUserRequest.getContactNumber());
		userEntity.setUsername(signupUserRequest.getUserName());
		userEntity.setRole("nonadmin"); // todo: Move to common mapping class, non admin or admin should be in business
										// logic?
		final UsersEntity createdUserEntity = signupBusinessService.signup(userEntity);
		SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid())
				.status("USER SUCCESSFULLY REGISTERED");
		return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED); // todo: handle exception

	}

	@RequestMapping(method = RequestMethod.POST, path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization)
			throws AuthenticationFailedException {
		// Basic sfdfafadfa form
		byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
		String decodedText = new String(decode);
		String[] decodedArray = decodedText.split(":");

		UserAuthEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
		UsersEntity user = userAuthToken.getUser();

		SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid())
				.message("SIGNED IN SUCCESSFULLY");

		HttpHeaders headers = new HttpHeaders();
		headers.add("access_token", userAuthToken.getAccessToken());
		return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SignoutResponse> logout(@RequestHeader("authorization") final String authorization)
			throws SignOutRestrictedException {

		UserAuthEntity userAuthToken = authenticationService.authenticateBearer(authorization); // todo: see if user has
																							// already signed out, not a
																							// project requirement as of
																							// now
		UsersEntity user = userAuthToken.getUser();

		SignoutResponse authorizedUserResponse = new SignoutResponse().id(user.getUuid())
				.message("SIGNED OUT SUCCESSFULLY");

		return new ResponseEntity<SignoutResponse>(authorizedUserResponse, HttpStatus.OK);
	}
}
