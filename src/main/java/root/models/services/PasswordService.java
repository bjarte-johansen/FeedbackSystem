package root.models.services;

import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * Service for handling password hashing and verification.
 * Note: This implementation is for demonstration purposes only and should not be used in production.
 * In a production environment, use a secure hashing algorithm like bcrypt, scrypt, or Argon2.
 */

@Service
public class PasswordService {

    /**
     * Generate a random salt for password hashing.
     *
     * @return Generated salt
     */

    public String generateSalt() {
        // Implement a secure random salt generator
        // For demonstration purposes, we'll use a simple random string (not recommended for production)

        // TODO/DEBUG: Replace with a secure random salt generator
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }


    /**
     * Hash a password with a given salt.
     *
     * @param password Password to hash
     * @param salt Salt to use for hashing
     * @return Hashed password
     */

    public static String hash(String password, String salt) {
        Objects.requireNonNull(password, "Password cannot be null");
        Objects.requireNonNull(salt, "Salt cannot be null");

        // Implement a secure hashing algorithm, e.g., bcrypt, scrypt, or Argon2
        // For demonstration purposes, we'll use a simple hash (not recommended for production)

        // TODO/DEBUG: Replace with a secure hashing algorithm
        String saltedPassword = salt + ";" + password;
        return Integer.toHexString(saltedPassword.hashCode());
    }


    /**
     * Verify a password against a stored hash and salt.
     *
     * @param password Password to verify
     * @param salt Salt used for hashing
     * @param storedHash Stored hash to compare against
     * @return True if the password is correct, false otherwise
     */

    public static boolean verify(String password, String salt, String storedHash) {
        Objects.requireNonNull(password, "Password cannot be null");
        Objects.requireNonNull(salt, "Salt cannot be null");
        Objects.requireNonNull(storedHash, "StoredHash cannot be null");

        // compute the hash of the provided password and salt, and compare it to the stored hash
        String computedHash = hash(password, salt);
        return computedHash.equals(storedHash);
    }
}
