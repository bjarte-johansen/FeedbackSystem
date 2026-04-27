package root.includes.proxyrepo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.includes.quicktests.repofun.FantasyRepository;
import root.repositories.review.ReviewRepository;
import root.repositories.review.ReviewSettingsRepository;
import root.repositories.review.ReviewVoteRepository;
import root.repositories.tenant.TenantDomainRepository;
import root.repositories.tenant.TenantRepository;
import root.repositories.verification.VerificationCodeRepository;

@Configuration
public class ProxyFactoryBeans {
    /*
    bean used to test proxy
    */

    @Bean
    public static FantasyRepository createFantasyRepository() {
        return RepositoryProxyConstructor.create(FantasyRepository.class);
    }


    /*
    actual beans used in project
     */

    @Bean
    public static VerificationCodeRepository createVerificationCodeRepository() {
        return RepositoryProxyConstructor.create(VerificationCodeRepository.class);
    }

    @Bean
    public static ReviewRepository createReviewRepository() {
        return RepositoryProxyConstructor.create(ReviewRepository.class);
    }

    @Bean
    public static ReviewVoteRepository createReviewVoteRepository() {
        return RepositoryProxyConstructor.create(ReviewVoteRepository.class);
    }

    @Bean
    public static ReviewSettingsRepository createReviewSettingsRepository() {
        return RepositoryProxyConstructor.create(ReviewSettingsRepository.class);
    }

//
//    @Bean
//    public static ReviewerRepository createReviewerRepository() {
//        return RepositoryProxyConstructor.create(ReviewerRepository.class);
//    }

    @Bean
    public static TenantRepository createTenantRepository() {
        return RepositoryProxyConstructor.create(TenantRepository.class);
    }

    @Bean
    public static TenantDomainRepository createTenantDomainRepository() {
        return RepositoryProxyConstructor.create(TenantDomainRepository.class);
    }
}
