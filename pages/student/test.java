package pages.student;

public class test {
    public static void main(String[] args) {
        String recipientEmail = "cse23058@iiitkalyani.ac.in";
        String studentName = "John Doe";
        String pdfFilePath = "Final_Attendance_Report_With_Chart.pdf"; 

        boolean sent = AttendanceMailer.sendAttendanceReport(recipientEmail, studentName, pdfFilePath);
        if (sent) {
            System.out.println("Report emailed successfully!");
        } else {
            System.out.println("Failed to email the report.");
        }
    }
}
