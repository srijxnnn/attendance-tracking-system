package pages.faculty;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class AttendanceWarningMailer {

    private static final String senderEmail = "anujkaushal1068@gmail.com";
    private static final String senderPassword = "mbsf pdzl niiu gsjc";

    public static void sendWarnings(int facultyId) {
        List<Map<String, String>> students = AttendanceFetcher.getLowAttendanceStudents(facultyId);

        System.out.println("=== DEBUG: Students Retrieved ===");
        for (Map<String, String> student : students) {
            System.out.println("Student ID: " + student.get("student_id")
                    + ", Name: " + student.get("student_name")
                    + ", Email: " + student.get("student_email")
                    + ", Course: " + student.get("course_name")
                    + ", Attendance: " + student.get("attendance_percentage") + "%");
        }
        System.out.println("=================================");

        for (Map<String, String> student : students) {
            String email = student.get("student_email"); 
            String name = student.get("student_name");
            String course = student.get("course_name");
            String percentage = student.get("attendance_percentage");
            String faculty_name = student.get("faculty_name");
            String roll = student.get("roll");

            if (email == null || email.isEmpty()) {
                System.out.println("Skipping student due to missing email: " + name);
                continue;
            }

            boolean status = sendEmail(email, name, course, percentage, roll, faculty_name);
            if (status) {
                System.out.println("Warning email sent to: " + name + " (" + email + ")");
            } else {
                System.out.println("Failed to send warning email to: " + name);
            }
        }
    }

    private static boolean sendEmail(String recipientEmail, String studentName, String courseName, String attendancePercentage, String roll, String faculty_name) {
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
            message.setFrom(new InternetAddress(senderEmail, "Attendance Management System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("⚠ Low Attendance Warning - " + studentName);

            // Email Body
            String emailContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }"
                    + ".container { max-width: 600px; background: white; padding: 20px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1); }"
                    + "h2 { color: #333; }"
                    + ".highlight { color: red; font-weight: bold; }"
                    + ".footer { margin-top: 20px; font-size: 14px; color: #666; text-align: center; }"
                    + ".faculty { font-weight: bold; color: #0056b3; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<h2>Attendance Warning Notification</h2>"
                    + "<p>Dear <strong>" + studentName + "</strong> (Roll No: <strong>" + roll + "</strong>),</p>"
                    + "<p>We have observed that your attendance in the course <strong>" + courseName + "</strong> has fallen below the required threshold.</p>"
                    + "<p><strong>Your current attendance percentage: <span class='highlight'>" + attendancePercentage + "%</span></strong></p>"
                    + "<p>It is crucial that you improve your attendance to avoid academic penalties.</p>"
                    + "<p>If you believe this is an error, please reach out to your faculty advisor:</p>"
                    + "<p>Faculty Name: <span class='faculty'>" + faculty_name + "</span></p>"
                    + "<p>Ensure that you attend upcoming classes regularly.</p>"
                    + "<br>"
                    + "<p>Best Regards,</p>"
                    + "<p><strong>Attendance Management Team</strong></p>"
                    + "<div class='footer'>This is an automated email, please do not reply.</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

                    message.setContent(emailContent, "text/html");

            // Send the Email
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        

    

    public static void main(String[] args) {
        int facultyId = 1; // Change this as needed for testing

        System.out.println("Fetching students with low attendance...");
        AttendanceWarningMailer.sendWarnings(facultyId);

        System.out.println("Email sending process completed.");
    }
}
}
