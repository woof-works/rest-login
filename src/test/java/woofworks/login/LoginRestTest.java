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

    /**
     * Test to log in successfully
     */
	@Test
	public void successfulLoginTest() {
		LoginRequestDTO request = buildLoginRequest();

		ResponseEntity<LoginResponseDTO> res = rest.postForEntity("/login", request,
				LoginResponseDTO.class);

		assertIsEqual(HttpStatus.OK, res.getStatusCode());

		LoginResponseDTO dto = res.getBody();
		Assert.isTrue(isNullOrEmpty(dto.token) == false);
	}

    /**
     * Checks that the JSON string is properly formatted and returned
     */
	@Test
	public void checkJsonStringTest() {
        LoginRequestDTO request = buildLoginRequest();

        ResponseEntity<String> res = rest.postForEntity("/login", request,
                String.class);

        assertIsEqual(HttpStatus.OK, res.getStatusCode());

        String json = res.getBody();

        Assert.isTrue(json.startsWith("{\"token\":\""));
    }

    /**
     * Builds a login DTO request
     *
     * @return
     */
    private LoginRequestDTO buildLoginRequest() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.username = "user";
        request.password = "password";

        return request;
    }
}
