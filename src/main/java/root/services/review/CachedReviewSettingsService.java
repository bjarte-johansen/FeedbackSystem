package root.services.review;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.models.review.ReviewSettings;
import root.services.ReviewSettingsService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CachedReviewSettingsService {
    @Autowired
    ReviewSettingsService reviewSettingsService;

    private static final Cache<String, ReviewSettings> CACHE = CacheBuilder.newBuilder()
        .maximumSize(10_000)                 // memory cap
        .expireAfterAccess(3, TimeUnit.HOURS) // refresh on access
        .recordStats()                        // optional
        .build();

    /**
     * Find cached review settings for a given externalId if exists. Creates a new entry with default review settings
     * if one was not found in database, and persists and caches the result.
     * TODO: rework the javadoc description, its bad, im in a hurry
     *
     * @param externalId the externalId to find review settings for, e.g. "/product/123"
     * @return the settings for the review, always non-null
     */

    public ReviewSettings findOrCreateByExternalId(String externalId) {
        try {
            return CACHE
                .get(externalId, () -> reviewSettingsService.findOrCreateByExternalId(externalId));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    /**
     * Invalidate / evict all data from cache. This can be used to force refresh of all review settings from database,
     * for example after a bulk update of review settings.
     */

    public void evictAll(){
        CACHE.invalidateAll();
    }


    /**
     * Evict single entry from cache
     *
     * @param externalId
     */

    public void evict(String externalId){
        if(externalId == null) return;

        CACHE.invalidate(externalId);
    }
}
