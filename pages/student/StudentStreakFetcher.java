package pages.student;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StudentStreakFetcher {
    public static Map<String, Integer> getStudentStreaks(int studentId) {
        Map<String, Integer> streaks = new HashMap<>();

        String query = "SELECT a.course_id, a.status FROM attendances a " +
                       "WHERE a.student_id = ? ORDER BY a.course_id, a.date";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            Map<Integer, Integer> currentStreaks = new HashMap<>();

            // First, get all the course ids and calculate the streaks.
            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String status = rs.getString("status");

                if (!currentStreaks.containsKey(courseId)) {
                    currentStreaks.put(courseId, 0);
                }

                if (status.equals("present")) {
                    currentStreaks.put(courseId, currentStreaks.get(courseId) + 1);
                } else {
                    currentStreaks.put(courseId, 0);  // Reset streak on "absent"
                }
            }

            // Now, we will map course_id to course_name
            for (Map.Entry<Integer, Integer> entry : currentStreaks.entrySet()) {
                int courseId = entry.getKey();
                int streak = entry.getValue();
                
                // Get the course name using the course_id
                String courseName = getCourseName(courseId);
                
                // Put the course name and streak into the final map
                streaks.put(courseName, streak);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return streaks;
    }

    // Method to fetch course name from the courses table using course_id
    private static String getCourseName(int courseId) {
        String courseName = "";
        String query = "SELECT name FROM courses WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                courseName = rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseName;
    }

    public static void main(String[] args) {
        int studentId = 4;
        Map<String, Integer> streaks = getStudentStreaks(studentId);
        System.out.println("Current Streaks for Student ID: " + studentId);
        for (Map.Entry<String, Integer> entry : streaks.entrySet()) {
            System.out.println("Course Name: " + entry.getKey() + ", Streak: " + entry.getValue());
        }
    }
}
