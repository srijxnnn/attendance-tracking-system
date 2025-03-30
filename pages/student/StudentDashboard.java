package pages.student;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

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
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.LIGHT_GRAY);
                int circleDiameter = 60;
                int x = (getWidth() - circleDiameter) / 2;
                int y = 20;
                g2.fillOval(x, y, circleDiameter, circleDiameter);
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
                JOptionPane.showMessageDialog(null, "Edit Profile clicked!");
            }
        });

        userPanel.add(usernameLabel);
        userPanel.add(editLabel);

        sidebar.add(userPanel);

        String[] options = {"Dashboard", "Leave Application", "Student Calendar", "Attendance Report"};
//        for (int i = 0; i < options.length; i++) {
//            JLabel optionLabel = new JLabel(options[i], SwingConstants.CENTER);
//            optionLabel.setForeground(Color.WHITE);
//            optionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
//            optionLabel.setOpaque(true);
//            optionLabel.setBackground(new Color(51, 51, 51));
//            optionLabel.setBounds(0, 160 + i * 50, 200, 40);
//
//            optionLabel.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseEntered(MouseEvent e) {
//                    optionLabel.setBackground(new Color(70, 70, 70));
//                }
//                @Override
//                public void mouseExited(MouseEvent e) {
//                    optionLabel.setBackground(new Color(51, 51, 51));
//                }
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    JOptionPane.showMessageDialog(null, optionLabel.getText() + " clicked");
//                }
//            });
//            sidebar.add(optionLabel);
//        }

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
                JOptionPane.showMessageDialog(null, optionDashboardLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() ->
                {
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
                JOptionPane.showMessageDialog(null, optionLeaveApplicationLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() -> {
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
                JOptionPane.showMessageDialog(null, optionCalenderLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() ->
                {
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
                JOptionPane.showMessageDialog(null, optionStudentAttendanceReportLabel.getText() + " clicked");
                SwingUtilities.invokeLater(()->
                {
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
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Logout clicked");
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

        // Bar Chart Panel (Attendance Stats)
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

                // Working Days bar
                g2.setColor(new Color(52, 152, 219)); // Blue bar
                int workingBarHeight = workingDays * scaleFactor;
                g2.fillRect(50, chartBase - workingBarHeight, barWidth, workingBarHeight);

                // Absences bar
                g2.setColor(new Color(231, 76, 60)); // Red bar
                int absenceBarHeight = absences * scaleFactor;
                g2.fillRect(50 + spacing, chartBase - absenceBarHeight, barWidth, absenceBarHeight);

                // Bar labels
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString("Working Days (" + workingDays + ")", 50, chartBase + 20);
                g2.drawString("Absences (" + absences + ")", 50 + spacing, chartBase + 20);
            }
        };
        barChartPanel.setBounds(210, 250, 760, 400);
        barChartPanel.setOpaque(false);

        backgroundPanel.add(sidebar);
        backgroundPanel.add(headerPanel);
        backgroundPanel.add(topInfoPanel);
        backgroundPanel.add(progressPanel);
        backgroundPanel.add(barChartPanel);

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