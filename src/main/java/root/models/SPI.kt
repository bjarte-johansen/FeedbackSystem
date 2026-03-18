package root.models

import java.time.Instant
import java.util.Optional


/***********************************************/
/* Reviewer Management SPI                     */
/***********************************************/

interface IReviewer {
    var id: Long
    var tenantId: Long
    var displayName: String
    var email: String
    var passwordHash: String
    var passwordSalt: String
    var verifiedAt: Instant?
    var createdAt: Instant?
}

interface IReviewerRepository {
    @Throws(Exception::class)
    fun create(reviewer: IReviewer): IReviewer

    @Throws(Exception::class)
    fun update(reviewer: IReviewer): IReviewer

    @Throws(Exception::class)
    fun findById(id: Long): Optional<IReviewer>

    @Throws(Exception::class)
    fun findByReviewerName(tenantId: Long, username: String): Optional<IReviewer>

    @Throws(Exception::class)
    fun findByEmail(tenantId: Long, email: String): Optional<IReviewer>

    @Throws(Exception::class)
    fun delete(reviewer: IReviewer): Int

    @Throws(Exception::class)
    fun deleteById(tenantId: Long, id: Long): Int
}



/***********************************************/
/* Verification Management SPI                 */
/***********************************************/
/*
interface IVerificationCodeGenerator {
    fun generate(digits: Int): String
}

interface IVerificationService {
    @Throws(Exception::class)
    fun sendVerificationNotification(user: IReviewer)

    @Throws(Exception::class)
    fun checkVerificationCode(user: IReviewer, code: String): Boolean
}
*/


/***********************************************/
/* Tenant Management SPI                       */
/***********************************************/

interface ITenant{
    var id: Long?;
    var name: String;
    var domain: String;
    var api_key: String;
    var email: String;
    var password_hash: String;
    var password_salt: String;
}


/***********************************************/
/* Review Management SPI                       */
/***********************************************/

interface IReview {
    var id: Long?
    var tenantId: Long?
    var externalId: String
    var externalIdHash: Long
    var authorId: Long
    var authorName: String
    var score: Int
    var comment: String
    var createdAt: Instant
    //var attributes: Map<String, Any>
}
