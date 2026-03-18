package root.models.repositories;

import root.models.IReview;
import root.models.ITenant;
import root.models.interfaces.ITenantRepository;

import java.util.List;
import java.util.Optional;

public class JdbcTenantRepository implements ITenantRepository {


    /**
     * Saves the tenant to the database. If the tenant has an ID, it updates the existing tenant; otherwise, it creates a new tenant.
     *
     * @return The saved tenant with its ID populated if it was created.
     */
    public ITenant save(ITenant tenant){
        return (tenant.getId() == null) ? create(tenant) : update(tenant);
    }


    /**
     * Create a new tenant. The tenant must not already exist.
     *
     * @param tenant The tenant to create.
     */
    @Override
    public ITenant create(ITenant tenant) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Update a tenant. The tenant must already exist.
     *
     * @param tenant The tenant to update.
     */
    @Override
    public ITenant update(ITenant tenant) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Find all tenants. WARNING: This method will return all tenants, which could be a large amount of data. Use with caution.
     */
    @Override
    public List<ITenant> findAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Find a tenant by its ID.
     *
     * @param tenantId The ID of the tenant to find.
     */
    @Override
    public Optional<ITenant> findById(long tenantId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * WARNING: This method will return all reviews for the tenant, which could be a large amount of data. Use with caution.
     *
     * @param tenantId The ID of the tenant to find reviews for.
     */
    @Override
    public List<IReview> findReviewsByTenantId(long tenantId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * WARNING: This method will delete the tenant and all related reviews and reviewers. Use with caution.
     *
     * @param tenantId The ID of the tenant to delete.
     */
    @Override
    public void deleteById(long tenantId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * WARNING: This method will delete all tenants and all related reviews and reviewers. Use with caution.
     */
    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}