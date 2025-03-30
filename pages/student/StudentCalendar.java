package pages.student;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class StudentCalendar extends JFrame {

    private final String[] options = {
            "Dashboard", "Leave Application", "Student Calendar", "Attendance Report"
    };

    // We'll load the student's courses from DB rather than hard-coding them
    // We'll store (courseCode -> courseId) for easy lookup:
    private Map<String, Integer> courseMap = new HashMap<>();

    // For the month & year combos:
    private final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    private final String[] years = {"2022", "2023", "2024", "2025", "2026"};

    // UI components
    private JPanel calendarPanel;
    private JLabel totalWorkingDaysLabel;
    private JLabel presentLabel;
    private JLabel absentLabel;
    private JPanel progressPanel;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> subjectCombo;

    // Attendance counters
    private int presentCount, absentCount, unmarkedCount;

    // For the circular progress
    private int attendancePercentage = 0; // Will compute from DB data
    private int progress = 0;

    // Student info
    private int userId;
    private int studentId;
    private String studentName = "Unknown"; // Will load from DB

    public StudentCalendar(int userId) {
        this.userId = userId;
        loadStudentInfo();     // Load student name + courses
        initUI();
        rebuildCalendar();     // Build initial calendar display
    }

    /**
     * Loads the student's name and courses from the database.
     */
    private void loadStudentInfo() {
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

            // 1) Load the student's name
            String infoQuery = "SELECT name FROM students WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(infoQuery)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        studentName = rs.getString("name");
                    }
                }
            }

            // 2) Load the student's courses (id + code)
            String courseQuery =
                    "SELECT c.id, c.code " +
                            "FROM courses c " +
                            "JOIN student_courses sc ON c.id = sc.course_id " +
                            "WHERE sc.student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(courseQuery)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int cId = rs.getInt("id");
                        String code = rs.getString("code");
                        courseMap.put(code, cId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Student Calendar Page");
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
        add(backgroundPanel);

        // Sidebar
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setBounds(0, 0, 200, getHeight());
        backgroundPanel.add(sidebar);

        // User Panel on Sidebar
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

        // Sidebar options
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
                    System.out.println(userId);
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

        // Top panel
        JPanel topPanel = new JPanel(null);
        topPanel.setBounds(210, 20, 760, 50);
        topPanel.setBackground(new Color(255, 255, 255, 80));
        backgroundPanel.add(topPanel);

        monthCombo = new JComboBox<>(months);
        monthCombo.setBounds(20, 10, 120, 30);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1); // current month
        topPanel.add(monthCombo);

        yearCombo = new JComboBox<>(years);
        yearCombo.setBounds(160, 10, 100, 30);
        // Set default to something close to current year:
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < years.length; i++) {
            if (String.valueOf(currentYear).equals(years[i])) {
                yearCombo.setSelectedIndex(i);
                break;
            }
        }
        topPanel.add(yearCombo);

        // Subject combo from DB courses
        subjectCombo = new JComboBox<>(courseMap.keySet().toArray(new String[0]));
        subjectCombo.setBounds(280, 10, 120, 30);
        topPanel.add(subjectCombo);

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBounds(620, 10, 100, 30);
        topPanel.add(logoutBtn);

        // Calendar panel
        calendarPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        calendarPanel.setBounds(210, 80, 500, 380);
        calendarPanel.setOpaque(false);
        backgroundPanel.add(calendarPanel);

        // Circular progress panel
        progressPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(10));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(10, 10, 120, 120);

                g2.setColor(new Color(46, 204, 113));
                // Draw arc based on "progress" (0..attendancePercentage)
                g2.drawArc(10, 10, 120, 120, 90, progress * 360 / 100);

                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                String text = progress + "%";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() / 2 + fm.getHeight() / 4;
                g2.drawString(text, x, y);
            }
        };
        progressPanel.setBounds(760, 120, 140, 140);
        progressPanel.setOpaque(false);
        backgroundPanel.add(progressPanel);

        // Label "ATTENDANCE PERCENTAGE"
        JPanel attPercentPanel = new JPanel(null);
        attPercentPanel.setBackground(new Color(255, 255, 255, 80));
        attPercentPanel.setBounds(730, 270, 180, 40);

        JLabel attPercentLabel = new JLabel("ATTENDANCE PERCENTAGE", SwingConstants.CENTER);
        attPercentLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        attPercentLabel.setBounds(0, 0, 180, 40);

        attPercentPanel.add(attPercentLabel);
        backgroundPanel.add(attPercentPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bottomPanel.setBounds(210, 480, 760, 100);
        bottomPanel.setOpaque(false);
        backgroundPanel.add(bottomPanel);

        totalWorkingDaysLabel = new JLabel("TOTAL WORKING DAYS: 0");
        totalWorkingDaysLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalWorkingDaysLabel.setBounds(20, 20, 300, 30);
        bottomPanel.add(totalWorkingDaysLabel);

        presentLabel = new JLabel("Presents: 0");
        presentLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        presentLabel.setBounds(350, 20, 150, 30);
        bottomPanel.add(presentLabel);

        absentLabel = new JLabel("Absents: 0");
        absentLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        absentLabel.setBounds(500, 20, 150, 30);
        bottomPanel.add(absentLabel);

        // Listeners to rebuild calendar when month/year/subject changes
        monthCombo.addActionListener(e -> rebuildCalendar());
        yearCombo.addActionListener(e -> rebuildCalendar());
        subjectCombo.addActionListener(e -> rebuildCalendar());
    }

    /**
     * Called whenever the user changes month, year, or subject.
     */
    private void rebuildCalendar() {
        // Clear old day labels
        calendarPanel.removeAll();

        // Build the new calendar display
        buildDynamicCalendar();

        // Recompute the attendance percentage & animate the progress arc
        animateAttendanceArc();

        // Refresh UI
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    /**
     * Builds the day headers + day squares using real attendance data from DB.
     */
    private void buildDynamicCalendar() {
        // 1) figure out which month/year/course is selected
        int selectedMonth = monthCombo.getSelectedIndex() + 1;
        int selectedYear = Integer.parseInt(yearCombo.getSelectedItem().toString());

        String selectedCourseCode = (String) subjectCombo.getSelectedItem();
        if (selectedCourseCode == null) return; // no course selected

        int courseId = courseMap.get(selectedCourseCode);

        // 2) fetch attendance data for that month/year/course from DB
        Map<Integer, String> attendanceMap = fetchAttendanceMap(studentId, courseId, selectedYear, selectedMonth);

        // 3) day headers (Mon..Sun)
        createDayHeaders();

        LocalDate firstDayOfMonth = LocalDate.of(selectedYear, selectedMonth, 1);
        int daysInMonth = firstDayOfMonth.lengthOfMonth();

        // dayOfWeek in Java: Monday=1 .. Sunday=7
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        // We'll place day 1 at row=1, col=(startDayOfWeek-1)
        int row = 1;
        int col = startDayOfWeek - 1;

        // Reset counters
        presentCount = 0;
        absentCount = 0;
        unmarkedCount = 0;

        // 4) Build day squares
        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

            // Check if we have attendance for this day
            String status = attendanceMap.get(day); // "present" or "absent" or null

            // Sunday (col=6) - you might handle differently if you want Sunday off, etc.
            if (col == 6) {
                // Mark Sunday differently if desired
                dayLabel.setBackground(Color.BLACK);
                dayLabel.setForeground(Color.WHITE);
            } else {
                if ("present".equalsIgnoreCase(status)) {
                    dayLabel.setBackground(Color.GREEN);
                    presentCount++;
                } else if ("absent".equalsIgnoreCase(status)) {
                    dayLabel.setBackground(Color.RED);
                    absentCount++;
                } else {
                    // No record for that day => unmarked
                    dayLabel.setBackground(Color.BLACK);
                    dayLabel.setForeground(Color.WHITE);
                    unmarkedCount++;
                }
            }

            dayLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    dayLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dayLabel.setBorder(null);
                }
            });

            // Positioning in the panel
            int xStart = 20, yStart = 20;
            int cellWidth = 50, cellHeight = 40;
            int spacingX = 10, spacingY = 10;

            int x = xStart + col * (cellWidth + spacingX);
            int y = yStart + row * (cellHeight + spacingY);

            dayLabel.setBounds(x, y, cellWidth, cellHeight);
            calendarPanel.add(dayLabel);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        // 5) Update the bottom panel counts
        updateBottomPanel();
    }

    /**
     * Queries the attendance table for all records in the given month/year
     * for the given student & course. Returns a map of (day -> "present"/"absent").
     */
    private Map<Integer, String> fetchAttendanceMap(int studentId, int courseId, int year, int month) {
        Map<Integer, String> map = new HashMap<>();
        String query = "SELECT DAY(date) AS dayNum, status " +
                "FROM attendances " +
                "WHERE student_id = ? AND course_id = ? " +
                "  AND YEAR(date) = ? AND MONTH(date) = ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setInt(3, year);
            ps.setInt(4, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("dayNum");
                    String status = rs.getString("status"); // "present" or "absent"
                    map.put(day, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Creates the weekday header row (MON..SUN).
     */
    private void createDayHeaders() {
        String[] dayNames = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

        int xStart = 20, yStart = 20;
        int cellWidth = 50, cellHeight = 40;
        int spacingX = 10;

        for (int col = 0; col < 7; col++) {
            JLabel headerLabel = new JLabel(dayNames[col], SwingConstants.CENTER);
            headerLabel.setOpaque(true);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            headerLabel.setBackground(new Color(230, 230, 230));
            headerLabel.setBounds(
                    xStart + col * (cellWidth + spacingX),
                    yStart,
                    cellWidth,
                    cellHeight
            );
            if (col == 6) {
                headerLabel.setBackground(Color.BLACK);
                headerLabel.setForeground(Color.WHITE);
            }
            calendarPanel.add(headerLabel);
        }
    }

    /**
     * Updates the labels showing total working days, presents, absents.
     * Also sets the `attendancePercentage` so we can animate the progress arc.
     */
    private void updateBottomPanel() {
        int totalWorking = presentCount + absentCount;
        totalWorkingDaysLabel.setText("TOTAL WORKING DAYS: " + totalWorking);
        presentLabel.setText("Presents: " + presentCount);
        absentLabel.setText("Absents: " + absentCount);

        // If you want to define unmarked as non-working or whatever,
        // you could do a different formula. This example uses totalWorking:
        if (totalWorking > 0) {
            attendancePercentage = (int)((presentCount * 100.0) / totalWorking);
        } else {
            attendancePercentage = 0;
        }
    }

    /**
     * Animates the circular arc from 0 up to attendancePercentage.
     */
    private void animateAttendanceArc() {
        progress = 0; // reset to 0 each time
        Timer timer = new Timer(20, e -> {
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
        // Suppose we pass a known studentId (e.g. 1) after login
        SwingUtilities.invokeLater(() -> {
            new StudentCalendar(1).setVisible(true);
        });
    }
}