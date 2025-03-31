package pages.faculty;

import db.DatabaseConnection;
import pages.student.AttendanceReportPage;
import pages.student.StudentCalendar;
import pages.student.StudentDashboard;
import pages.student.StudentLeaveApplicationPage;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FacultyCalendar extends JFrame {
    // Options for the sidebar
    private final String[] options = {"Dashboard", "Leave Requests", "Mark Attendance", "Student Attendance Report"};
    private final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    private final String[] semesters = {"Semester 1", "Semester 2", "Semester 3", "Semester 4"};
    // regdNos are loaded dynamically later.

    // UI Components
    private JPanel calendarPanel;
    private JLabel[] dayLabels;
    private int presentCount, absentCount, unmarkedCount;

    private JPanel progressPanel;
    private int attendancePercentage = 59;
    private int progress = 0;

    private JLabel totalWorkingDaysLabel;
    private JLabel presentLabel;
    private JLabel absentLabel;

    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> regdNoCombo;
    private JComboBox<String> subjectCombo;

    // Map for faculty's courses (courseCode -> courseId)
    private Map<String, Integer> courseMap = new HashMap<>();

    // Faculty info
    private int userId;   // Faculty user id
    private String facultyName = "Unknown";

    public FacultyCalendar(int userId) {
        this.userId = userId;
        loadFacultyInfo();       // Load the faculty name
        loadFacultyCourses();    // Load courses taught by the faculty
        initUI();
        rebuildCalendar();
    }

    /**
     * Loads the faculty's name from the database based on userId.
     */
    private void loadFacultyInfo() {
        String query = "SELECT name FROM faculty WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    facultyName = rs.getString("name");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads the courses taught by this faculty and stores them in courseMap.
     */
    private void loadFacultyCourses() {
        String query = "SELECT c.id, c.code " +
                "FROM faculty f " +
                "JOIN faculty_courses fc ON f.id = fc.faculty_id " +
                "JOIN courses c ON fc.course_id = c.id " +
                "WHERE f.user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cId = rs.getInt("id");
                    String code = rs.getString("code");
                    courseMap.put(code, cId);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Dynamically loads registration numbers of students who are enrolled in courses taught by this faculty.
     */
    private String[] loadStudentRegNos() {
        ArrayList<String> regNoList = new ArrayList<>();
        String query = "SELECT DISTINCT s.reg_no " +
                "FROM faculty f " +
                "JOIN faculty_courses fc ON f.id = fc.faculty_id " +
                "JOIN student_courses sc ON fc.course_id = sc.course_id " +
                "JOIN students s ON sc.student_id = s.id " +
                "WHERE f.user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    regNoList.add(rs.getString("reg_no"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return regNoList.toArray(new String[regNoList.size()]);
    }

    private void initUI() {
        setTitle("Faculty Calendar");
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

        JPanel sidebar = createSidebar();
        sidebar.setBounds(0, 0, 200, getHeight());
        backgroundPanel.add(sidebar);

        JPanel topPanel = new JPanel(null);
        topPanel.setBounds(210, 20, 760, 50);
        topPanel.setBackground(new Color(255, 255, 255, 80));
        backgroundPanel.add(topPanel);

        monthCombo = new JComboBox<>(months);
        monthCombo.setBounds(20, 10, 120, 30);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        topPanel.add(monthCombo);

        yearCombo = new JComboBox<>(new String[]{"2022", "2023", "2024", "2025", "2026"});
        yearCombo.setBounds(160, 10, 100, 30);
        // Set default year to current if present.
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < yearCombo.getItemCount(); i++) {
            if (String.valueOf(currentYear).equals(yearCombo.getItemAt(i))) {
                yearCombo.setSelectedIndex(i);
                break;
            }
        }
        topPanel.add(yearCombo);

        // Load registration numbers dynamically.
        regdNoCombo = new JComboBox<>(loadStudentRegNos());
        regdNoCombo.setBounds(280, 10, 120, 30);
        topPanel.add(regdNoCombo);

        // Populate subject combo using courses taught by the faculty.
        subjectCombo = new JComboBox<>(courseMap.keySet().toArray(new String[0]));
        subjectCombo.setBounds(420, 10, 120, 30);
        topPanel.add(subjectCombo);

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBounds(620, 10, 100, 30);
        topPanel.add(logoutBtn);

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

        dayLabels = new JLabel[42];

        progressPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(10));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(10, 10, 120, 120);
                g2.setColor(new Color(46, 204, 113));
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

        JPanel attPercentPanel = new JPanel(null);
        attPercentPanel.setBackground(new Color(255, 255, 255, 80));
        attPercentPanel.setBounds(730, 270, 180, 40);
        JLabel attPercentLabel = new JLabel("ATTENDANCE PERCENTAGE", SwingConstants.CENTER);
        attPercentLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        attPercentLabel.setBounds(0, 0, 180, 40);
        attPercentPanel.add(attPercentLabel);
        backgroundPanel.add(attPercentPanel);

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

        // Listeners to rebuild calendar when month/year/subject/reg number changes.
        monthCombo.addActionListener(e -> rebuildCalendar());
        yearCombo.addActionListener(e -> rebuildCalendar());
        subjectCombo.addActionListener(e -> rebuildCalendar());
        regdNoCombo.addActionListener(e -> rebuildCalendar());
    }

    private void rebuildCalendar() {
        calendarPanel.removeAll();
        buildDynamicCalendar();
        calendarPanel.revalidate();
        calendarPanel.repaint();
        updateBottomPanel();
        animateAttendanceArc();
    }

    /**
     * Builds the day headers and day cells using attendance data from DB.
     */
    private void buildDynamicCalendar() {
        int selectedMonth = monthCombo.getSelectedIndex() + 1;
        int selectedYear = Integer.parseInt(yearCombo.getSelectedItem().toString());

        String selectedCourseCode = (String) subjectCombo.getSelectedItem();
        if (selectedCourseCode == null) return;
        int courseId = courseMap.get(selectedCourseCode);

        // Get the student id from the selected registration number.
        String selectedRegNo = (String) regdNoCombo.getSelectedItem();
        int studentIdFromRegNo = getStudentIdFromRegNo(selectedRegNo);

        // Fetch attendance map for that student, course, month, and year.
        Map<Integer, String> attendanceMap = fetchAttendanceMap(studentIdFromRegNo, courseId, selectedYear, selectedMonth);

        createDayHeaders();

        LocalDate firstDayOfMonth = LocalDate.of(selectedYear, selectedMonth, 1);
        int daysInMonth = firstDayOfMonth.lengthOfMonth();
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // Monday=1...Sunday=7
        int row = 1;
        int col = startDayOfWeek - 1;

        // Reset counters.
        presentCount = 0;
        absentCount = 0;
        unmarkedCount = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

            String status = attendanceMap.get(day); // "present", "absent", or null

            if (col == 6) { // Sunday
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
    }

    /**
     * Queries the attendances table for the given student, course, month, and year.
     * Returns a map of day number to attendance status ("present"/"absent").
     */
    private Map<Integer, String> fetchAttendanceMap(int studentId, int courseId, int year, int month) {
        Map<Integer, String> map = new HashMap<>();
        String query = "SELECT DAY(date) AS dayNum, status " +
                "FROM attendances " +
                "WHERE student_id = ? AND course_id = ? AND YEAR(date) = ? AND MONTH(date) = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setInt(3, year);
            ps.setInt(4, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("dayNum");
                    String status = rs.getString("status");
                    map.put(day, status);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    /**
     * Creates the weekday header row.
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
            headerLabel.setBounds(xStart + col * (cellWidth + spacingX), yStart, cellWidth, cellHeight);
            if (col == 6) {
                headerLabel.setBackground(Color.BLACK);
                headerLabel.setForeground(Color.WHITE);
            }
            calendarPanel.add(headerLabel);
        }
    }

    /**
     * Updates the bottom panel with attendance counts and recalculates the attendance percentage.
     */
    private void updateBottomPanel() {
        int totalWorking = presentCount + absentCount;
        totalWorkingDaysLabel.setText("TOTAL WORKING DAYS: " + totalWorking);
        presentLabel.setText("Presents: " + presentCount);
        absentLabel.setText("Absents: " + absentCount);
        if (totalWorking > 0) {
            attendancePercentage = (presentCount * 100) / totalWorking;
        } else {
            attendancePercentage = 0;
        }
    }

    /**
     * Animates the circular progress arc from 0 to attendancePercentage.
     */
    private Timer progressTimer;
    private void animateAttendanceArc() {
        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.stop();
        }
        progress = 0;
        progressPanel.repaint();
        if (attendancePercentage == 0) {
            return;
        }
        progressTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (progress < attendancePercentage) {
                    progress++;
                    progressPanel.repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        progressTimer.start();
    }

    /**
     * Given a registration number, queries the students table to get the student id.
     */
    private int getStudentIdFromRegNo(String regNo) {
        int id = 0;
        String query = "SELECT id FROM students WHERE reg_no = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }

    // --- Helper methods for Sidebar and Header ---
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(51, 51, 51));

        JPanel userPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.LIGHT_GRAY);
                int d = 60;
                int x = (getWidth() - d) / 2;
                int y = 20;
                g2.fillOval(x, y, d, d);
            }
        };
        userPanel.setBounds(0, 0, 200, 150);
        userPanel.setBackground(new Color(51, 51, 51));

        // Set faculty name as the username
        JLabel usernameLabel = new JLabel(facultyName, SwingConstants.CENTER);
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

//        for (int i = 0; i < options.length; i++) {
//            JLabel optionLabel = new JLabel(options[i], SwingConstants.CENTER);
//            optionLabel.setForeground(Color.WHITE);
//            optionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
//            optionLabel.setOpaque(true);
//            optionLabel.setBackground(new Color(51, 51, 51));
//            optionLabel.setBounds(0, 160 + i * 50, 200, 40);
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
//                    if (optionLabel.getText().equals(options[0])) {
//                        SwingUtilities.invokeLater(() -> {
//                            new FacultyDashboard(userId).setVisible(true);
//                        });
//                    }
//                }
//            });
//            sidebar.add(optionLabel);
//        }

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
                    new FacultyDashboard(userId).setVisible(true);
                    System.out.println(userId);
                });

            }
        });
        sidebar.add(optionDashboardLabel);

        JLabel optionLeaveRequestPermissionLabel=new JLabel(options[1], SwingConstants.CENTER);
        optionLeaveRequestPermissionLabel.setForeground(Color.WHITE);
        optionLeaveRequestPermissionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionLeaveRequestPermissionLabel.setOpaque(true);
        optionLeaveRequestPermissionLabel.setBackground(new Color(51, 51, 51));
        optionLeaveRequestPermissionLabel.setBounds(0, 160+1*50, 200, 40);

        optionLeaveRequestPermissionLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                optionLeaveRequestPermissionLabel.setBackground(new Color(70, 70, 70));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                optionLeaveRequestPermissionLabel.setBackground(new Color(51, 51, 51));
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JOptionPane.showMessageDialog(null, optionLeaveRequestPermissionLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() -> {
                    new FacultyLeaveRequestPermission(userId).setVisible(true);
                });

            }
        });
        sidebar.add(optionLeaveRequestPermissionLabel);

        JLabel optionAttendanceMarkingLabel=new JLabel(options[2], SwingConstants.CENTER);
        optionAttendanceMarkingLabel.setForeground(Color.WHITE);
        optionAttendanceMarkingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionAttendanceMarkingLabel.setOpaque(true);
        optionAttendanceMarkingLabel.setBackground(new Color(51, 51, 51));
        optionAttendanceMarkingLabel.setBounds(0, 160+2*50, 200, 40);

        optionAttendanceMarkingLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                optionAttendanceMarkingLabel.setBackground(new Color(70, 70, 70));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                optionAttendanceMarkingLabel.setBackground(new Color(51, 51, 51));
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JOptionPane.showMessageDialog(null, optionAttendanceMarkingLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() ->
                {
                    new StudentAttendanceMarkingPage(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionAttendanceMarkingLabel);

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
                    new FacultyCalendar(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);

        return sidebar;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBackground(new Color(255, 255, 255, 80));
        JLabel titleLabel = new JLabel("Faculty Calendar");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(20, 30, 300, 40);
        headerPanel.add(titleLabel);

        JButton exportBtn = new JButton("Export as PDF");
        styleHeaderButton(exportBtn);
        exportBtn.setBounds(500, 35, 140, 30);
        headerPanel.add(exportBtn);

        JButton logoutBtn = new JButton("LOGOUT");
        styleHeaderButton(logoutBtn);
        logoutBtn.setBounds(650, 35, 100, 30);
        addButtonHoverGradient(logoutBtn);
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(new Color(255, 69, 0));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(0, 123, 255));
            }
        });
        headerPanel.add(logoutBtn);

        return headerPanel;
    }

    private void styleHeaderButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void addButtonHoverGradient(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200, 200, 200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255));
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyCalendar(1).setVisible(true);
        });
    }
}
