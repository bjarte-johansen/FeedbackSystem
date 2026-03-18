package root.models.interfaces

import root.models.IReviewer
import java.util.Optional

/**
 * TODO: write javadoc for IUserRepository
 */

@Deprecated("Use IReviewerRepository instead. This interface is deprecated and will be removed in a future release.")
public interface IUserRepository{
    @Throws(Exception::class) fun save(reviewer: IReviewer): IReviewer
    @Throws(Exception::class) fun create(reviewer: IReviewer): IReviewer
    @Throws(Exception::class) fun update(reviewer: IReviewer): IReviewer
    @Throws(Exception::class) fun delete(reviewer: IReviewer)
    @Throws(Exception::class) fun deleteById(reviewerId: Long?)
    @Throws(Exception::class) fun findById(reviewerId: Long?): Optional<IReviewer>
    @Throws(Exception::class) fun findByEmail(email: String): Optional<IReviewer>
    @Throws(Exception::class) fun findByDisplayName(displayName: String): Optional<IReviewer>
    @Throws(Exception::class) fun existsById(reviewerId: Long?): Boolean
}