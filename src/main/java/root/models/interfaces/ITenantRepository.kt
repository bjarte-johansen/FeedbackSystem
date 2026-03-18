package root.models.interfaces

import root.models.IReview
import root.models.ITenant
import java.util.Optional

interface ITenantRepository {

    /**
     * Save a tenant. If the tenant has an ID, it will be updated. If the tenant does not have an ID, it will be created.
     *
     * @param tenant The tenant to save. If the tenant has an ID, it will be updated. If the tenant does not have an ID, it will be created.
     */

    @Throws(Exception::class)
    fun save(tenant: ITenant): ITenant {
        if(tenant.id == null){
            return create(tenant)
        }else{
            return update(tenant)
        }
    }

    /**
     * Create a new tenant. The tenant must not already exist.
     *
     * @param tenant The tenant to create.
     */

    @Throws(Exception::class)
    fun create(tenant: ITenant): ITenant


    /**
     * Update a tenant. The tenant must already exist.
     *
     * @param tenant The tenant to update.
     */

    @kotlin.jvm.Throws
    fun update(tenant: ITenant): ITenant


    /**
     * find all tenants. WARNING: This method will return all tenants, which could be a large amount of data. Use with caution.
     */

    @Throws(Exception::class)
    fun findAll(): List<ITenant>

    /**
     * Find a tenant by its ID.
     *
     * @param tenantId The ID of the tenant to find.
     */
    @Throws(Exception::class)
    fun findById(tenantId: Long): Optional<ITenant>


    /**
     * WARNING: This method will return all reviews for the tenant, which could be a large amount of data. Use with caution.
     *
     * @param tenantId The ID of the tenant to find reviews for.
     */
    @Throws(Exception::class)
    fun findReviewsByTenantId(tenantId: Long): List<IReview>


    /**
     * WARNING: This method will delete the tenant and all related reviews and reviewers. Use with caution.
     *
     * @param tenantId The ID of the tenant to delete.
     */

    @Throws(Exception::class)
    fun deleteById(tenantId: Long)


    /**
     * WARNING: This method will delete all tenants and all related reviews and reviewers. Use with caution.
     */

    @Throws(Exception::class)
    fun deleteAll()
}