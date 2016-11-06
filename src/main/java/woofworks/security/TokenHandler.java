package woofworks.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Taken from https://www.javacodegeeks.com/2014/10/stateless-spring-security-part-2-stateless-authentication.html
 * Modified a little to suit local app needs but base remains mostly unchanged
 * 
 * Created by Tim on 07-Nov-16.
 */
@Configuration
public class TokenHandler {

    private static final String HMAC_ALGO = "HmacSHA256";

    private static final String SEPARATOR = ".";

    private static final String SEPARATOR_SPLITTER = "\\.";

    private final Mac hmac;

    private ObjectMapper objectMapper;

    public TokenHandler(@Value("${token.secret}") String secret) {
        try {
            byte[] secretKey = DatatypeConverter.parseBase64Binary(secret);
            hmac = Mac.getInstance(HMAC_ALGO);
            hmac.init(new SecretKeySpec(secretKey, HMAC_ALGO));

            //A seperate object mapper is created here, since the serialization / deserialization may be different from the main object mapper that is used by the rest services
            objectMapper = new ObjectMapper();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("failed to initialize HMAC: " + e.getMessage(), e);
        }
    }

    public TokenUser parseUserFromToken(String token) {
        final String[] parts = token.split(SEPARATOR_SPLITTER);
        if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
            try {
                final byte[] userBytes = fromBase64(parts[0]);
                final byte[] hash = fromBase64(parts[1]);

                boolean validHash = Arrays.equals(createHmac(userBytes), hash);
                if (validHash) {
                    final TokenUser user = fromJSON(userBytes);

                    LocalDateTime expiryDate = user.expires;
                    if (LocalDateTime.now().isBefore(expiryDate)) {
                        return user;
                    }
                }
            } catch (IllegalArgumentException e) {
                // log tempering attempt here
            }
        }
        return null;
    }

    public String createTokenForUser(TokenUser user) {
        byte[] userBytes = toJSON(user);
        byte[] hash = createHmac(userBytes);
        final StringBuilder sb = new StringBuilder(170);
        sb.append(toBase64(userBytes));
        sb.append(SEPARATOR);
        sb.append(toBase64(hash));
        return sb.toString();
    }

    private TokenUser fromJSON(final byte[] userBytes) {
        try {
            return objectMapper.readValue(new ByteArrayInputStream(userBytes),
                    TokenUser.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private byte[] toJSON(TokenUser user) {
        try {
            return objectMapper.writeValueAsBytes(user);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String toBase64(byte[] content) {
        return DatatypeConverter.printBase64Binary(content);
    }

    private byte[] fromBase64(String content) {
        return DatatypeConverter.parseBase64Binary(content);
    }

    // synchronized to guard internal hmac object
    private synchronized byte[] createHmac(byte[] content) {
        return hmac.doFinal(content);
    }
    /*
     * public static void main(String[] args) {
	 * Date start = new Date();
	 * byte[] secret = new byte[70];
	 * new java.security.SecureRandom().nextBytes(secret);
	 *
	 * TokenHandler tokenHandler = new TokenHandler(secret);
	 * for (int i = 0; i < 1000; i++) {
	 * final User user = new User(java.util.UUID.randomUUID().toString().substring(0, 8), new Date(
	 * new Date().getTime() + 10000));
	 * user.grantRole(UserRole.ADMIN);
	 * final String token = tokenHandler.createTokenForUser(user);
	 * final User parsedUser = tokenHandler.parseUserFromToken(token);
	 * if (parsedUser == null || parsedUser.getUsername() == null) {
	 * System.out.println("error");
	 * }
	 * }
	 * System.out.println(System.currentTimeMillis() - start.getTime());
	 * }
	 */
}

