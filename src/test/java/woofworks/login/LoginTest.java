package woofworks.login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import woofworks.BaseTest;
import woofworks.dto.LoginRequestDTO;
import woofworks.dto.LoginResponseDTO;
import woofworks.exception.LoginFailedException;
import woofworks.model.User;
import woofworks.security.TokenHandler;
import woofworks.security.TokenUser;
import woofworks.service.UserService;

import java.time.LocalDateTime;

import static com.google.common.base.Objects.equal;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginTest extends BaseTest {

	@Autowired
	private UserService service;

	@Autowired
	private TokenHandler tokenHandler;

	@Before
	public void init() {
		saveUser();
	}

	@Test
	public void generateLoginTokenTest() {
		LoginRequestDTO request = new LoginRequestDTO();
		request.username = "user";
		request.password = "password";

		LoginResponseDTO response = service.logUserIn(request);
		Assert.notNull(response);
		Assert.notNull(response.token);
	}

	@Test(expected = LoginFailedException.class)
	public void noUsernameFoundTest() {
		LoginRequestDTO request = new LoginRequestDTO();
		request.username = "failedUser";
		request.password = "password";

		service.logUserIn(request);
	}

	@Test(expected = LoginFailedException.class)
	public void incorrectPasswordTest() {
		LoginRequestDTO request = new LoginRequestDTO();
		request.username = "user";
		request.password = "password1";

		service.logUserIn(request);
	}

	@Test
	public void logUserInAndRetrieveUserTest() {
		LoginRequestDTO request = new LoginRequestDTO();
		request.username = "user";
		request.password = "password";

		LoginResponseDTO response = service.logUserIn(request);

		User u = service.getUserFromToken(response.token);

		Assert.isTrue(u != null);
	}

	@Test
	public void tokenShouldBeDifferentBetweenMillisecondsTest() {
		User u = new User();
		u.setId(1L);

		LocalDateTime now = LocalDateTime.now();

		TokenUser user1 = new TokenUser(u, now);
		TokenUser user2 = new TokenUser(u, now.plusNanos(1L));

		Assert.isTrue(equal(user1.expires, user2.expires) == false,
				"Epoc long for both users should be different");

		String token1 = tokenHandler.createTokenForUser(user1);
		String token2 = tokenHandler.createTokenForUser(user2);

		Assert.isTrue(equal(token1, token2) == false, "Token for both users should be different");
	}
}
