package root.models

import java.time.Instant

interface IUserVerificationRecord {
    var id: Long?;
    var userId: Long;
    var verificationCode: String;
    var expiresAt: Instant;
}