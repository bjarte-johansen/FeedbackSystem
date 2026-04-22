package root.includes.proxyrepo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.includes.quicktests.repofun.FantasyRepository;
import root.repositories.*;

@Configuration
public class ProxyFactoryBeans {
    @Bean
    public static FantasyRepository createFantasyRepository() {
        return RepositoryProxyConstructor.create(FantasyRepository.class);
    }

    @Bean
    public static VerificationCodeRepository createVerificationCodeRepository() {
        return RepositoryProxyConstructor.create(VerificationCodeRepository.class);
    }

    @Bean
    public static TenantDomainRepository createTenantDomainRepository() {
        return RepositoryProxyConstructor.create(TenantDomainRepository.class);
    }

    @Bean
    public static ReviewRepository createReviewRepository() {
        return RepositoryProxyConstructor.create(ReviewRepository.class);
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
    public static ReviewVoteRepository createReviewVoteRepository() {
        return RepositoryProxyConstructor.create(ReviewVoteRepository.class);
    }
}
