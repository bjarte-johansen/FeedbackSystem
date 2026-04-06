package root.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import root.models.Review;

import java.util.List;


@Repository
public class TenantRepositoryCustomImpl {
    @Autowired
    ReviewRepository reviewRepo;

    @Autowired
    TenantRepository tenantRepo;
}
