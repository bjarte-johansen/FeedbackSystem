package root.models;

import root.interfaces.ITenant;

public class Tenant implements ITenant {
    private Long id;
    private String name;
    private String domain;
    private String apiKey;
    private String email;
    private String passwordHash;
    private String passwordSalt;

    public Tenant() {
        this("", "", "" , "", "", "");
    }

    public Tenant(String name, String domain, String apiKey, String email, String password_hash, String passwordSalt) {
        this.id = null;
        this.name = name;
        this.domain = domain;
        this.apiKey = apiKey;
        this.email = email;
        this.passwordHash = password_hash;
        this.passwordSalt = passwordSalt;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public void setApiKey(String api_key) {
        this.apiKey = api_key;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public void setPasswordHash(String password_hash) {
        this.passwordHash = password_hash;
    }

    @Override
    public String getPasswordSalt() {
        return passwordSalt;
    }

    @Override
    public void setPasswordSalt(String password_salt) {
        this.passwordSalt = password_salt;
    }

    public String toString()
    {
        return "Tenant{id=" + id + ", name='" + name + "', domain='" + domain + "', api_key='" + apiKey + "', email='" + email + "'}";
    }
}
