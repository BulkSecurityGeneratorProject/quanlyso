package logia.quanlyso.web.rest;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;

import logia.quanlyso.security.jwt.JWTConfigurer;
import logia.quanlyso.security.jwt.TokenProvider;
import logia.quanlyso.web.rest.vm.LoginVM;

/**
 * Controller to authenticate users.
 *
 * @author Dai Mai
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    /** The log. */
    private final Logger log = LoggerFactory.getLogger(UserJWTController.class);

    /** The token provider. */
    private final TokenProvider tokenProvider;

    /** The authentication manager. */
    private final AuthenticationManager authenticationManager;

    /**
     * Instantiates a new user JWT controller.
     *
     * @param tokenProvider the token provider
     * @param authenticationManager the authentication manager
     */
    public UserJWTController(TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Authorize.
     *
     * @param loginVM the login VM
     * @param response the response
     * @return the response entity
     */
    @PostMapping("/authenticate")
    @Timed
    public ResponseEntity authorize(@Valid @RequestBody LoginVM loginVM, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return ResponseEntity.ok(new JWTToken(jwt));
        } catch (AuthenticationException ae) {
            log.trace("Authentication exception trace: {}", ae);
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException",
                ae.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Object to return as body in JWT Authentication.
     *
     * @author Dai Mai
     */
    static class JWTToken {

        /** The id token. */
        private String idToken;

        /**
         * Instantiates a new JWT token.
         *
         * @param idToken the id token
         */
        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        /**
         * Gets the id token.
         *
         * @return the id token
         */
        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        /**
         * Sets the id token.
         *
         * @param idToken the new id token
         */
        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
