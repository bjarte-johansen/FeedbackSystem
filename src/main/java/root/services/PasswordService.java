package root.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import root.app.AppConfig;

import static root.common.utils.Preconditions.checkArgument;


/**
 * Service for handling password hashing and verification.
 * Note: This implementation is for demonstration purposes only and should not be used in production.
 * In a production environment, use a secure hashing algorithm like bcrypt, scrypt, or Argon2.
 */

@Service
public class PasswordService implements PasswordEncoder {

    /**
     * Hash a password with, generates its own salt.
     *
     * @param password Password to hash
     * @return Hashed password
     */

    public String encode(CharSequence password) {
        checkArgument(password != null && !password.isEmpty(), "Password cannot be null or blank");
        checkArgument(password.length() >= AppConfig.MIN_PASSWORD_LENGTH, "Password must be at least 8 characters long");
        checkArgument(password.toString().matches(AppConfig.VALID_PASSWORD_REGEX), "Password contains invalid characters");

        return BCrypt.hashpw(password.toString(), BCrypt.gensalt());
    }


    /**
     * Verify a password against a stored hash and salt.
     *
     * @param password Password to verify
     * @param encodedPassword Stored hash to compare against
     * @return True if the password is correct, false otherwise
     */

    public boolean matches(CharSequence password, String encodedPassword) {
        checkArgument(password != null, "Password cannot be null");
        checkArgument(encodedPassword != null, "Stored hash cannot be null");

        return BCrypt.checkpw(password.toString(), encodedPassword);
    }
}