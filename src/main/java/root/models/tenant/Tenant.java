package root.models.tenant;

import root.interfaces.HasId;


/**
 * Represents a tenant in the system. A tenant is an entity that can have multiple users and resources associated with it.
 * The tenant is responsible for managing its own users and resources, and it can have its own settings and configurations.
 * The ITenant interface defines the properties and methods that a tenant should have, such as name, domain, API key,
 * email, password hash, and password salt. This interface can be implemented by any class that represents a tenant in the
 * system, such as a Tenant class that is used to store tenant information in a database.
 *
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */

public class Tenant implements HasId {
    private Long id;
    private String name;
    private String domain;
    private String apiKey;
    private String email;
    private String passwordHash;
    private String passwordSalt;
    private String schemaName;
    private boolean enableListing = true;
    private boolean enableSubmit = true;
    private int scoreMin;
    private int scoreMax;


    /**
     * Constructs a new Tenant object
     */

    public Tenant() {
        this("", "", "" , "", "", "" , "");
    }


    /**
     * Construct a new Tenant object with given properties
     *
     * @param name
     * @param domain
     * @param apiKey
     * @param email
     * @param password_hash
     * @param passwordSalt
     * @param schemaName
     */
    public Tenant(String name, String domain, String apiKey, String email, String password_hash, String passwordSalt, String schemaName) {
        //this.id = null;
        this.name = name;
        this.domain = domain;
        this.apiKey = apiKey;
        this.email = email;
        this.passwordHash = password_hash;
        this.passwordSalt = passwordSalt;
        this.schemaName = schemaName;
    }

    /** get id */
    public Long getId() {
        return id;
    }

    /** set id */
    public void setId(long id) {
        this.id = id;
    }

    /** get name */
    public String getName() {
        return name;
    }

    /** set name */
    public void setName(String name) {
        this.name = name;
    }

    /** get domain */
    public String getDomain() {
        return domain;
    }

    /** set domain */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /** get api key */
    public String getApiKey() {
        return apiKey;
    }

    /** set api key */
    public void setApiKey(String api_key) {
        this.apiKey = api_key;
    }

    /** set email */
    public String getEmail() {
        return email;
    }

    /** set email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** get password hash */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** set password hash */
    public void setPasswordHash(String password_hash) {
        this.passwordHash = password_hash;
    }

    /** get password salt */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /** set password salt */
    public void setPasswordSalt(String password_salt) {
        this.passwordSalt = password_salt;
    }

    /** get schema name */
    public String getSchemaName() { return schemaName; }

    /** set schema name */
    public void setSchemaName(String schemaName) { this.schemaName = schemaName; }


    public boolean getEnableListing() { return enableListing; }
    public void setEnableListing(boolean enableListing) { this.enableListing = enableListing; }

    public boolean getEnableSubmit() { return enableSubmit; }
    public void setEnableSubmit(boolean enableSubmit) { this.enableSubmit = enableSubmit; }

    public int getScoreMin() { return scoreMin; }
    public void setScoreMin(int score_min) { this.scoreMin = score_min; }

    public int getScoreMax() { return scoreMax; }
    public void setScoreMax(int score_max) { this.scoreMax = score_max; }


    @Override
    public String toString()
    {
        return "Tenant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", passwordSalt='" + passwordSalt + '\'' +
                ", schemaName='" + schemaName + '\'' +
                '}';
    }
}
