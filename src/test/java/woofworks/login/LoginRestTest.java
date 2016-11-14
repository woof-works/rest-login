package woofworks.login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import woofworks.BaseTest;
import woofworks.dto.LoginRequestDTO;
import woofworks.dto.LoginResponseDTO;
import woofworks.service.UserService;

import static com.google.common.base.Strings.isNullOrEmpty;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginRestTest extends BaseTest {
	@Autowired
	private TestRestTemplate rest;

	@Autowired
	private UserService service;

	@Before
	public void init() {
		saveUser();
	}

	@Test
	public void successfulLoginTest() {
		LoginRequestDTO request = new LoginRequestDTO();
		request.username = "user";
		request.password = "password";

		ResponseEntity<LoginResponseDTO> res = rest.postForEntity("/login", request,
				LoginResponseDTO.class);

		assertIsEqual(HttpStatus.OK, res.getStatusCode());

		LoginResponseDTO dto = res.getBody();
		Assert.isTrue(isNullOrEmpty(dto.token) == false);
	}
}
