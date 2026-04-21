package root.repositories.deprecated;

import root.includes.proxyrepo.ProxyRepository;
import root.models.UserVerificationRecord;

@Deprecated
public interface UserVerificationRepository extends ProxyRepository<UserVerificationRecord, Long> {
}