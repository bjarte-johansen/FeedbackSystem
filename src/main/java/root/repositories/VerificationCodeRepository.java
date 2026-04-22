package root.repositories;

import root.includes.proxyrepo.ProxyRepository;
import root.models.VerificationCode;

import java.util.Optional;

public interface VerificationCodeRepository extends ProxyRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmail(String email);
     //Optional<VerificationCode> findByEmailAndHash(String email, String hash);
     void deleteByEmail(String email);
}
