package logia.quanlyso.service.dto;

import logia.quanlyso.config.Constants;
import logia.quanlyso.domain.Authority;
import logia.quanlyso.domain.User;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 *
 * @author Dai Mai
 */
public class UserDTO {

	/** The id. */
	private Long			id;

	/** The login. */
	@NotBlank
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	private String			login;

	/** The first name. */
	@Size(max = 50)
	private String			firstName;

	/** The last name. */
	@Size(max = 50)
	private String			lastName;

	/** The email. */
	@Email
	@Size(min = 5, max = 100)
	private String			email;

	/** The image url. */
	@Size(max = 256)
	private String			imageUrl;

	/** The activated. */
	private boolean			activated	= false;

	/** The lang key. */
	@Size(min = 2, max = 5)
	private String			langKey;

	/** The created by. */
	private String			createdBy;

	/** The created date. */
	private Instant			createdDate;

	/** The last modified by. */
	private String			lastModifiedBy;

	/** The last modified date. */
	private Instant			lastModifiedDate;

	/** The authorities. */
	private Set<String>		authorities;

	/** The grant access date. */
	private ZonedDateTime	grantAccessDate;

	/** The revoke access date. */
	private ZonedDateTime	revokeAccessDate;

	/**
	 * Instantiates a new user DTO.
	 */
	public UserDTO() {
		// Empty constructor needed for MapStruct.
	}

	/**
	 * Instantiates a new user DTO.
	 *
	 * @param user the user
	 */
	public UserDTO(User user) {
		this(user.getId(), user.getLogin(), user.getFirstName(), user.getLastName(),
				user.getEmail(), user.getActivated(), user.getImageUrl(), user.getLangKey(),
				user.getCreatedBy(), user.getCreatedDate(), user.getLastModifiedBy(),
				user.getLastModifiedDate(),
				user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()),
				user.getGrantAccessDate(), user.getRevokeAccessDate());
	}

	/**
	 * Instantiates a new user DTO.
	 *
	 * @param id the id
	 * @param login the login
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param email the email
	 * @param activated the activated
	 * @param imageUrl the image url
	 * @param langKey the lang key
	 * @param createdBy the created by
	 * @param createdDate the created date
	 * @param lastModifiedBy the last modified by
	 * @param lastModifiedDate the last modified date
	 * @param authorities the authorities
	 * @param grantAccessDate the grant access date
	 * @param revokeAccessDate the revoke access date
	 */
	public UserDTO(Long id, String login, String firstName, String lastName, String email,
			boolean activated, String imageUrl, String langKey, String createdBy,
			Instant createdDate, String lastModifiedBy, Instant lastModifiedDate,
			Set<String> authorities, ZonedDateTime grantAccessDate,
			ZonedDateTime revokeAccessDate) {

		this.id = id;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.activated = activated;
		this.imageUrl = imageUrl;
		this.langKey = langKey;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDate;
		this.authorities = authorities;
		this.grantAccessDate = grantAccessDate;
		this.revokeAccessDate = revokeAccessDate;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the login.
	 *
	 * @return the login
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * Sets the login.
	 *
	 * @param login the new login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Gets the image url.
	 *
	 * @return the image url
	 */
	public String getImageUrl() {
		return this.imageUrl;
	}

	/**
	 * Checks if is activated.
	 *
	 * @return true, if is activated
	 */
	public boolean isActivated() {
		return this.activated;
	}

	/**
	 * Gets the lang key.
	 *
	 * @return the lang key
	 */
	public String getLangKey() {
		return this.langKey;
	}

	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public String getCreatedBy() {
		return this.createdBy;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Instant getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Gets the last modified by.
	 *
	 * @return the last modified by
	 */
	public String getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	/**
	 * Gets the last modified date.
	 *
	 * @return the last modified date
	 */
	public Instant getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Gets the authorities.
	 *
	 * @return the authorities
	 */
	public Set<String> getAuthorities() {
		return this.authorities;
	}

	/**
	 * @return the grantAccessDate
	 */
	public ZonedDateTime getGrantAccessDate() {
		return this.grantAccessDate;
	}

	/**
	 * @return the revokeAccessDate
	 */
	public ZonedDateTime getRevokeAccessDate() {
		return this.revokeAccessDate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserDTO{" + "login='" + this.login + '\'' + ", firstName='" + this.firstName + '\''
				+ ", lastName='" + this.lastName + '\'' + ", email='" + this.email + '\'' + ", imageUrl='"
				+ this.imageUrl + '\'' + ", activated=" + this.activated + ", langKey='" + this.langKey + '\''
				+ ", createdBy=" + this.createdBy + ", createdDate=" + this.createdDate + ", lastModifiedBy='"
				+ this.lastModifiedBy + '\'' + ", lastModifiedDate=" + this.lastModifiedDate
				+ ", authorities=" + this.authorities + ", grantAccessDate=" + this.grantAccessDate
				+ ", revokeAccessDate=" + this.revokeAccessDate + "}";
	}
}
