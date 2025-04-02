package pages.student;

import db.DatabaseConnection;
import pages.auth.UserAuthentication;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class StudentLeaveApplicationPage extends JFrame {
    private String studentName = "";
    private int regNo = 0;

    private Map<String, Integer> courseMap = new HashMap<>();

    private JComboBox<String> subjectComboBox;
    private JTextArea reasonTextArea;
    private DefaultTableModel tableModel;

    private int studentId;
    private int userId;

    public StudentLeaveApplicationPage(int userId) {
        this.userId=userId;
        loadStudentData(userId);
        initUI();
        loadRecentLeaves();
    }
    private void loadStudentData(int userId) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            // 1) Load student basic info
            String studentIdQuery = "SELECT * FROM students WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(studentIdQuery)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        studentId = rs.getInt("id");
                    }
                }
            }
            String studentInfoQuery = "SELECT name, reg_no FROM students WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(studentInfoQuery)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        studentName = rs.getString("name");
                        regNo = rs.getInt("reg_no");
                    }
                }
            }

            String courseQuery = "SELECT c.id, c.code " +
                    "FROM courses c " +
                    "JOIN student_courses sc ON c.id = sc.course_id " +
                    "WHERE sc.student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(courseQuery)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int courseId = rs.getInt("id");
                        String code  = rs.getString("code");
                        courseMap.put(code, courseId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Leave Application");
        setSize(1000, 700);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(44, 62, 80),
                        getWidth(), getHeight(), new Color(189, 195, 199)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        backgroundPanel.setLayout(null);

        // Sidebar
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setBounds(0, 0, 200, getHeight());

        JPanel userPanel = new JPanel(null) {
            private Image profileImage;

            {
                profileImage = new ImageIcon("pages/profile-circle-border.png").getImage();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int d = 60; 
                int x = (getWidth() - d) / 2;
                int y = 20;
                g2.drawImage(profileImage, x, y, d, d, this);
            }
        };
        userPanel.setBounds(0, 0, 200, 150);
        userPanel.setBackground(new Color(51, 51, 51));

        JLabel usernameLabel = new JLabel(studentName, SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setBounds(0, 90, 200, 20);

        JLabel editLabel = new JLabel("Edit Profile", SwingConstants.CENTER);
        editLabel.setForeground(new Color(200, 200, 200));
        editLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        editLabel.setBounds(0, 115, 200, 20);
        editLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                editLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                editLabel.setForeground(new Color(200, 200, 200));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                new StudentEditProfile(userId).setVisible(true);
            }
        });

        userPanel.add(usernameLabel);
        userPanel.add(editLabel);
        sidebar.add(userPanel);

        String[] options = {"Dashboard", "Leave Application", "Student Calendar", "Attendance Report"};

        JLabel optionDashboardLabel=new JLabel(options[0], SwingConstants.CENTER);
        optionDashboardLabel.setForeground(Color.WHITE);
        optionDashboardLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionDashboardLabel.setOpaque(true);
        optionDashboardLabel.setBackground(new Color(51, 51, 51));
        optionDashboardLabel.setBounds(0, 160+0*50, 200, 40);

        optionDashboardLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                optionDashboardLabel.setBackground(new Color(70, 70, 70));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                optionDashboardLabel.setBackground(new Color(51, 51, 51));
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                SwingUtilities.invokeLater(() ->
                {
                    StudentLeaveApplicationPage.this.dispose();
                    new StudentDashboard(userId).setVisible(true);
                });

            }
        });
        sidebar.add(optionDashboardLabel);

        JLabel optionLeaveApplicationLabel=new JLabel(options[1], SwingConstants.CENTER);
        optionLeaveApplicationLabel.setForeground(Color.WHITE);
        optionLeaveApplicationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionLeaveApplicationLabel.setOpaque(true);
        optionLeaveApplicationLabel.setBackground(new Color(51, 51, 51));
        optionLeaveApplicationLabel.setBounds(0, 160+1*50, 200, 40);

        optionLeaveApplicationLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                optionLeaveApplicationLabel.setBackground(new Color(70, 70, 70));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                optionLeaveApplicationLabel.setBackground(new Color(51, 51, 51));
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                SwingUtilities.invokeLater(() -> {
                    StudentLeaveApplicationPage.this.dispose();
                    new StudentLeaveApplicationPage(userId).setVisible(true);
                });

            }
        });
        sidebar.add(optionLeaveApplicationLabel);

        JLabel optionCalenderLabel=new JLabel(options[2], SwingConstants.CENTER);
        optionCalenderLabel.setForeground(Color.WHITE);
        optionCalenderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionCalenderLabel.setOpaque(true);
        optionCalenderLabel.setBackground(new Color(51, 51, 51));
        optionCalenderLabel.setBounds(0, 160+2*50, 200, 40);

        optionCalenderLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                optionCalenderLabel.setBackground(new Color(70, 70, 70));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                optionCalenderLabel.setBackground(new Color(51, 51, 51));
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                SwingUtilities.invokeLater(() ->
                {
                    StudentLeaveApplicationPage.this.dispose();
                    new StudentCalendar(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionCalenderLabel);

        JLabel optionStudentAttendanceReportLabel=new JLabel(options[3], SwingConstants.CENTER);
        optionStudentAttendanceReportLabel.setForeground(Color.WHITE);
        optionStudentAttendanceReportLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionStudentAttendanceReportLabel.setOpaque(true);
        optionStudentAttendanceReportLabel.setBackground(new Color(51, 51, 51));
        optionStudentAttendanceReportLabel.setBounds(0, 160+3*50, 200, 40);

        optionStudentAttendanceReportLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                optionStudentAttendanceReportLabel.setBackground(new Color(70, 70, 70));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                optionStudentAttendanceReportLabel.setBackground(new Color(51, 51, 51));
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                SwingUtilities.invokeLater(()->
                {
                    StudentLeaveApplicationPage.this.dispose();
                    new AttendanceReportPage(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);

        // Header Panel
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBounds(210, 10, 760, 40);
        headerPanel.setBackground(new Color(255, 255, 255, 80));

        JLabel headerLabel = new JLabel("Leave Application");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setBounds(20, 5, 250, 30);

        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setFocusable(false);
        logoutButton.setBounds(650, 5, 100, 30);
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                StudentLeaveApplicationPage.this.dispose();
                new UserAuthentication().setVisible(true);
            }
        });

        headerPanel.add(headerLabel);
        headerPanel.add(logoutButton);

        JPanel mainContentPanel = new JPanel(null);
        mainContentPanel.setBounds(210, 70, 760, 580);
        mainContentPanel.setBackground(new Color(255, 255, 255, 80));

        JLabel subjectCodeLabel = new JLabel("Subject Code:");
        subjectCodeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        subjectCodeLabel.setBounds(30, 30, 120, 30);

        subjectComboBox = new JComboBox<>(courseMap.keySet().toArray(new String[0]));
        subjectComboBox.setBounds(160, 30, 200, 30);
        subjectComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                backgroundPanel.repaint();
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });


        JLabel reasonLabel = new JLabel("Reason for Leave:");
        reasonLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        reasonLabel.setBounds(30, 80, 150, 30);

        reasonTextArea = new JTextArea();
        reasonTextArea.setLineWrap(true);
        reasonTextArea.setWrapStyleWord(true);

        JScrollPane reasonScrollPane = new JScrollPane(reasonTextArea);
        reasonScrollPane.setBounds(30, 120, 500, 150);

        JButton requestLeaveButton = new JButton("Request Leave");
        requestLeaveButton.setBounds(30, 280, 150, 30);
        requestLeaveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleLeaveRequest();
            }
        });

        // Recent Applications Table
        JLabel recentLabel = new JLabel("Your Recent Applications");
        recentLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        recentLabel.setBounds(30, 330, 250, 30);

        String[] columnNames = {"Subject Code", "Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable recentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(recentTable);
        tableScrollPane.setBounds(30, 370, 500, 150);

        mainContentPanel.add(subjectCodeLabel);
        mainContentPanel.add(subjectComboBox);
        mainContentPanel.add(reasonLabel);
        mainContentPanel.add(reasonScrollPane);
        mainContentPanel.add(requestLeaveButton);
        mainContentPanel.add(recentLabel);
        mainContentPanel.add(tableScrollPane);

        // Add all panels to background
        backgroundPanel.add(sidebar);
        backgroundPanel.add(headerPanel);
        backgroundPanel.add(mainContentPanel);

        add(backgroundPanel);
    }

    private void handleLeaveRequest() {
        String selectedCode = (String) subjectComboBox.getSelectedItem();
        String reason = reasonTextArea.getText().trim();

        if (selectedCode == null || selectedCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a subject code.");
            return;
        }
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide a reason for leave.");
            return;
        }

        int courseId = courseMap.get(selectedCode);

        LocalDate today = LocalDate.now();
        Date sqlDate = Date.valueOf(today);  

        try (Connection conn = DatabaseConnection.getInstance()) {
            String insertQuery = "INSERT INTO student_leaves (student_id, date, course_id, status, reason) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setInt(1, studentId);
                ps.setDate(2, sqlDate);
                ps.setInt(3, courseId);
                ps.setString(4, "pending");
                ps.setString(5, reason);

                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Leave Requested Successfully!");

            reasonTextArea.setText("");

            loadRecentLeaves();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error requesting leave!");
        }
    }


    private void loadRecentLeaves() {
        tableModel.setRowCount(0);

        String query = "SELECT sl.date, c.code, sl.status " +
                "FROM student_leaves sl " +
                "JOIN courses c ON sl.course_id = c.id " +
                "WHERE sl.student_id = ? " +
                "ORDER BY sl.id DESC"; 

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, studentId);
            System.out.println(studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date leaveDate    = rs.getDate("date");
                    String courseCode = rs.getString("code");
                    String status     = rs.getString("status");

                    tableModel.addRow(new Object[]{courseCode, leaveDate.toString(), status});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentLeaveApplicationPage(1).setVisible(true);
        });
    }
}

