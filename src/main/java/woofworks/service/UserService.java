package woofworks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woofworks.dto.LoginRequestDTO;
import woofworks.dto.LoginResponseDTO;
import woofworks.exception.LoginFailedException;
import woofworks.model.SessionToken;
import woofworks.model.User;
import woofworks.repository.UserRepository;
import woofworks.security.TokenHandler;
import woofworks.security.TokenUser;

/**
 * Created by Tim on 11-Nov-16.
 */
@Service
@Transactional
public class UserService extends BaseService {

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * Logs in the user and returns the response DTO that contains the login token
     *
     * @param request
     * @return
     */
    public LoginResponseDTO logUserIn(LoginRequestDTO request) {
        User u = getUserByUserName(request.username);

        // Verify if the username is valid
        if (u == null) {
            throwLoginFailedException();
        }

        // Verify if the password is valid
        boolean validPassword = encoder.matches(request.password, u.getPassword());
        if (validPassword == false) {
            throwLoginFailedException();
        }

        return logUserIn(u);
    }

    /**
     * Logs the specified user in
     *
     * @param user
     * @return
     */
    public LoginResponseDTO logUserIn(User user) {
        // Generate the token based on the user
        TokenUser tokenUser = new TokenUser(user);
        String token = tokenHandler.createTokenForUser(tokenUser);

        saveSessionToken(tokenUser, token);

        return new LoginResponseDTO(token);
    }

    /**
     * Saves a session token
     *
     * @param user
     * @param token
     */
    private void saveSessionToken(TokenUser user, String token) {
        SessionToken session = new SessionToken();
        session.setExpires(user.expires);
        session.setToken(token);
        session.setSessionUser(get(User.class, user.id));

        save(session);
    }

    /**
     * Throws a login failed exception
     */
    private void throwLoginFailedException() {
        throw new LoginFailedException();
    }

    /**
     * Returns the user object by it's username
     *
     * @param username
     * @return
     */
    public User getUserByUserName(String username) {
        return userRepository.getUserByUserName(username);
    }
}
