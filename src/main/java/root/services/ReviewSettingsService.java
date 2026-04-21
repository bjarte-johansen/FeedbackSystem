package root.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.models.ReviewSettings;
import root.repositories.ReviewSettingsRepository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * Service to let us use cached review settings so we only memory hit for most often seen review-host pages.
 *
 * <p>
 * Note that this version uses a default of max 10_000 review settings in memory and a TTL of 3 hours
 */

@Service
public class ReviewSettingsService {
    private static Cache<String, Optional<ReviewSettings>> CACHE = CacheBuilder.newBuilder()
        .maximumSize(10_000)                 // memory cap
        .expireAfterAccess(3, TimeUnit.HOURS) // refresh on access
        .recordStats()                        // optional
        .build();

    @Autowired
    ReviewSettingsRepository reviewSettingsRepo;


    /**
     * Find cached review settings for a given externalId if exists. Creates a new entry with default review settings
     * if one was not found in database, and persists and caches the result.
     * TODO: rework the javadoc description, its bad, im in a hurry
     *
     * @param externalId the externalId to find review settings for, e.g. "/product/123"
     * @return the settings for the review, always non-null
     */

    public ReviewSettings findCachedReviewSettings(String externalId) {
        try {
            return CACHE
                .get(externalId, () -> {
                    var rs = findUncachedReviewSettings(externalId);
                    if(rs.isEmpty()){
                        var newRs = new ReviewSettings();
                        newRs.setExternalId(externalId);
                        newRs.setEnableListing(true);
                        newRs.setEnableSubmit(true);
                        reviewSettingsRepo.save(newRs);
                        rs = Optional.of(newRs);
                    }
                    return rs;
                })
                .orElseThrow(() -> new RuntimeException("Unexpected empty review settings"));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    /**
     * Find uncached review settings for a given externalId if exists.
     *
     * @param externalId the externalId to find review settings for, e.g. "/product/123"
     * @return the settings for the review
     */

    public Optional<ReviewSettings> findUncachedReviewSettings(String externalId) {
        return reviewSettingsRepo.findByExternalId(externalId);
    }
}
