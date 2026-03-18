package root.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Tenant implements ITenant{
    private Long id;
    private String name;
    private String domain;
    private String api_key;
    private String email;
    private String password_hash;
    private String password_salt;

    public Tenant() {
        this("", "", "" , "", "", "");
    }

    public Tenant(String name, String domain, String api_key, String email, String password_hash, String password_salt) {
        this.id = null;
        this.name = name;
        this.domain = domain;
        this.api_key = api_key;
        this.email = email;
        this.password_hash = password_hash;
        this.password_salt = password_salt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    @NotNull
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(@NotNull String domain) {
        this.domain = domain;
    }

    @Override
    @NotNull
    public String getApi_key() {
        return api_key;
    }

    @Override
    public void setApi_key(@NotNull String api_key) {
        this.api_key = api_key;
    }

    @Override
    @NotNull
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @Override
    @NotNull
    public String getPassword_hash() {
        return password_hash;
    }

    @Override
    public void setPassword_hash(@NotNull String password_hash) {
        this.password_hash = password_hash;
    }

    @Override
    @NotNull
    public String getPassword_salt() {
        return password_salt;
    }

    @Override
    public void setPassword_salt(@NotNull String password_salt) {
        this.password_salt = password_salt;
    }
}
