package root.models;

import jakarta.persistence.Table;
import root.interfaces.HasId;

import java.lang.reflect.Field;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Represents a tenant in the system. A tenant is an entity that can have multiple users and resources associated with it.
 * The tenant is responsible for managing its own users and resources, and it can have its own settings and configurations.
 * The ITenant interface defines the properties and methods that a tenant should have, such as name, domain, API key,
 * email, password hash, and password salt. This interface can be implemented by any class that represents a tenant in the
 * system, such as a Tenant class that is used to store tenant information in a database.
 *
 * TODO: Any code inserted here must have javadoc comments. This is a requirement for all code in this project.
 */

@Table(name="")
public class NonPersistableTenant {
    private final Long id;
    private final String name;
    private final String domain;
    private final String apiKey;
    private final String email;
    private final String passwordHash;
    private final String passwordSalt;
    private final String schemaName;
    private final boolean enableListing;
    private final int scoreMin;
    private final int scoreMax;


    /**
     * Constructs a new Tenant object
     */

    public NonPersistableTenant() {
        this.id = null;
        this.name = "";
        this.domain = "";
        this.apiKey = "";
        this.email = "";
        this.passwordHash = "";
        this.passwordSalt = "";
        this.schemaName = "";
        this.enableListing = false;
        this.scoreMin = 0;
        this.scoreMax = 0;
    }


    /**
     * Construct a new Tenant object with given properties
     *
     * @param t Tenant object to clone without password/salt information
     */
    public NonPersistableTenant(Tenant t) {
        checkArgument(t != null, "Copy constructor cannot be called with null argument");

        this.id = t.getId();
        this.name = t.getName();
        this.domain = t.getDomain();
        this.apiKey = t.getApiKey();
        this.email = t.getEmail();
        this.passwordHash = "";
        this.passwordSalt = "";
        this.schemaName = t.getSchemaName();
        this.enableListing = t.getEnableListing();
        this.scoreMin = t.getScoreMin();
        this.scoreMax = t.getScoreMax();
    }

    /** get id */
    public Long getId() {return id;}

    /** get domain */
    public String getDomain() {return domain;}

    /** get api key */
    public String getApiKey() {return apiKey;}

    /** get email */
    public String getEmail() {return email;}

    /** get password hash */
    public String getPasswordHash() {return passwordHash;}

    /** get password salt */
    public String getPasswordSalt() {return passwordSalt;}

    /** get schema name */
    public String getSchemaName() { return schemaName; }

    public boolean getEnableListing() { return enableListing; }
    public int getScoreMin() { return scoreMin; }
    public int getScoreMax() { return scoreMax; }


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
                ", enableListing=" + enableListing +
                ", scoreMin=" + scoreMin +
                ", scoreMax=" + scoreMax +
                '}';
    }
}
