package woofworks.dto;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Tim on 2016-11-12.
 */
public class LoginRequestDTO {
    @NotBlank(message = "username can't be blank")
    public String username;

    @NotBlank(message = "psasword can't be blank")
    public String password;
}
