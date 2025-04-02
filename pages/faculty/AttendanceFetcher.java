package pages.faculty;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class AttendanceFetcher {

    public static List<Map<String, String>> getLowAttendanceStudents(int facultyId) {
        List<Map<String, String>> students = new ArrayList<>();

        String query = """
            SELECT 
                s.id AS student_id, 
                s.name AS student_name,
                s.roll AS roll,
                u.email AS student_email, 
                c.id AS course_id, 
                c.name AS course_name, 
                f.id AS faculty_id, 
                f.name AS faculty_name, 
                (SUM(CASE WHEN a.status = 'present' THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id)) AS attendance_percentage
            FROM students s
            JOIN users u ON s.user_id = u.id
            JOIN student_courses sc ON s.id = sc.student_id
            JOIN courses c ON sc.course_id = c.id
            JOIN faculty_courses fc ON c.id = fc.course_id
            JOIN faculty f ON fc.faculty_id = f.id
            LEFT JOIN attendances a ON s.id = a.student_id AND c.id = a.course_id
            WHERE f.id = ?
            GROUP BY s.id, s.name, u.email, c.id, c.name, f.id, f.name
            HAVING attendance_percentage < 75;
        """;

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, facultyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> studentData = new HashMap<>();
                studentData.put("student_id", String.valueOf(rs.getInt("student_id")));
                studentData.put("student_name", rs.getString("student_name"));
                studentData.put("roll", rs.getString("roll"));
                studentData.put("student_email", rs.getString("student_email"));
                studentData.put("course_id", String.valueOf(rs.getInt("course_id")));
                studentData.put("course_name", rs.getString("course_name"));
                studentData.put("faculty_id", String.valueOf(rs.getInt("faculty_id")));
                studentData.put("faculty_name", rs.getString("faculty_name"));
                studentData.put("attendance_percentage", String.valueOf(rs.getDouble("attendance_percentage")));


                students.add(studentData);

                System.out.println("Student ID: " + studentData.get("student_id") +
                    ", Name: " + studentData.get("student_name") +
                    ", Email: " + studentData.get("student_email") +
                    ", Course: " + studentData.get("course_name") +
                    ", Attendance: " + studentData.get("attendance_percentage") + "%");
            }
            
            if (students.isEmpty()) {
                System.out.println("No students found with low attendance.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return students;
    }

    public static void main(String[] args) {
        int facultyId = 1;
        List<Map<String, String>> lowAttendanceStudents = getLowAttendanceStudents(facultyId);

        for (Map<String, String> student : lowAttendanceStudents) {
            System.out.println(student);
        }
    }
}
