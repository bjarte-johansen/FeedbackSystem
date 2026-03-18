package root.models

import root.database.DB
import root.database.FSQLQuery
import java.util.Optional

/*
class UserVerificationRecordImpl: IUserVerificationRecord{
    override var id: Long? = 0;
    override var userId: Long = 0;
    override var verificationCode: String = "";
    override var expiresAt: Instant = Instant.now();
}
*/

interface IReviewerVerficiationRepository{
    @Throws(Exception::class)
    fun save(record: IUserVerificationRecord): IUserVerificationRecord;

    @Throws(Exception::class)
    fun findByUserId(userId: Long): Optional<IUserVerificationRecord>
}

public class UserVerifcationRepository: IReviewerVerficiationRepository {
    override fun save(record: IUserVerificationRecord): IUserVerificationRecord {
        if(record.id != null) {
            // Update existing record
            FSQLQuery.create(DB.getConnection(), "UPDATE user_verification_records SET user_id = ?, verification_code = ?, expires_at = ? WHERE id = ?")
                .bind(record.userId)
                .bind(record.verificationCode)
                .bind(record.expiresAt)
                .bind(record.id)
                .update();
        } else {
            // Insert new record
            val id = FSQLQuery.create(
                DB.getConnection(),
                "INSERT INTO user_verification_records (user_id, verification_code, expires_at) VALUES (?, ?, ?)"
                )
                .bind(record.userId)
                .bind(record.verificationCode)
                .bind(record.expiresAt)
                .insertAndGetId();
            record.id = id;
        }
        return record;
    }

    override fun findByUserId(userId: Long): Optional<IUserVerificationRecord> {
        return FSQLQuery.create(DB.getConnection(), "SELECT id, user_id, verification_code, expires_at FROM user_verification_records WHERE user_id = ?")
            .bind(userId)
            .fetchOne(UserVerificationRecord::class.java).map { it as IUserVerificationRecord};
    }
}