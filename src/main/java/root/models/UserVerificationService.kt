package root.models

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import root.models.interfaces.IUserRepository
import java.time.Instant
import java.util.Objects

class UserVerificationRecord: IUserVerificationRecord{
    override var id: Long? = 0
    override var userId: Long = 0
    override var verificationCode: String
    override var expiresAt: Instant

    constructor(userId: Long, verificationCode: String, expiresAt: Instant) {
        this.userId = userId
        this.verificationCode = verificationCode
        this.expiresAt = expiresAt
    }
}

interface IUserVerificationService {
    fun getExpirationDurationSeconds(): Long
    fun sendVerificationNotification(userId: Long): Unit
    fun checkVerificationCode(verificationRecord: UserVerificationRecord, code: String?): Boolean
    fun tryVerificationCode(verificationRecord: UserVerificationRecord, code: String?): Boolean
    fun setUserAsFullyVerified(userId: Long)
}

@Service
class UserVerificationService : IUserVerificationService {
    @Autowired
    private lateinit var IUserRepository: IUserRepository

    override fun getExpirationDurationSeconds(): Long {
        return 15 * 60L; // 15 minutes in seconds
    }

    override fun setUserAsFullyVerified(userId: Long) {
        var user = IUserRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        user.verifiedAt = Instant.now()
        IUserRepository.save(user)
    }

    // Implement notification logic (e.g., send email or SMS to user)
    override fun sendVerificationNotification(userId: Long) {
        val expirationDurationSeconds = getExpirationDurationSeconds();
        val expiresAt = Instant.now().plusSeconds(expirationDurationSeconds); // Set expiration time to 15 minutes from now
        val verificationCode = VerificationDigitsGenerator.generate(6);

        var verificationRecord = UserVerificationRecord(userId, verificationCode, expiresAt);
        //UserVerificationService.


        // TODO: Save the verification code and expiration time to the database, associated with the user
        TODO("Implement logic to save verification code and expiration time to the database")
    }

    // mark as verified if the code is correct and not expired
    override fun tryVerificationCode(verificationRecord: UserVerificationRecord, code: String?): Boolean {
        if (checkVerificationCode(verificationRecord, code)) {
            setUserAsFullyVerified(verificationRecord.userId);
            return true;
        }
        return false
    }

    override fun checkVerificationCode(verificationRecord: UserVerificationRecord, code: String?): Boolean {
        Objects.requireNonNull(verificationRecord.verificationCode, "VerificationCode can not be null");
        Objects.requireNonNull(verificationRecord.expiresAt, "ExpiresAt can not be null");

        // check expired code
        if (Instant.now().isAfter(verificationRecord.expiresAt)) {
            throw RuntimeException("Verification code has expired, get a fresh one");
        }

        // match & return result
        return verificationRecord.verificationCode == code;
    }

    fun isUserVerified(userId: Long): Boolean {
        // TODO: optimize so that we don't need to fetch the whole user entity just to check if it's verified
        return IUserRepository.findById(userId).isPresent();
    }
}