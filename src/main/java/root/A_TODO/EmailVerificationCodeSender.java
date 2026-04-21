package root.A_TODO;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.*;
//import java.util.Properties;

@Deprecated
public class EmailVerificationCodeSender {
//    public static void send(String email, String verificationCode) throws Exception {
//        Properties p = new Properties();
//
//        p.put("mail.smtp.auth", "true");
//        p.put("mail.smtp.starttls.enable", "true");
//        p.put("mail.smtp.host", "smtp.office365.com");
//        p.put("mail.smtp.port", "587");
//
//        Session s = Session.getInstance(p, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("184905@stud.hvl.no", "");
//            }
//        });
//
//        String fixedStr = ", Verifiseringskode for omtale på FeedbackSystem DAT109";
//
//        Message m = new MimeMessage(s);
//        m.setFrom(new InternetAddress("prosjektdathoyskolenpavestland@gmail.com"));
//        m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
//        m.setSubject(verificationCode + fixedStr);
//        m.setText(verificationCode + fixedStr);
//
//        Transport.send(m);
//    }
}