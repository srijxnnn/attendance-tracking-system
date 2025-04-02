package pages.student;

import java.util.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class AttendanceReport {

    public static void getReport(int student_id, String filepath) {

        Map<String, String> student_details = GetReportInformation.getStudentDetails(student_id);
        Map<String, Integer> attendence_summary = GetReportInformation.getAttendanceSummary(student_id);
        List<Map<String, String>> sub_attendence = GetReportInformation.getSubjectAttendance(student_id);

        System.out.println("Student Details:");
        for (Map.Entry<String, String> entry : student_details.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nAttendance Summary:");
        for (Map.Entry<String, Integer> entry : attendence_summary.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nSubject-wise Attendance:");
        for (Map<String, String> subject : sub_attendence) {
            System.out.println("----- Subject -----");
            for (Map.Entry<String, String> entry : subject.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        try {
            PdfWriter writer = new PdfWriter(filepath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            String logoPath = "College.png";
            ImageData logoData = ImageDataFactory.create(logoPath);
            Image logo = new Image(logoData).setWidth(75).setHeight(75);
            logo.setFixedPosition(30, 725);

            document.add(logo);

            document.add(new Paragraph("Indian Institue Of Information\nTechnology, Kalyani")
                    .setFontSize(20).setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Department of Computer Science & Engineering")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Academic Year: 2024-2025")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("STUDENT ATTENDANCE REPORT")
                    .setFontSize(18).setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            String formattedDate = currentDate.format(formatter);

            String rollNumber = student_details.get("roll");
            String yearPrefix = rollNumber.substring(4, 6);

            String studentYear;
            switch (yearPrefix) {
                case "25":
                    studentYear = "1st Year";
                    break;
                case "24":
                    studentYear = "2nd Year";
                    break;
                case "23":
                    studentYear = "2nd Year";
                    break;
                case "22":
                    studentYear = "3rd Year";
                    break;
                case "21":
                    studentYear = "4th Year";
                    break;
                default:
                    studentYear = "Unknown Year";
            }

            Table studentInfoTable = new Table(new float[]{3, 7});
            studentInfoTable.setWidth(UnitValue.createPercentValue(100));
            studentInfoTable.addCell(getStyledCell("Student Name:"));
            studentInfoTable.addCell(student_details.get("name"));
            studentInfoTable.addCell(getStyledCell("Student ID:"));
            studentInfoTable.addCell(student_details.get("roll"));
            studentInfoTable.addCell(getStyledCell("Class:"));
            studentInfoTable.addCell("Computer Science - " + studentYear);
            studentInfoTable.addCell(getStyledCell("Report Date:"));
            studentInfoTable.addCell(formattedDate);

            document.add(studentInfoTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("ATTENDANCE SUMMARY")
                    .setBold().setFontSize(14)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            Table summaryTable = new Table(new float[]{3, 3});
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            summaryTable.addCell(getStyledCell("Total Classes:"));
            summaryTable.addCell(String.valueOf(attendence_summary.get("total_classes")));
            summaryTable.addCell(getStyledCell("Classes Attended:"));
            summaryTable.addCell(String.valueOf(attendence_summary.get("attended")));
            summaryTable.addCell(getStyledCell("Attendance Percentage:"));
            summaryTable.addCell(String.valueOf(attendence_summary.get("percentage") + "%"));
            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("SUBJECT-WISE ATTENDANCE")
                    .setBold().setFontSize(14)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            Table subjectTable = new Table(new float[]{4, 3, 3, 3});
            subjectTable.setWidth(UnitValue.createPercentValue(100));
            subjectTable.addCell(getTableHeaderCell("Subject"));
            subjectTable.addCell(getTableHeaderCell("Total Classes"));
            subjectTable.addCell(getTableHeaderCell("Attended"));
            subjectTable.addCell(getTableHeaderCell("Attendance %"));

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (Map<String, String> subjectData : sub_attendence) {
                String subject = subjectData.get("subject");
                String totalClasses = subjectData.get("total_classes");
                String attended = subjectData.get("attended");
                String percentage = subjectData.get("percentage");

                subjectTable.addCell(getStyledCell(subject));
                subjectTable.addCell(getStyledCell(totalClasses));
                subjectTable.addCell(getStyledCell(attended));
                subjectTable.addCell(getStyledCell(percentage));

                dataset.addValue(Double.parseDouble(percentage.replace("%", "")), "Attendance", subject);
            }

            document.add(subjectTable);
            document.add(new Paragraph("\n"));

            JFreeChart barChart = createBarChart(dataset);
            BufferedImage bufferedImage = new BufferedImage(1200, 600, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            barChart.draw(g2, new Rectangle2D.Double(0, 0, 1200, 600));
            g2.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            ImageData imageData = ImageDataFactory.create(baos.toByteArray());

            Image chartImage = new Image(imageData);
            chartImage.setWidth(UnitValue.createPercentValue(100));
            chartImage.setAutoScaleHeight(true);

            document.add(chartImage);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("REMARKS & OBSERVATIONS")
                    .setBold().setFontSize(14)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            double attendancee = attendence_summary.get("percentage");
            String performanceRemark;
            String detailedComment;

            if (attendancee >= 90) {
                performanceRemark = "Excellent";
                detailedComment = "The student has maintained outstanding attendance, showing dedication and commitment to academics. "
                        + "Keep up the great work!";
            } else if (attendancee >= 80) {
                performanceRemark = "Very Good";
                detailedComment = "The student has a strong attendance record with minor gaps. Consistency will ensure continued success.";
            } else if (attendancee >= 70) {
                performanceRemark = "Good";
                detailedComment = "The attendance is decent, but improvements can be made. Regular participation is recommended.";
            } else if (attendancee >= 60) {
                performanceRemark = "Needs Improvement";
                detailedComment = "The student has missed a considerable number of classes. It is advised to attend more sessions for better understanding.";
            } else {
                performanceRemark = "Poor";
                detailedComment = "Low attendance is a major concern. Immediate action is required to avoid academic consequences.";
            }

            document.add(new Paragraph("Overall Attendance Performance: " + performanceRemark)
                    .setFontSize(12)
                    .setBold());
            document.add(new Paragraph("Comments: " + detailedComment)
                    .setFontSize(12));

            document.add(new Paragraph("\n"));

            double attendancePercentage = attendancee;
            String attendanceCategory;
            String motivationMessage;
            String futureAdvice;

            if (attendancePercentage >= 95) {
                attendanceCategory = "🚀 Star Performer!";
                motivationMessage = "You're like a rocket on a steady path to success, never missing a moment of learning.";
                futureAdvice = "Maintain this incredible consistency, and your academic journey will be nothing short of extraordinary!";
            } else if (attendancePercentage >= 90) {
                attendanceCategory = "🏆 Consistent Achiever!";
                motivationMessage = "You're in the top league! Your presence in class is making a real difference.";
                futureAdvice = "Stay on this path, and you'll continue to shine bright in your academic career.";
            } else if (attendancePercentage >= 80) {
                attendanceCategory = "📈 Steady Climber!";
                motivationMessage = "You're making good progress, balancing academics well.";
                futureAdvice = "A little more effort in attending every class could make a significant impact.";
            } else if (attendancePercentage >= 70) {
                attendanceCategory = "⏳ The Busy Explorer!";
                motivationMessage = "You attend fairly well but sometimes miss out on valuable lessons.";
                futureAdvice = "Try to make small adjustments to be present more often—every class counts!";
            } else if (attendancePercentage >= 60) {
                attendanceCategory = "⚠️ The Risk Taker!";
                motivationMessage = "You're walking a fine line. Missing classes frequently might slow you down.";
                futureAdvice = "Consider setting reminders or schedules to help improve attendance and stay ahead.";
            } else {
                attendanceCategory = "🚦 At a Crossroad!";
                motivationMessage = "Your attendance is critically low, which might affect your academic progress.";
                futureAdvice = "Make attending classes a priority—your future self will thank you for it!";
            }

            document.add(new Paragraph("ATTENDANCE REMARKS")
                    .setBold().setFontSize(14)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new Paragraph("Performance Level: " + attendanceCategory)
                    .setFontSize(12)
                    .setBold());

            document.add(new Paragraph("Reflection: " + motivationMessage)
                    .setFontSize(12));

            document.add(new Paragraph("Advice for Improvement: " + futureAdvice)
                    .setFontSize(12));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("IMPORTANT NOTICE")
                    .setBold().setFontSize(14)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new Paragraph(
                    "Mandatory Attendance Policy!\n"
                    + "All students are required to maintain a minimum attendance percentage as per institute regulations. "
                    + "Failure to meet this requirement may result in serious academic consequences, including:\n")
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(5));

            document.add(new Paragraph("• Disqualification from semester examinations\n"
                    + "• Deduction of internal assessment marks\n"
                    + "• Academic probation or disciplinary actions")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(20)
                    .setMarginBottom(10));

            document.add(new Paragraph(
                    "Student Responsibility\n"
                    + "Consistent class participation is crucial for academic success. Students are advised to prioritize attendance "
                    + "and communicate any valid concerns with faculty members. Attendance records are strictly monitored, and non-compliance "
                    + "may impact overall academic performance.")
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(5));

            document.add(new Paragraph(
                    "For further clarification, refer to the official **University Handbook** or consult the **Department Administration**.")
                    .setFontSize(12)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE));

            document.add(new Paragraph("\n"));

            String signaturePath = "signature.jpg";
            ImageData signatureData = ImageDataFactory.create(signaturePath);
            Image signatureImage = new Image(signatureData).setWidth(100).setHeight(40);

            document.add(signatureImage.setFixedPosition(75, 640));

            document.add(new Paragraph("__________________________")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginTop(10));

            document.add(new Paragraph("Head of Department")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Indian Institute of Information Technology, Kalyani")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));

            document.close();
            System.out.println("Final Attendance Report with Chart Created Successfully!");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Cell getStyledCell(String text) {
        return new Cell().add(new Paragraph(text)).setPadding(5);
    }

    private static Cell getTableHeaderCell(String text) {
        return new Cell().add(new Paragraph(text))
                .setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5).setTextAlignment(TextAlignment.CENTER);
    }

    private static JFreeChart createBarChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Subject-wise Attendance",
                "Subjects",
                "Attendance (%)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        chart.setBackgroundPaint(Color.white);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(79, 129, 189));
        ((BarRenderer) chart.getCategoryPlot().getRenderer()).setBarPainter(new StandardBarPainter());
        return chart;
    }

    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
