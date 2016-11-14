package woofworks.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tim on 2016-11-12.
 */
@Getter
@Setter
public class LoginResponseDTO {

    public String token;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token) {
        this.token = token;
    }
}
