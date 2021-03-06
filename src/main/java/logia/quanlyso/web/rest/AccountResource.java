package logia.quanlyso.web.rest;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import logia.quanlyso.domain.User;
import logia.quanlyso.repository.UserRepository;
import logia.quanlyso.security.SecurityUtils;
import logia.quanlyso.service.MailService;
import logia.quanlyso.service.UserService;
import logia.quanlyso.service.dto.UserDTO;
import logia.quanlyso.web.rest.util.HeaderUtil;
import logia.quanlyso.web.rest.vm.KeyAndPasswordVM;
import logia.quanlyso.web.rest.vm.ManagedUserVM;
import springfox.documentation.annotations.ApiIgnore;

/**
 * REST controller for managing the current user's account.
 *
 * @author Dai Mai
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

	/** The log. */
	private final Logger			log	= LoggerFactory.getLogger(AccountResource.class);

	/** The user repository. */
	private final UserRepository	userRepository;

	/** The user service. */
	private final UserService		userService;

	/** The mail service. */
	private final MailService		mailService;

	/**
	 * Instantiates a new account resource.
	 *
	 * @param userRepository the user repository
	 * @param userService the user service
	 * @param mailService the mail service
	 */
	public AccountResource(UserRepository userRepository, UserService userService,
			MailService mailService) {

		this.userRepository = userRepository;
		this.userService = userService;
		this.mailService = mailService;
	}

	/**
	 * POST /register : register the user.
	 *
	 * @param managedUserVM the managed user View Model
	 * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad
	 *         Request) if the login or email is already in use
	 */
	@PostMapping(path = "/register", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.TEXT_PLAIN_VALUE })
	@Timed
	@ApiIgnore("Quanlyso system don't support register account. Contact administrator to get new one")
	public ResponseEntity registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {

		HttpHeaders textPlainHeaders = new HttpHeaders();
		textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);

		return this.userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
				.map(user -> new ResponseEntity<>("login already in use", textPlainHeaders,
						HttpStatus.BAD_REQUEST))
				.orElseGet(() -> this.userRepository.findOneByEmail(managedUserVM.getEmail())
						.map(user -> new ResponseEntity<>("email address already in use",
								textPlainHeaders, HttpStatus.BAD_REQUEST))
						.orElseGet(() -> {
							User user = this.userService.createUser(managedUserVM.getLogin(),
									managedUserVM.getPassword(), managedUserVM.getFirstName(),
									managedUserVM.getLastName(),
									managedUserVM.getEmail().toLowerCase(),
									managedUserVM.getImageUrl(), managedUserVM.getLangKey());

							this.mailService.sendActivationEmail(user);
							return new ResponseEntity<>(HttpStatus.CREATED);
						}));
	}

	/**
	 * GET /activate : activate the registered user.
	 *
	 * @param key the activation key
	 * @return the ResponseEntity with status 200 (OK) and the activated user in body, or status 500
	 *         (Internal Server Error) if the user couldn't be activated
	 */
	@GetMapping("/activate")
	@Timed
//	@ApiIgnore("Quanlyso system don't support activate account. Contact administrator to do this")
	public ResponseEntity activateAccount(@RequestParam(value = "key") String key) {
		return this.userService.activateRegistration(key)
				.map(user -> new ResponseEntity<>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /authenticate : check if the user is authenticated, and return its login.
	 *
	 * @param request the HTTP request
	 * @return the login if the user is authenticated
	 */
	@GetMapping("/authenticate")
	@Timed
	public String isAuthenticated(HttpServletRequest request) {
		this.log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET /account : get the current user.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500
	 *         (Internal Server Error) if the user couldn't be returned
	 */
	@GetMapping("/account")
	@Timed
	public ResponseEntity<UserDTO> getAccount() {
		return Optional.ofNullable(this.userService.getUserWithAuthorities())
				.map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account : update the current user information.
	 *
	 * @param userDTO the current user information
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) or 500 (Internal
	 *         Server Error) if the user couldn't be updated
	 */
	@PostMapping("/account")
	@Timed
	@ApiIgnore("Quanlyso system don't support update account. Contact administrator to do this")
	public ResponseEntity saveAccount(@Valid @RequestBody UserDTO userDTO) {
		final String userLogin = SecurityUtils.getCurrentUserLogin();
		Optional<User> existingUser = this.userRepository.findOneByEmail(userDTO.getEmail());
		if (existingUser.isPresent()
				&& (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
			return ResponseEntity.badRequest().headers(HeaderUtil
					.createFailureAlert("user-management", "emailexists", "Email already in use"))
					.body(null);
		}
		return this.userRepository.findOneByLogin(userLogin).map(u -> {
			this.userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(),
					userDTO.getEmail(), userDTO.getLangKey(), userDTO.getImageUrl());
			return new ResponseEntity(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account/change_password : changes the current user's password.
	 *
	 * @param password the new password
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) if the new
	 *         password is not strong enough
	 */
	@PostMapping(path = "/account/change_password", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity changePassword(@RequestBody String password) {
		if (!this.checkPasswordLength(password)) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		this.userService.changePassword(password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * POST /account/reset_password/init : Send an email to reset the password of the user.
	 *
	 * @param mail the mail of the user
	 * @return the ResponseEntity with status 200 (OK) if the email was sent, or status 400 (Bad
	 *         Request) if the email address is not registered
	 */
	@PostMapping(path = "/account/reset_password/init", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity requestPasswordReset(@RequestBody String mail) {
		return this.userService.requestPasswordReset(mail).map(user -> {
			this.mailService.sendPasswordResetMail(user);
			return new ResponseEntity<>("email was sent", HttpStatus.OK);
		}).orElse(new ResponseEntity<>("email address not registered", HttpStatus.BAD_REQUEST));
	}

	/**
	 * POST /account/reset_password/finish : Finish to reset the password of the user.
	 *
	 * @param keyAndPassword the generated key and the new password
	 * @return the ResponseEntity with status 200 (OK) if the password has been reset,
	 *         or status 400 (Bad Request) or 500 (Internal Server Error) if the password could not
	 *         be reset
	 */
	@PostMapping(path = "/account/reset_password/finish", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity finishPasswordReset(
			@RequestBody KeyAndPasswordVM keyAndPassword) {
		if (!this.checkPasswordLength(keyAndPassword.getNewPassword())) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		return this.userService
				.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
				.map(user -> new ResponseEntity<>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * Check password length.
	 *
	 * @param password the password
	 * @return true, if successful
	 */
	private boolean checkPasswordLength(String password) {
		return !StringUtils.isEmpty(password)
				&& password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH
				&& password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
	}
}
