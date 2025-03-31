package pages.faculty;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FacultyEditProfile extends JFrame {

    private int userId;
    // Profile details loaded from DB.
    private String username;      // from users table
    private String email;         // from users table
    private String name;          // from faculty table
    private String designation;   // from faculty table
    private String expertise;     // from faculty table
    // Courses are mapped via faculty_courses.
    // We'll load a comma-separated list of course codes.
    private String courses;

    // UI Components
    private JTextField usernameField; // now visible (editable if desired)
    private JTextField emailField;
    private JTextField nameField;
    private JTextField designationField;
    private JTextField expertiseField;
    private JTextField coursesField; // displays comma-separated course codes
    private JButton saveButton;

    public FacultyEditProfile(int userId) {
        this.userId = userId;
        loadUserInfo();     // Loads username and email from users table.
        loadFacultyInfo();  // Loads name, designation, expertise from faculty table.
        loadFacultyCourses(); // Loads comma-separated course codes.
        initUI();
    }

    /**
     * Loads username and email from the users table using userId.
     */
    private void loadUserInfo() {
        String query = "SELECT username, email FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                    email = rs.getString("email");

                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads the faculty's profile details (name, designation, expertise) from the faculty table.
     */
    private void loadFacultyInfo() {
        String query = "SELECT name, designation, expertise FROM faculty WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    designation = rs.getString("designation");
                    expertise = rs.getString("expertise");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads the faculty's course codes from the database and sets the courses variable as a comma-separated string.
     */
    private void loadFacultyCourses() {
        String query = "SELECT c.code " +
                "FROM faculty_courses fc " +
                "JOIN courses c ON fc.course_id = c.id " +
                "WHERE fc.faculty_id = (SELECT id FROM faculty WHERE user_id = ?)";
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(rs.getString("code"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        courses = sb.toString();
    }

    /**
     * Updates both the users table and the faculty table.
     */
    private void updateProfile() {
        // Update users table for username and email.
        String userQuery = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        // Update faculty table for name, designation, expertise.
        String facultyQuery = "UPDATE faculty SET name = ?, designation = ?, expertise = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement psUser = conn.prepareStatement(userQuery);
             PreparedStatement psFaculty = conn.prepareStatement(facultyQuery)) {

            psUser.setString(1, usernameField.getText().trim());
            psUser.setString(2, emailField.getText().trim());
            psUser.setInt(3, userId);
            int userRows = psUser.executeUpdate();

            psFaculty.setString(1, nameField.getText().trim());
            psFaculty.setString(2, designationField.getText().trim());
            psFaculty.setString(3, expertiseField.getText().trim());
            psFaculty.setInt(4, userId);
            int facultyRows = psFaculty.executeUpdate();

            if (userRows > 0 && facultyRows > 0) {
                updateFacultyCourses(coursesField.getText().trim());
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Profile update failed!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates the faculty_courses mapping.
     * This method deletes any existing mapping for the faculty and then inserts new rows
     * based on the comma-separated course codes provided.
     */
    private void updateFacultyCourses(String coursesInput) {
        List<String> courseCodes = Arrays.stream(coursesInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        String deleteQuery = "DELETE FROM faculty_courses WHERE faculty_id = (SELECT id FROM faculty WHERE user_id = ?)";
        String selectCourseQuery = "SELECT id FROM courses WHERE code = ?";
        String insertQuery = "INSERT INTO faculty_courses (faculty_id, course_id) VALUES ((SELECT id FROM faculty WHERE user_id = ?), ?)";
        try (Connection conn = DatabaseConnection.getInstance()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psDel = conn.prepareStatement(deleteQuery)) {
                psDel.setInt(1, userId);
                psDel.executeUpdate();
            }
            for (String code : courseCodes) {
                int courseId = 0;
                try (PreparedStatement psSel = conn.prepareStatement(selectCourseQuery)) {
                    psSel.setString(1, code);
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) {
                            courseId = rs.getInt("id");
                        }
                    }
                }
                if (courseId != 0) {
                    try (PreparedStatement psIns = conn.prepareStatement(insertQuery)) {
                        psIns.setInt(1, userId);
                        psIns.setInt(2, courseId);
                        psIns.executeUpdate();
                    }
                } else {
                    System.out.println("Course code not found: " + code);
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Edit Profile");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        ProfilePanel panel = new ProfilePanel();
        panel.setBounds(0, 0, 600, 300);
        add(panel);
    }

    /**
     * Custom panel for editing profile details.
     */
    private class ProfilePanel extends JPanel {
        private float alpha = 0f;
        private Timer fadeTimer;

        public ProfilePanel() {
            setLayout(null);
            setPreferredSize(new Dimension(600, 300));
            setOpaque(false);

            fadeTimer = new Timer(20, e -> {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadeTimer.stop();
                }
                repaint();
            });
            fadeTimer.start();

            // Circular placeholder for profile picture.
            JPanel picPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.LIGHT_GRAY);
                    int d = Math.min(getWidth(), getHeight()) - 10;
                    int x = (getWidth() - d) / 2;
                    int y = (getHeight() - d) / 2;
                    g2.fillOval(x, y, d, d);
                }
            };
            picPanel.setBounds(20, 20, 100, 100);
            picPanel.setOpaque(false);
            add(picPanel);

            // Editable fields for profile details.
            JLabel unameLabel = new JLabel("Username:");
            unameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            unameLabel.setBounds(150, 20, 80, 20);
            add(unameLabel);

            usernameField = new JTextField(username);
            usernameField.setBounds(230, 20, 120, 25);
            add(usernameField);

            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            nameLabel.setBounds(150, 60, 80, 20);
            add(nameLabel);

            nameField = new JTextField(name);
            nameField.setBounds(230, 60, 120, 25);
            add(nameField);

            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emailLabel.setBounds(150, 100, 80, 20);
            add(emailLabel);

            emailField = new JTextField(email);
            emailField.setBounds(230, 100, 120, 25);
            add(emailField);

            JLabel desigLabel = new JLabel("Designation:");
            desigLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            desigLabel.setBounds(370, 60, 80, 20);
            add(desigLabel);

            designationField = new JTextField(designation);
            designationField.setBounds(450, 60, 120, 25);
            add(designationField);

            JLabel expertLabel = new JLabel("Expertise:");
            expertLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            expertLabel.setBounds(370, 100, 80, 20);
            add(expertLabel);

            expertiseField = new JTextField(expertise);
            expertiseField.setBounds(450, 100, 120, 25);
            add(expertiseField);

            JLabel coursesLabel = new JLabel("Courses:");
            coursesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            coursesLabel.setBounds(150, 140, 80, 20);
            add(coursesLabel);

            coursesField = new JTextField(courses);
            coursesField.setBounds(230, 140, 340, 25);
            add(coursesField);

            saveButton = new JButton("SAVE");
            saveButton.setBounds(360, 190, 80, 30);
            styleButton(saveButton);
            addHoverEffect(saveButton);
            add(saveButton);

            saveButton.addActionListener(e -> {
                updateProfile();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80),
                    getWidth(), getHeight(), new Color(189, 195, 199));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
            super.paintComponent(g);
        }

        private void styleButton(JButton btn) {
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setBackground(new Color(0,123,255));
            btn.setForeground(Color.WHITE);
            btn.setBorder(new LineBorder(new Color(0,123,255), 2));
        }

        private void addHoverEffect(JButton btn) {
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(200,200,200));
                    btn.setForeground(Color.BLACK);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(0,123,255));
                    btn.setForeground(Color.WHITE);
                }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyEditProfile(6).setVisible(true);
        });
    }
}
