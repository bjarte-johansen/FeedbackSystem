package root.repositories;

import org.springframework.stereotype.Repository;
import root.app.ReviewQueryOptions;
import root.includes.proxyrepo.ProxyRepository;
import root.models.Review;
import root.models.ReviewSettings;

import java.util.List;
import java.util.Optional;


/**
 * Repository for ReviewSettings, settings that can be edited/set for specific externalIds to modify what
 * is allowed to do with reviews by externalId
 */

//@Repository
public interface ReviewSettingsRepository extends ProxyRepository<ReviewSettings, Long>, ReviewRepositoryInterface{
    Optional<ReviewSettings> findByExternalId(String externalId);
    long countByExternalId(String externalId);
}
