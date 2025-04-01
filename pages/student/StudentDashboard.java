package pages.student;

import db.DatabaseConnection;
import pages.auth.UserAuthentication;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;


public class StudentDashboard extends JFrame {
    private int attendancePercentage;
    private int workingDays;
    private int absences;
    private String studentName;
    private Integer regNo;
    private int userId;
    private int studentId;

    private int progress = 0;
    private JPanel progressPanel;

    public StudentDashboard(int userId) {
        this.userId = userId;
        loadStudentData();
        initUI();
    }

    private void loadStudentData() {
        try (Connection conn = DatabaseConnection.getInstance()) {
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
                        regNo       = rs.getInt("reg_no");
                    }
                }
            }

            String attendanceQuery = "SELECT "
                    + "(SELECT COUNT(*) FROM attendances WHERE student_id = ?) AS total_days, "
                    + "(SELECT COUNT(*) FROM attendances WHERE student_id = ? AND status = 'absent') AS total_absent";
            try (PreparedStatement ps = conn.prepareStatement(attendanceQuery)) {
                ps.setInt(1, studentId);
                ps.setInt(2, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int totalDays   = rs.getInt("total_days");
                        int totalAbsent = rs.getInt("total_absent");

                        this.workingDays = totalDays;
                        this.absences    = totalAbsent;

                        int totalPresent = totalDays - totalAbsent;

                        if (totalDays != 0) {
                            this.attendancePercentage = (int) ((totalPresent / (double) totalDays) * 100);
                        } else {
                            this.attendancePercentage = 0;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Student Dashboard");
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
                int d = 60; // Diameter of the circle.
                int x = (getWidth() - d) / 2;
                int y = 20;
                // Draw the image scaled to fit within the circle bounds.
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
                // Instead of showing a simple message, open the StudentEditProfile dialog.
                // Assuming StudentEditProfile has a constructor that accepts userId:
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
                    StudentDashboard.this.dispose();
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
                    StudentDashboard.this.dispose();
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
                    StudentDashboard.this.dispose();
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
                    StudentDashboard.this.dispose();
                    new AttendanceReportPage(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);

        // Header Panel
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBounds(210, 10, 760, 40);
        headerPanel.setBackground(new Color(255, 255, 255, 80));

        JLabel dashboardLabel = new JLabel("Student Dashboard");
        dashboardLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        dashboardLabel.setBounds(20, 5, 250, 30);

        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setFocusable(false);
        logoutButton.setBounds(650, 5, 100, 30);
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                StudentDashboard.this.dispose();
                new UserAuthentication().setVisible(true);
            }
        });

        headerPanel.add(dashboardLabel);
        headerPanel.add(logoutButton);

        // Info Panel
        JPanel topInfoPanel = new JPanel(null);
        topInfoPanel.setBounds(210, 70, 550, 150);
        topInfoPanel.setBackground(new Color(255, 255, 255, 80));

        JLabel welcomeLabel = new JLabel("Welcome " + studentName + " !!!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setBounds(20, 10, 400, 30);

        JLabel semesterLabel = new JLabel("Semester: 4");
        semesterLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        semesterLabel.setBounds(20, 50, 200, 20);

        JLabel courseLabel = new JLabel("Course: BTECH CSE");
        courseLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        courseLabel.setBounds(20, 80, 200, 20);

        JLabel regdLabel = new JLabel("REGD No: " + regNo);
        regdLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        regdLabel.setBounds(20, 110, 200, 20);

        topInfoPanel.add(welcomeLabel);
        topInfoPanel.add(semesterLabel);
        topInfoPanel.add(courseLabel);
        topInfoPanel.add(regdLabel);

        // Circular Progress Panel (Attendance %)
        progressPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setStroke(new BasicStroke(10));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(10, 10, 110, 110);


                g2.setColor(new Color(46, 204, 113));
                g2.drawArc(10, 10, 110, 110, 90, progress * 360 / 100);

                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                String text = progress + "%";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() / 2 + fm.getHeight() / 4;
                g2.drawString(text, x, y);
            }
        };
        progressPanel.setBounds(800, 70, 140, 140);
        progressPanel.setOpaque(false);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(null);
        statsPanel.setBounds(210, 250, 760, 400);
        statsPanel.setOpaque(false);

        // Bar Chart Panel
        JPanel barChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.drawString("Attendance Chart", 20, 30);

                int chartBase = getHeight() - 50;
                int barWidth = 80;
                int spacing = 150;
                int scaleFactor = 5;

                g2.setColor(new Color(52, 152, 219));
                int workingBarHeight = workingDays * scaleFactor;
                g2.fillRect(50, chartBase - workingBarHeight, barWidth, workingBarHeight);

                g2.setColor(new Color(231, 76, 60));
                int absenceBarHeight = absences * scaleFactor;
                g2.fillRect(50 + spacing, chartBase - absenceBarHeight, barWidth, absenceBarHeight);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString("Working Days (" + workingDays + ")", 50, chartBase + 20);
                g2.drawString("Absences (" + absences + ")", 50 + spacing, chartBase + 20);
            }
        };
        barChartPanel.setBounds(0, 0, 355, 400);
        barChartPanel.setOpaque(false);

        // Streaks Panel
        JPanel streaksPanel = new JPanel();
        streaksPanel.setLayout(new GridLayout(3, 1, 10, 10));
        streaksPanel.setBounds(380, 50, 370, 300);
        streaksPanel.setOpaque(false);

        Map<String, Integer> map = StudentStreakFetcher.getStudentStreaks(studentId);

        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<Integer> streaks = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            subjects.add(entry.getKey());
            streaks.add(entry.getValue());
            System.out.println("Course Name: " + entry.getKey() + ", Streak: " + entry.getValue());
        }

        Color[] colors = {new Color(0, 128, 255), new Color(50, 205, 50), new Color(70, 130, 180)};

        
        for (int i = 0; i < subjects.size(); i++) {
            JPanel streakItem = new JPanel();
            streakItem.setLayout(new BorderLayout());
            streakItem.setBackground(new Color(255, 255, 255, 100));
            streakItem.setBounds(0, 0, 200, 70); // Increase width from 400 to 500
            streakItem.setPreferredSize(new Dimension(200, 70));
            streakItem.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
            streakItem.setBorder(BorderFactory.createCompoundBorder(
                streakItem.getBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)  // Add padding for better effect
            ));
            
            // Set rounded corners for the streak item
            streakItem.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 100), 4, true));

            // Load the GIF using ImageIcon for animation
            String gifPath = "./pages/student/fire.png";  // Replace with the correct path
            File gifFile = new File(gifPath);

            if (gifFile.exists()) {
                System.out.println("GIF found: " + gifPath);
            } else {
                System.out.println("GIF not found: " + gifPath);
            }

            // Create the ImageIcon directly
            ImageIcon fireGif = new ImageIcon(gifPath);
            Image image = fireGif.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(image);
            // Create a JLabel to hold the GIF and ensure it is animated
            JLabel gifLabel = new JLabel(resizedIcon);
            gifLabel.setPreferredSize(new Dimension(70, 10));  // Adjust the size to fit better

            // Wrap the JLabel in a JPanel for proper placement
            JPanel gifPanel = new JPanel();
            gifPanel.setLayout(new BorderLayout());  // FlowLayout or BorderLayout can be used
            gifPanel.setOpaque(false);  // Transparent background
            gifPanel.add(gifLabel, BorderLayout.WEST);  // Add GIF to center
            gifPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));  // Adds 20px padding on the left

            // Add the gifPanel to the streakItem panel (west side)
            streakItem.add(gifPanel, BorderLayout.WEST);
            
            // Create a JLabel for the streak text
            JLabel streakLabel = new JLabel(subjects.get(i) + " Streak: " + streaks.get(i) + " days", JLabel.CENTER);
            streakLabel.setFont(new Font("Calibri", Font.BOLD, 18));
            streakLabel.setForeground(Color.DARK_GRAY);

            // Add the streak label to the center of the streakItem panel
            streakItem.add(streakLabel, BorderLayout.CENTER);

            // Add the streak item to the main streaks panel
            streaksPanel.add(streakItem);
        }

        
        
        // Add streaksPanel to the background panel
        backgroundPanel.add(streaksPanel);


        // Adding to Parent Panel
        statsPanel.add(barChartPanel);
        statsPanel.add(streaksPanel);
        backgroundPanel.add(sidebar);
        backgroundPanel.add(headerPanel);
        backgroundPanel.add(topInfoPanel);
        backgroundPanel.add(progressPanel);
        backgroundPanel.add(statsPanel);

        add(backgroundPanel);

        // Timer for circular progress animation
        Timer timer = new Timer(10, e -> {
            if (progress < attendancePercentage) {
                progress++;
                progressPanel.repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentDashboard(1).setVisible(true);
        });
    }
}