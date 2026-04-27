package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.models.review.ReviewSettings;
import root.repositories.review.ReviewSettingsRepository;

import java.util.Optional;


/**
 * Service to let us use cached review settings so we only memory hit for most often seen review-host pages.
 *
 * <p>
 * Note that this version uses a default of max 10_000 review settings in memory and a TTL of 3 hours
 */

@Service
public class ReviewSettingsService {
    @Autowired
    ReviewSettingsRepository reviewSettingsRepo;


    /**
     * Find uncached review settings for a given externalId if exists.
     *
     * @param externalId the externalId to find review settings for, e.g. "/product/123"
     * @return the settings for the review
     */

    public ReviewSettings findByExternalId(String externalId) {
        return reviewSettingsRepo.findByExternalId(externalId).orElse(null);
    }

    /**
     * Find or create uncached review settings for a given externalId if exists. Automatically persists created
     * entities.
     *
     * @param externalId the externalId to find review settings for, e.g. "/product/123"
     * @return the settings for the review
     */

    public ReviewSettings findOrCreateByExternalId(String externalId) {
        var rs = findByExternalId(externalId);
        if(rs != null) return rs;

        rs = new ReviewSettings();
        rs.setExternalId(externalId);
        rs.setEnableListing(true);
        rs.setEnableSubmit(true);
        reviewSettingsRepo.save(rs);
        return rs;
    }
}
