package root.models;

import root.interfaces.HasId;

import java.time.Instant;

/**
 * Class to store verification data, getter/setters written with javadoc by chatgpt because ...
 */

public class VerificationCode implements HasId {
    private Long id;
    private String hash;
    private String email;
    private Instant expiresAt;
    private int attempts;

    /** @return id */ public Long getId(){return id;}
    /** @param id set id */ public void setId(long id){this.id=id;}

    /** @return hash */ public String getHash(){return hash;}
    /** @param hash set hash */ public void setHash(String hash){this.hash=hash;}

    /** @return email */ public String getEmail(){return email;}
    /** @param email set email */ public void setEmail(String email){this.email=email;}

    /** @return expiresAt */ public Instant getExpiresAt(){return expiresAt;}
    /** @param expiresAt set expiresAt */ public void setExpiresAt(Instant expiresAt){this.expiresAt=expiresAt;}

    /** @return attempts */ public int getAttempts(){return attempts;}
    /** @param attempts set attempts */ public void setAttempts(int attempts){this.attempts=attempts;}
}
