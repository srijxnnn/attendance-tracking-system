package pages.student;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class GetReportInformation {

    // Fetch student details
    public static Map<String, String> getStudentDetails(int studentId) {
        Map<String, String> studentInfo = new HashMap<>();
        String query = "SELECT s.name, s.reg_no, s.roll, u.email FROM students s JOIN users u ON s.user_id = u.id WHERE s.id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                studentInfo.put("name", rs.getString("name"));
                studentInfo.put("reg_no", rs.getString("reg_no"));
                studentInfo.put("roll", rs.getString("roll"));
                studentInfo.put("email", rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentInfo;
    }

    // Fetch overall attendance summary
    public static Map<String, Integer> getAttendanceSummary(int studentId) {
        Map<String, Integer> summary = new HashMap<>();
        String query = "SELECT COUNT(*) AS total_classes, SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) AS attended FROM attendances WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int totalClasses = rs.getInt("total_classes");
                int attended = rs.getInt("attended");
                summary.put("total_classes", totalClasses);
                summary.put("attended", attended);
                summary.put("percentage", totalClasses == 0 ? 0 : (attended * 100 / totalClasses));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    // Fetch subject-wise attendance
    public static List<Map<String, String>> getSubjectAttendance(int studentId) {
        List<Map<String, String>> subjects = new ArrayList<>();
        String query = "SELECT c.name AS subject, COUNT(a.id) AS total_classes, " +
                       "SUM(CASE WHEN a.status = 'present' THEN 1 ELSE 0 END) AS attended " +
                       "FROM attendances a JOIN courses c ON a.course_id = c.id " +
                       "WHERE a.student_id = ? GROUP BY c.name";
        
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> subjectData = new HashMap<>();
                int total = rs.getInt("total_classes");
                int attended = rs.getInt("attended");
                double percentage = total == 0 ? 0 : (attended * 100.0 / total);
                
                subjectData.put("subject", rs.getString("subject"));
                subjectData.put("total_classes", String.valueOf(total));
                subjectData.put("attended", String.valueOf(attended));
                subjectData.put("percentage", String.format("%.2f%%", percentage));
                subjects.add(subjectData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    // Fetch attendance trend (for line graph)
    public static List<Map<String, Object>> getAttendanceTrend(int studentId) {
        List<Map<String, Object>> trend = new ArrayList<>();
        String query = "SELECT date, " +
                       "(SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) * 100 / COUNT(*)) AS attendance_percentage " +
                       "FROM attendances WHERE student_id = ? GROUP BY date ORDER BY date";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("date", rs.getDate("date"));
                data.put("percentage", rs.getDouble("attendance_percentage"));
                trend.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trend;
    }

    // Fetch faculty details (if needed)
    public static Map<String, String> getFacultyDetails(int facultyId) {
        Map<String, String> facultyInfo = new HashMap<>();
        String query = "SELECT name, expertise, designation FROM faculty WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                facultyInfo.put("name", rs.getString("name"));
                facultyInfo.put("expertise", rs.getString("expertise"));
                facultyInfo.put("designation", rs.getString("designation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facultyInfo;
    }

    public static void main(String[] args) {
        // Example Usage
        int studentId = 1; // Replace with an actual student ID

        System.out.println("Student Details:");
        System.out.println(getStudentDetails(studentId));

        System.out.println("Attendance Summary:");
        System.out.println(getAttendanceSummary(studentId));

        System.out.println("Subject-Wise Attendance:");
        System.out.println(getSubjectAttendance(studentId));

        System.out.println("Attendance Trend:");
        System.out.println(getAttendanceTrend(studentId));
    }
}
