package pages.auth;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class OTPService {
    private static final String senderEmail = "anujkaushal1068@gmail.com";
    private static final String senderPassword = "mbsf pdzl niiu gsjc";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String recipientEmail = "cse23058@iiitkalyani.ac.in";

        String generatedOTP = generateOTP();
        System.out.println("Generated OTP: " + generatedOTP);

        boolean emailSent = sendOTP(recipientEmail, generatedOTP);
        if (!emailSent) {
            System.out.println("Failed to send OTP. Exiting.");
        }

        System.out.print("Enter the OTP sent to your email: ");
        String enteredOTP = scanner.nextLine();

        if (generatedOTP.equals(enteredOTP)) {
            System.out.println("OTP Verified Successfully! User Authenticated.");
        } else {
            System.out.println("Invalid OTP! Verification Failed.");
        }

        scanner.close();
    }

    public static String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public static boolean sendOTP(String recipientEmail, String otp) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "Attend Assist OTP Service"));
            message.setReplyTo(InternetAddress.parse("comnoreply@yourdomain"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your One-Time Password (OTP)");
            message.setContent(
                "<html><body>"
                + "<h3 style='color: #333;'>Your OTP Code</h3>"
                + "<p>Your One-Time Password (OTP) is: <b>" + otp + "</b></p>"
                + "<p>This OTP is valid for 5 minutes.</p>"
                + "<p style='font-size: 12px; color: gray;'>If you did not request this, please ignore this email.</p>"
                + "<p><a href='https://yourwebsite.com/unsubscribe' style='color: blue;'>Unsubscribe</a></p>"
                + "</body></html>",
                "text/html"
            );

            Transport.send(message);
            System.out.println("OTP sent to " + recipientEmail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send OTP.");
            return false;
        }
    }
}
