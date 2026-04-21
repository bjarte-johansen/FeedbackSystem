package root.models;

import jakarta.persistence.Table;
import root.interfaces.HasId;

@Table(name = "review_settings")
public class ReviewSettings implements HasId {
    private Long id;
    private String externalId;
    private boolean enableListing;
    private boolean enableSubmit;

    /**
     * Unique identifier for the review settings entry.
     */
    public Long getId(){ return id; }

    /**
     * Unique identifier for the review settings entry.
     */
    public void setId(long id){ this.id=id; }

    /**
     * Returns the external identifier (e.g. page or product path) these review settings apply to.
     */
    public String getExternalId(){ return externalId; }

    /**
     * Sets the external identifier (e.g. page or product path) these review settings apply to.
     *
     * @param externalId
     */
    public void setExternalId(String externalId){ this.externalId = externalId; }

    /**
     * Indicates whether listing (viewing) reviews is enabled for this externalId.
     *
     * @return true if reviews can be displayed, false otherwise
     */
    public boolean getEnableListing(){ return enableListing; }

    /**
     * Set whether listing (viewing) reviews is enabled for this externalId.
     *
     * @param enableListing
     */
    public void setEnableListing(boolean enableListing){ this.enableListing = enableListing; }

    /**
     * Indicates whether submitting is enabled for this externalId.
     *
     * @return true if submit is allowed
     */
    public boolean getEnableSubmit(){ return enableSubmit; }

    /**
     * Set whether submitting is enabled for this externalId.
     *
     * @param enableSubmit
     */
    public void setEnableSubmit(boolean enableSubmit){ this.enableSubmit = enableSubmit; }
}
