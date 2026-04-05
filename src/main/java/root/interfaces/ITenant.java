package root.interfaces;

/**
 * Represents a tenant in the system. A tenant is an entity that can have multiple users and resources associated with it.
 * The tenant is responsible for managing its own users and resources, and it can have its own settings and configurations.
 * The ITenant interface defines the properties and methods that a tenant should have, such as name, domain, API key,
 * email, password hash, and password salt. This interface can be implemented by any class that represents a tenant in the
 * system, such as a Tenant class that is used to store tenant information in a database.
 *
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */

public interface ITenant extends HasId {

    /**
     * Gets the name of the tenant. The name is a unique identifier for the tenant and is used to distinguish it from
     * other tenants in the system.
     *
     * @return
     */
    String getName();

    /**
     * Sets the name of the tenant.
     *
     * @param name
     */
    void setName(String name);

    /**
     * Gets the domain of the tenant.
     *
     * @return
     */
    String getDomain();

    /**
     * Sets the domain of the tenant.
     * @param domain
     */
    void setDomain(String domain);

    /**
     * Gets the API key of the tenant. The API key is a unique identifier that is used to authenticate the tenant when
     * making API requests.
     *
     * @return
     */
    String getApiKey();

    /**
     * Sets the API key of the tenant. The API key is a unique identifier that is used to authenticate the tenant when
     * making API requests.
     *
     * @param apiKey
     */
    void setApiKey(String apiKey);

    /**
     * Gets the email address associated with the tenant. This email address is used for communication and notifications
     * related to the tenant's account.
     *
     * @return
     */
    String getEmail();

    /**
     * Sets the email address associated with the tenant. This email address is used for communication and notifications
     * related to the tenant's account.
     *
     * @param email
     */
    void setEmail(String email);

    /**
     * Gets the password hash for the tenant. The password hash is a secure representation of the tenant's password and
     * is used for authentication purposes.
     *
     * @return
     */
    String getPasswordHash();

    /**
     * Sets the password hash for the tenant. The password hash is a secure representation of the tenant's password and
     * is used for authentication purposes.
     * @param passwordHash
     */
    void setPasswordHash(String passwordHash);

    /**
     * Gets the password salt for the tenant. The password salt is a random value that is used in conjunction with the
     * password hash to enhance security and protect against certain types of attacks.
     *
     * @return
     */
    String getPasswordSalt();

    /**
     * Sets the password salt for the tenant. The password salt is a random value that is used in conjunction with the
     * password hash to enhance security and protect against certain types of attacks.
     *
     * @param passwordSalt
     */
    void setPasswordSalt(String passwordSalt);
}
