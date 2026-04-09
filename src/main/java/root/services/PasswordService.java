package root.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import root.app.AppConfig;

import static root.common.utils.Preconditions.checkArgument;


/**
 * Service for handling password hashing and verification.
 * Note: This implementation is for demonstration purposes only and should not be used in production.
 * In a production environment, use a secure hashing algorithm like bcrypt, scrypt, or Argon2.
 */

@Service
public class PasswordService {

    /**
     * Hash a password with, generates its own salt.
     *
     * @param password Password to hash
     * @return Hashed password
     */

    public String hash(String password) {
        checkArgument(password != null && !password.isBlank(), "Password cannot be null or blank");
        checkArgument(password.length() >= AppConfig.MIN_PASSWORD_LENGTH, "Password must be at least 8 characters long");
        checkArgument(password.matches(AppConfig.VALID_PASSWORD_REGEX), "Password contains invalid characters");

        return BCrypt.hashpw(password, BCrypt.gensalt());
    }



    /**
     * Verify a password against a stored hash and salt.
     *
     * @param password Password to verify
     * @param storedHash Stored hash to compare against
     * @return True if the password is correct, false otherwise
     */

    public boolean verify(String password, String storedHash) {
        checkArgument(password != null, "Password cannot be null");
        checkArgument(storedHash != null, "Stored hash cannot be null");

        return BCrypt.checkpw(password, storedHash);
    }
}
