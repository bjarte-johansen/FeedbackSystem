package root.repositories.verification;

import root.includes.proxyrepo.ProxyRepository;
import root.models.verification.VerificationCode;

import java.util.Optional;

public interface VerificationCodeRepository extends ProxyRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmail(String email);
     //Optional<VerificationCode> findByEmailAndHash(String email, String hash);
     void deleteByEmail(String email);
}
