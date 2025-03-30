package pages.auth;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Scanner;

public class OTPService {
    private static final String senderEmail = "anujkaushal1068@gmail.com";  // Change this
    private static final String senderPassword = "mbsf pdzl niiu gsjc"; // Change this (Use App Password)

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String recipientEmail = "cse23058@iiitkalyani.ac.in"; // Change this to the receiver's email

        // Generate a 6-digit OTP
        String generatedOTP = generateOTP();
        System.out.println("Generated OTP: " + generatedOTP); // For debugging

        // Send OTP to user's email
        boolean emailSent = sendOTP(recipientEmail, generatedOTP);
        if (!emailSent) {
            System.out.println("Failed to send OTP. Exiting.");
        }

        // Ask user to enter OTP
        System.out.print("Enter the OTP sent to your email: ");
        String enteredOTP = scanner.nextLine();

        // Verify OTP
        if (generatedOTP.equals(enteredOTP)) {
            System.out.println("OTP Verified Successfully! User Authenticated.");
        } else {
            System.out.println("Invalid OTP! Verification Failed.");
        }

        scanner.close();
    }

    // Function to generate a 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Ensures a 6-digit number
        return String.valueOf(otp);
    }

    // Function to send OTP via email
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
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your OTP Code");
            message.setText("Your One-Time Password (OTP) is: " + otp + "\n\nThis OTP is valid for 5 minutes.");

            Transport.send(message);
            System.out.println("OTP sent to " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send OTP.");
            return false;
        }
    }
}
