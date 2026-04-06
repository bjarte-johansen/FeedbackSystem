package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.interfaces.IUserVerificationService;
import root.models.UserVerificationRecord;
import root.includes.VerificationDigitsGenerator;
import root.repositories.ReviewerRepository;

import java.time.Instant;
import java.util.Objects;

@Service
public class UserVerificationService implements IUserVerificationService {

    @Autowired
    private ReviewerRepository reviewerRepo;

    @Override
    public Long getExpirationDurationSeconds() {
        return 15 * 60L; // 15 minutes
    }

    @Override
    public void setUserAsFullyVerified(Long userId) throws Exception {
        var user = reviewerRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerifiedAt(Instant.now());
        reviewerRepo.save(user);
    }

    @Override
    public void sendVerificationNotification(Long userId) throws Exception{
        long expirationDurationSeconds = getExpirationDurationSeconds();
        Instant expiresAt = Instant.now().plusSeconds(expirationDurationSeconds);

        String verificationCode = VerificationDigitsGenerator.generate(6);

        UserVerificationRecord verificationRecord = new UserVerificationRecord(userId, verificationCode, expiresAt);

        // TODO: persist verificationRecord
        throw new UnsupportedOperationException("Implement logic to save verification code");
    }

    @Override
    public Boolean tryVerificationCode(UserVerificationRecord verificationRecord, String code) throws Exception{
        if (checkVerificationCode(verificationRecord, code)) {
            setUserAsFullyVerified(verificationRecord.getUserId());
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkVerificationCode(UserVerificationRecord verificationRecord, String code) throws Exception{
        Objects.requireNonNull(verificationRecord.getVerificationCode(), "VerificationCode can not be null");
        Objects.requireNonNull(verificationRecord.getExpiresAt(), "ExpiresAt can not be null");

        if (Instant.now().isAfter(verificationRecord.getExpiresAt())) {
            throw new RuntimeException("Verification code has expired, get a fresh one");
        }

        return verificationRecord.getVerificationCode().equals(code);
    }

    public Boolean isUserVerified(Long userId) throws Exception{
        return reviewerRepo.findById(userId).isPresent();
    }
}