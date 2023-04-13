package tr.nttdata.poc.minicommerce.customer.email;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tr.nttdata.poc.minicommerce.customer.model.Customer;

import java.util.Date;
import java.util.Random;


@Service
public class EmailService implements IEmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Override
    public String send(EmailSubject subject, Customer customer) {

        try {
            String token = generateJwtToken(customer);
            String link = setLinkBySubject(subject, token);
            String body = setBodyBySubject(subject,customer.getFirstName(),link);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,"utf-8");
            mimeMessageHelper.setSubject(subject.toString());
            mimeMessageHelper.setText(body,true);
            mimeMessageHelper.setTo(customer.getEmail());
            mimeMessageHelper.setFrom("klcarf@gmail.com");

            mailSender.send(mimeMessage);
            return token;
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email.");
        }
    }

    private String generateJwtToken(Customer customer) {
        int jwtExpiration = 120;
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);
        return Jwts.builder()
                .setSubject(customer.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    private String setLinkBySubject(EmailSubject emailSubject, String token) {
        switch (emailSubject) {
            case ACTIVATION -> {
                return "http://localhost:8080/api/confirm?token=" + token;
            }
            case RESET_PASSWORD -> {
                return "http://localhost:8080/api/password-reset-request?token=" + token;
            }
            case TWO_FACTOR_AUTHENTICATION -> {
                return generateVerificationCode();
            }
            default -> {
                return "";
            }
        }
    }
    private String setBodyBySubject(EmailSubject emailSubject, String name, String body){
        switch (emailSubject) {
            case ACTIVATION -> {
                return sendRegistrationEmail(name,body);
            }
            case RESET_PASSWORD -> {
                return generatePasswordResetEmailHtml(name,body);
            }
            case TWO_FACTOR_AUTHENTICATION -> {
                return sendVerificationCodeEmail(name,body);
            }
            default -> {
                return "";
            }
        }
    }
    private String sendVerificationCodeEmail(String name, String verificationCode) {
        String username = name;
        String company = "Ntt ecommerce";
        String htmlBody = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>" +
                "Two-Factor Authentication - Verification Code</title></head><body><h1>" +
                "Two-Factor Authentication - Verification Code</h1><p>Hello " + name +
                ",</p><p>You are receiving this email because you have enabled Two-Factor " +
                "Authentication for your account.</p><p>Your verification code is: <strong>" +
                verificationCode + "</strong></p><p>Please enter the code in the 2FA " +
                "verification dialog box to complete the login process.</p><p>If you did " +
                "not request this verification code or you are not sure what this email " +
                "is about, please contact our support team immediately.</p><p>Regards,<br>" +
                "The " + company + " Team</p></body></html>";
        String subject = "Two-Factor Authentication - Verification Code";

        return htmlBody;
    }
    private String sendRegistrationEmail(String name, String link) {
        String registrationLink = link;

        String htmlBody = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Welcome to Our Website!</title></head><body><h1>Welcome to Our Website!</h1><p>Dear " +
                name + ",</p><p>Thank you for registering with us! Please click on the following link to activate your account and complete your registration:</p><p><a href=\"" +
                registrationLink + "\"> Activation Link </a></p><p>Best regards,<br>The Our Website Team</p></body></html>";

        String subject = "Welcome to Our Website!";

        return htmlBody;
        // Send the email using your preferred email sending library, e.g. JavaMail
    }
    private String generatePasswordResetEmailHtml(String customerName, String resetLink) {
        String companyName = "Ntt ecommerce";
        String htmlBody = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Password Reset Request</title></head><body><p>Dear " + customerName + "," +
                "</p><p>We have received a request to reset your password. To reset your password, please click on the following link:</p><p><a href=\"" + resetLink + "\">" +
                "Reset Password</a></p><p>If you did not request to reset your password, please ignore this email.</p><p>Best regards,<br>The " + companyName + " Team</p></body></html>";

        return htmlBody;
    }
    public static String generateVerificationCode() {
        Random random = new Random();
        int codeValue = 100000 + random.nextInt(900000); // generates a random integer between 100000 and 999999
        return Integer.toString(codeValue);
    }
}