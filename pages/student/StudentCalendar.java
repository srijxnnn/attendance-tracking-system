package pages.student;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import pages.auth.UserAuthentication;

public class StudentCalendar extends JFrame {

    private final String[] options = {"Dashboard", "Leave Application", "Student Calendar", "Attendance Report"};

    private Map<String, Integer> courseMap = new HashMap<>();
    private final String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
    private final String[] years = {"2022", "2023", "2024", "2025", "2026"};

    private JPanel calendarPanel;
    private JLabel totalWorkingDaysLabel;
    private JLabel presentLabel;
    private JLabel absentLabel;
    private JPanel progressPanel;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JComboBox<String> subjectCombo;

    private int presentCount, absentCount, unmarkedCount;

    private int attendancePercentage = 0; 
    private int progress = 0;

    private int userId;
    private int studentId;
    private String studentName = "Unknown"; 

    public StudentCalendar(int userId) {
        this.userId = userId;
        loadStudentInfo();     
        initUI();
        rebuildCalendar();   
    }

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

            String infoQuery = "SELECT name FROM students WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(infoQuery)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        studentName = rs.getString("name");
                    }
                }
            }

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
                    StudentCalendar.this.dispose();
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
                SwingUtilities.invokeLater(() -> {
                    StudentCalendar.this.dispose();
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
                    StudentCalendar.this.dispose();
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
                    StudentCalendar.this.dispose();
                    new AttendanceReportPage(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);

        // Top panel
        JPanel topPanel=new JPanel(null);
        topPanel.setBounds(210, 20, 760, 50);
        topPanel.setBackground(new Color(255, 255, 255, 80));
        backgroundPanel.add(topPanel);

        monthCombo=new JComboBox<>(months);
        monthCombo.setBounds(20, 10, 120, 30);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1); 
        topPanel.add(monthCombo);

        yearCombo=new JComboBox<>(years);
        yearCombo.setBounds(160, 10, 100, 30);
        int currentYear=LocalDate.now().getYear();
        for(int i=0;i<years.length;i++) {
            if(String.valueOf(currentYear).equals(years[i])) 
            {
                yearCombo.setSelectedIndex(i);
                break;
            }
        }
        topPanel.add(yearCombo);

        subjectCombo=new JComboBox<>(courseMap.keySet().toArray(new String[0]));
        subjectCombo.setBounds(280, 10, 120, 30);
        topPanel.add(subjectCombo);

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                StudentCalendar.this.dispose();
                new UserAuthentication().setVisible(true);
            }
        });
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
                String text=progress+"%";
                FontMetrics fm=g2.getFontMetrics();
                int textWidth=fm.stringWidth(text);
                int x=(getWidth()-textWidth)/2;
                int y=getHeight()/2+fm.getHeight()/4;
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

        monthCombo.addActionListener(e -> rebuildCalendar());
        yearCombo.addActionListener(e -> rebuildCalendar());
        subjectCombo.addActionListener(e -> rebuildCalendar());
    }


    private void rebuildCalendar() {
        calendarPanel.removeAll();

        buildDynamicCalendar();

        animateAttendanceArc();

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }


    private void buildDynamicCalendar() 
    {
        int selectedMonth=monthCombo.getSelectedIndex() + 1;
        int selectedYear=Integer.parseInt(yearCombo.getSelectedItem().toString());

        String selectedCourseCode=(String) subjectCombo.getSelectedItem();
        if(selectedCourseCode==null) return;

        int courseId=courseMap.get(selectedCourseCode);

        Map<Integer, String> attendanceMap=fetchAttendanceMap(studentId, courseId, selectedYear, selectedMonth);

        createDayHeaders();

        LocalDate firstDayOfMonth=LocalDate.of(selectedYear, selectedMonth, 1);
        int daysInMonth=firstDayOfMonth.lengthOfMonth();

        int startDayOfWeek=firstDayOfMonth.getDayOfWeek().getValue();
        int row=1;
        int col=startDayOfWeek-1;


        presentCount=0;
        absentCount=0;
        unmarkedCount=0;

        for (int day=1;day<=daysInMonth;day++) 
        {
            JLabel dayLabel=new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

            String status=attendanceMap.get(day); 

            if(col==6) 
            {
                dayLabel.setBackground(Color.BLACK);
                dayLabel.setForeground(Color.WHITE);
            } 
            else 
            {
                if ("present".equalsIgnoreCase(status)) 
                {
                    dayLabel.setBackground(Color.GREEN);
                    presentCount++;
                } 
                else if("absent".equalsIgnoreCase(status)) 
                {
                    dayLabel.setBackground(Color.RED);
                    absentCount++;
                } 
                else 
                {
                    dayLabel.setBackground(Color.BLACK);
                    dayLabel.setForeground(Color.WHITE);
                    unmarkedCount++;
                }
            }

            dayLabel.addMouseListener(new MouseAdapter() 
            {
                @Override
                public void mouseEntered(MouseEvent e) 
                {
                    dayLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }

                @Override
                public void mouseExited(MouseEvent e) 
                {
                    dayLabel.setBorder(null);
                }
            });

            int xStart=20, yStart=20;
            int cellWidth=50, cellHeight=40;
            int spacingX=10, spacingY=10;

            int x=xStart+col*(cellWidth+spacingX);
            int y=yStart+row*(cellHeight+spacingY);

            dayLabel.setBounds(x, y, cellWidth, cellHeight);
            calendarPanel.add(dayLabel);

            col++;
            if(col==7) 
            {
                col=0;
                row++;
            }
        }
        updateBottomPanel();
    }

    private Map<Integer, String> fetchAttendanceMap(int studentId, int courseId, int year, int month) 
    {
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

            try(ResultSet rs=ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    int day=rs.getInt("dayNum");
                    String status=rs.getString("status");
                    map.put(day, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void createDayHeaders() 
    {
        String[] dayNames={"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

        int xStart=20, yStart=20;
        int cellWidth=50, cellHeight=40;
        int spacingX=10;

        for(int col=0;col<7;col++) 
        {
            JLabel headerLabel=new JLabel(dayNames[col], SwingConstants.CENTER);
            headerLabel.setOpaque(true);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            headerLabel.setBackground(new Color(230, 230, 230));
            headerLabel.setBounds(
                    xStart+col*(cellWidth+spacingX),
                    yStart,
                    cellWidth,
                    cellHeight
            );
            if(col==6) 
            {
                headerLabel.setBackground(Color.BLACK);
                headerLabel.setForeground(Color.WHITE);
            }
            calendarPanel.add(headerLabel);
        }
    }

    private void updateBottomPanel() {
        int totalWorking = presentCount + absentCount;
        totalWorkingDaysLabel.setText("TOTAL WORKING DAYS: " + totalWorking);
        presentLabel.setText("Presents: " + presentCount);
        absentLabel.setText("Absents: " + absentCount);

        if (totalWorking > 0) {
            attendancePercentage = (int)((presentCount * 100.0) / totalWorking);
        } else {
            attendancePercentage = 0;
        }
    }

    private void animateAttendanceArc() {
        progress = 0; 
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
        SwingUtilities.invokeLater(() -> {
            new StudentCalendar(1).setVisible(true);
        });
    }
}