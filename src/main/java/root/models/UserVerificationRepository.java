package root.models;

import root.database.DB;
import root.database.FSQLQuery;
import root.interfaces.IReviewerVerificationRepository;
import root.interfaces.IUserVerificationRecord;

import java.sql.Connection;
import java.util.Optional;

public class UserVerificationRepository implements IReviewerVerificationRepository {

    @Override
    public IUserVerificationRecord save(IUserVerificationRecord record) throws Exception {
        return DB.with(conn -> {
            if (record.getId() != null) {
                // UPDATE
                String sql = "UPDATE user_verification_records SET user_id = ?, verification_code = ?, expires_at = ? WHERE id = ?";
                FSQLQuery.create(conn, sql)
                    .bind(record.getUserId())
                    .bind(record.getVerificationCode())
                    .bind(record.getExpiresAt())
                    .bind(record.getId())
                    .update();
            } else {
                // INSERT
                String sql = "INSERT INTO user_verification_records (user_id, verification_code, expires_at) VALUES (?, ?, ?)";
                Long id = FSQLQuery.create(conn, sql)
                    .bind(record.getUserId())
                    .bind(record.getVerificationCode())
                    .bind(record.getExpiresAt())
                    .insertAndGetId();

                record.setId(id);
            }

            return record;
        });
    }

    @Override
    public Optional<IUserVerificationRecord> findByUserId(Long userId) throws Exception {
        return DB.with(conn -> {
            return FSQLQuery.create(
                    conn,
                    "SELECT id, user_id, verification_code, expires_at FROM user_verification_records WHERE user_id = ?")
                .bind(userId)
                .fetchOne(UserVerificationRecord.class)
                .map(r -> (IUserVerificationRecord) r);
        });
    }
}