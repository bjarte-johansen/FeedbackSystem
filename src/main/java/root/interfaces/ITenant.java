package root.interfaces;

/***********************************************/

public interface ITenant extends HasId {

    String getName();
    void setName(String name);

    String getDomain();
    void setDomain(String domain);

    String getApiKey();
    void setApiKey(String apiKey);

    String getEmail();
    void setEmail(String email);

    String getPasswordHash();
    void setPasswordHash(String passwordHash);

    String getPasswordSalt();
    void setPasswordSalt(String passwordSalt);
}
