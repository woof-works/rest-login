package woofworks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import woofworks.dto.LoginRequestDTO;
import woofworks.dto.LoginResponseDTO;
import woofworks.service.UserService;

/**
 * Created by Tim on 11-Nov-16.
 */
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponseDTO login(@Validated @RequestBody LoginRequestDTO request) {
        return userService.logUserIn(request);
    }
}
