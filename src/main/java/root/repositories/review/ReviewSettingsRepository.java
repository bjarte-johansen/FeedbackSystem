package root.repositories.review;

import root.includes.proxyrepo.ProxyRepository;
import root.models.review.ReviewSettings;

import java.util.Optional;


/**
 * Repository for ReviewSettings, settings that can be edited/set for specific externalIds to modify what
 * is allowed to do with reviews by externalId
 */

//@Repository
public interface ReviewSettingsRepository extends ProxyRepository<ReviewSettings, Long>, ReviewRepositoryCustom {
    Optional<ReviewSettings> findByExternalId(String externalId);
    long countByExternalId(String externalId);
}
