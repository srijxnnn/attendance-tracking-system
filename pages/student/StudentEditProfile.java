package pages.student;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class StudentEditProfile extends JFrame {

    private int userId;
    private String username;  
    private String email;      
    private String name;      
    private String regdNo;     
    private String roll;       
    private String courses;     

    // UI Components
    private JTextField usernameField; 
    private JTextField emailField;    
    private JTextField nameField; 
    private JTextField regdField;    
    private JTextField rollField;    
    private JTextArea coursesArea;  
    private JButton saveButton;

    public StudentEditProfile(int userId) {
        this.userId = userId;
        loadUserInfo();      
        loadStudentInfo();   
        loadStudentCourses(); 
        initUI();
    }

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

    private void loadStudentInfo() {
        String query = "SELECT name, reg_no, roll FROM students WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    regdNo = rs.getString("reg_no");
                    roll = rs.getString("roll");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadStudentCourses() {
        String query = "SELECT c.code " +
                "FROM student_courses sc " +
                "JOIN courses c ON sc.course_id = c.id " +
                "WHERE sc.student_id = (SELECT id FROM students WHERE user_id = ?)";
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

    private void updateProfile() {
        String userQuery = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement psUser = conn.prepareStatement(userQuery)) {

            psUser.setString(1, usernameField.getText().trim());
            psUser.setString(2, emailField.getText().trim());
            psUser.setInt(3, userId);
            int userRows = psUser.executeUpdate();

            if (userRows > 0) {
                updateStudentCourses(coursesArea.getText().trim());
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Profile update failed!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateStudentCourses(String coursesInput) {
        List<String> courseCodes = Arrays.stream(coursesInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        String deleteQuery = "DELETE FROM student_courses WHERE student_id = (SELECT id FROM students WHERE user_id = ?)";
        String selectCourseQuery = "SELECT id FROM courses WHERE code = ?";
        String insertQuery = "INSERT INTO student_courses (student_id, course_id) VALUES ((SELECT id FROM students WHERE user_id = ?), ?)";
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

            JPanel picPanel = new JPanel() {
                private Image profileImage = new ImageIcon("pages/profile-circle-border.png").getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    int d = Math.min(getWidth(), getHeight()) - 10; // Diameter of the circle.
                    int x = (getWidth() - d) / 2;
                    int y = (getHeight() - d) / 2;
                    // Draw the image scaled to fit within the circle bounds.
                    g2.drawImage(profileImage, x, y, d, d, this);
                }
            };
            picPanel.setBounds(20, 20, 100, 100);
            picPanel.setOpaque(false);
            add(picPanel);

            // Non-editable fields.
            JLabel unameLabel = new JLabel("Username:");
            unameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            unameLabel.setBounds(150, 20, 80, 20);
            add(unameLabel);

            usernameField = new JTextField(username);
            usernameField.setBounds(230, 20, 120, 25);
            usernameField.setEditable(false);
            add(usernameField);

            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emailLabel.setBounds(150, 60, 80, 20);
            add(emailLabel);

            emailField = new JTextField(email);
            emailField.setBounds(230, 60, 180, 25);
            add(emailField);

            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            nameLabel.setBounds(150, 100, 80, 20);
            add(nameLabel);

            nameField = new JTextField(name);
            nameField.setBounds(230, 100, 120, 25);
            nameField.setEditable(false);
            add(nameField);

            JLabel regdLabel = new JLabel("Regd No.:");
            regdLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            regdLabel.setBounds(150, 140, 80, 20);
            add(regdLabel);

            regdField = new JTextField(regdNo);
            regdField.setBounds(230, 140, 120, 25);
            regdField.setEditable(false);
            add(regdField);

            JLabel rollLabel = new JLabel("Roll No.:");
            rollLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            rollLabel.setBounds(370, 20, 80, 20);
            add(rollLabel);

            rollField = new JTextField(roll);
            rollField.setBounds(450, 20, 120, 25);
            rollField.setEditable(false);
            add(rollField);

            JLabel coursesLabel = new JLabel("Courses:");
            coursesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            coursesLabel.setBounds(150, 180, 80, 20);
            add(coursesLabel);

            coursesArea = new JTextArea(courses);
            coursesArea.setLineWrap(true);
            coursesArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(coursesArea);
            scrollPane.setBounds(230, 180, 340, 60);
            scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                @Override protected void configureScrollBarColors() {}
                @Override protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
                @Override protected JButton createIncreaseButton(int orientation) { return zeroButton(); }
                private JButton zeroButton() {
                    JButton btn = new JButton();
                    btn.setPreferredSize(new Dimension(0,0));
                    return btn;
                }
                @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
                @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {}
            });
            add(scrollPane);

            saveButton = new JButton("SAVE");
            saveButton.setBounds(360, 250, 80, 30);
            styleButton(saveButton);
            addHoverEffect(saveButton);
            add(saveButton);

            saveButton.addActionListener(e -> updateProfile());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            GradientPaint gp = new GradientPaint(0, 0, new Color(44,62,80),
                    getWidth(), getHeight(), new Color(189,195,199));
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
        SwingUtilities.invokeLater(() -> new StudentEditProfile(1).setVisible(true));
    }
}

