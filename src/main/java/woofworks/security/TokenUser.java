package woofworks.security;


import lombok.Getter;
import lombok.Setter;
import woofworks.model.User;

import java.time.LocalDateTime;

/**
 * Represents a user that will be converted into a token and returned to the client requesting it
 * Created by Tim on 07-Nov-16.
 */
@Getter
@Setter
public class TokenUser {
    public Long id;

    public String username;

    public LocalDateTime expires;

    public TokenUser() {

    }

    public TokenUser(User user) {
        this(user, LocalDateTime.now());
    }

    public TokenUser(User user, LocalDateTime ref) {
        id = user.getId();
        username = user.getLogin();
        expires = ref.plusDays(10);
    }
}
