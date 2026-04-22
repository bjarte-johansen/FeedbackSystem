package root.includes;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import root.app.AppConfig;

import java.util.Properties;


public class EmailVerificationCodeSender {

    public static void send(String email, String host, String verificationCode) {
        Properties p = new Properties();

        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.port", "587");

        Session s = Session.getInstance(p, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    "dat109prosjekt@gmail.com",
                    "fwhf izag otim phro" // 16-char app password
                );
            }
        });

        String fixedStr = ", Verifiseringskode for omtale på FeedbackSystem DAT109";

        try {
            Message m = new MimeMessage(s);
            m.setFrom(new InternetAddress("dat109prosjekt@gmail.com"));
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            m.setSubject(verificationCode + fixedStr);
            m.setText(verificationCode + fixedStr);

            String html = AppConfig.VERIFICATION_CODE_EMAIL_FORMAT.formatted(verificationCode, host);

            m.setContent(html, "text/html; charset=UTF-8");

            Transport.send(m);
        }catch (MessagingException me){
            me.printStackTrace();
            throw new RuntimeException("Failed to send verification code email: " + me.getMessage());
        }
    }
}
