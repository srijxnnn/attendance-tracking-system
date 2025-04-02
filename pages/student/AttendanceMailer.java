package pages.student;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;

public class AttendanceMailer {
    private static final String senderEmail = "anujkaushal1068@gmail.com";
    private static final String senderPassword = "mbsf pdzl niiu gsjc";

    public static boolean sendAttendanceReport(String recipientEmail, String studentName, String pdfFilePath) {
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
            message.setFrom(new InternetAddress(senderEmail, "Attend Assist Reports"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Attendance Report - " + studentName);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(
                "<html><body>"
                + "<h3>Dear " + studentName + ",</h3>"
                + "<p>Attached is your attendance report for this semester.</p>"
                + "<p>Keep track of your attendance and maintain consistency.</p>"
                + "<p style='font-size: 12px; color: gray;'>This is an automated email. Please do not reply.</p>"
                + "</body></html>",
                "text/html"
            );

            MimeBodyPart attachmentPart = new MimeBodyPart();
            File file = new File(pdfFilePath);
            if (!file.exists()) {
                System.out.println("PDF file not found: " + pdfFilePath);
                return false;
            }
            attachmentPart.attachFile(file);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Attendance report sent to " + recipientEmail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send attendance report.");
            return false;
        }
    }
}
