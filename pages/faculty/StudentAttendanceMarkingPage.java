package pages.faculty;
 
 import db.DatabaseConnection;
 import java.awt.*;
 import java.awt.event.*;
 import java.awt.geom.RoundRectangle2D;
 import java.sql.*;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.time.LocalDate;
 import java.time.format.DateTimeFormatter;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import javax.swing.*;
 import javax.swing.plaf.basic.BasicScrollBarUI;
 import pages.auth.UserAuthentication;

 public class StudentAttendanceMarkingPage extends JFrame 
 { 
    private final String[] options={"Dashboard", "Leave Request", "Mark Attendance", "Student Attendance Report"};
    private final String[] semesters={"Semester 1", "Semester 2", "Semester 3", "Semester 4",
                                        "Semester 5", "Semester 6", "Semester 7", "Semester 8"};
    private final String[] dates;
    private final String[] subjects;

    private JComboBox<String> semesterCombo;
    private JComboBox<String> dateCombo;
    private JComboBox<String> subjectCombo;

    private final List<StudentData> studentList=new ArrayList<>();
    private JPanel contentPanel;

    int userID;
    public StudentAttendanceMarkingPage(int userID) 
    {
        this.userID = userID;
        subjects = getSubjectCodes(userID);
        List<String> recentDates = getRecent7Days();
        dates = recentDates.toArray(new String[0]);
        setTitle("Student Attendance Marking");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        JPanel sidebar = createSidebar();
        sidebar.setBounds(0, 0, 200, 1000);
        backgroundPanel.add(sidebar);

        JPanel topPanel = createTopPanel();
        topPanel.setBounds(210, 20, 760, 50);
        backgroundPanel.add(topPanel);

        contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        contentPanel.setPreferredSize(new Dimension(760, 600));
        refreshTableData(new ArrayList<>());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBounds(210, 90, 760, 560);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() 
        {
            @Override
            protected void configureScrollBarColors() 
            {}

            @Override
            protected JButton createDecreaseButton(int orientation) 
            { 
                return zeroButton(); 
            }

            @Override
            protected JButton createIncreaseButton(int orientation) 
            { 
                return zeroButton(); 
            }
            private JButton zeroButton() 
            {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                return btn;
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) 
            {}

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) 
            {}

        });

        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() 
        {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) 
            {}

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) 
            {}
        });
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        backgroundPanel.add(scrollPane);
    }

    private void refreshTableData(List<StudentData> newData) 
    {
        contentPanel.removeAll(); 
        TableHeaderRow headerRow = new TableHeaderRow();
        headerRow.setBounds(0, 0, 760, 50);
        contentPanel.add(headerRow);

        int startY = 60;
        for (StudentData student : newData) 
        {
            TableDataRow dataRow = new TableDataRow(student);
            dataRow.setBounds(0, startY, 760, 50);
            contentPanel.add(dataRow);
            startY += 60;
        }
        
        JButton markBtn = new JButton("Mark Attendance");
        markBtn.setBackground(Color.BLUE);
        markBtn.setForeground(Color.WHITE);
        markBtn.setFocusPainted(false);
        markBtn.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                markBtn.setBackground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                markBtn.setBackground(Color.BLUE);
            }
        });
        markBtn.addActionListener(e -> {
            String selectedDate = (String) dateCombo.getSelectedItem();
            System.out.println("Selected Date: " + selectedDate);

            String selectedSubjectCode=(String)subjectCombo.getSelectedItem();
            int course_id=getCourseId(selectedSubjectCode);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date=null;
            try{
                date=sdf.parse(selectedDate);
                System.out.println("Converted Date: " + date);
            } 
            catch(ParseException except) 
            {
                except.printStackTrace();
            }

            if(checkDates(date, course_id))
            {
                Component[] comps = contentPanel.getComponents();
                for(Component comp : comps) 
                {
                    if(comp instanceof TableDataRow) 
                    {
                        TableDataRow row = (TableDataRow) comp;
                        int student_id=getStudentId(row.getRegdNo());

                        System.out.println("Regd No: " + row.getRegdNo() +
                                        ", Name: " + row.getName() +
                                        ", Attendance: " + row.getAttendance());

                        UpdateAttendance(student_id, date, course_id, row.getAttendance().toLowerCase());
                        
                    }
                }
                JOptionPane.showMessageDialog(null, "Attendance Updated Successfully!");
            }
            else
            {
                Component[] comps = contentPanel.getComponents();
                for(Component comp : comps) 
                {
                    if(comp instanceof TableDataRow) 
                    {
                        TableDataRow row=(TableDataRow) comp;
                        int student_id=getStudentId(row.getRegdNo());

                        System.out.println("Regd No: " + row.getRegdNo() +
                                        ", Name: " + row.getName() +
                                        ", Attendance: " + row.getAttendance());

                        SetAttendance(student_id, date, course_id, row.getAttendance().toLowerCase());
                        
                    }

                }
                JOptionPane.showMessageDialog(null, "Attendance Marked Successfully!");
            }


            
        });
        int buttonWidth = 150;
        int buttonHeight = 40;
        int buttonX = (760 - buttonWidth) / 2;
        int buttonY = startY + 50;
        markBtn.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        contentPanel.add(markBtn);
        contentPanel.setPreferredSize(new Dimension(760, buttonY + buttonHeight + 10));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createSidebar() 
    {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(51, 51, 51));
        JPanel userPanel = new JPanel(null) 
        {
            private Image profileImage;

            {
                profileImage = new ImageIcon("pages/profile-circle-border.png").getImage();
            }

            @Override
            protected void paintComponent(Graphics g) 
            {
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

        JLabel usernameLabel = new JLabel("Faculty Name", SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setBounds(0, 90, 200, 20);

        JLabel editLabel = new JLabel("Edit Profile", SwingConstants.CENTER);
        editLabel.setForeground(new Color(200, 200, 200));
        editLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        editLabel.setBounds(0, 115, 200, 20);

        editLabel.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                editLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) 
            {
                editLabel.setForeground(new Color(200, 200, 200));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> new FacultyEditProfile(userID).setVisible(true));
            }
        });
        userPanel.add(usernameLabel);
        userPanel.add(editLabel);
        sidebar.add(userPanel);

        JLabel optionDashboardLabel = new JLabel(options[0], SwingConstants.CENTER);
        optionDashboardLabel.setForeground(Color.WHITE);
        optionDashboardLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionDashboardLabel.setOpaque(true);
        optionDashboardLabel.setBackground(new Color(51, 51, 51));
        optionDashboardLabel.setBounds(0, 160, 200, 40);
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
                StudentAttendanceMarkingPage.this.dispose();
                SwingUtilities.invokeLater(() -> new FacultyDashboard(userID).setVisible(true));
            }
        });
        sidebar.add(optionDashboardLabel);
        JLabel optionLeaveRequestLabel = new JLabel(options[1], SwingConstants.CENTER);
        optionLeaveRequestLabel.setForeground(Color.WHITE);
        optionLeaveRequestLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionLeaveRequestLabel.setOpaque(true);
        optionLeaveRequestLabel.setBackground(new Color(51, 51, 51));

        optionLeaveRequestLabel.setBounds(0, 210, 200, 40);
        optionLeaveRequestLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                optionLeaveRequestLabel.setBackground(new Color(70, 70, 70));
            }
            @Override
            public void mouseExited(MouseEvent e) 
            {
                optionLeaveRequestLabel.setBackground(new Color(51, 51, 51));
            }
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                StudentAttendanceMarkingPage.this.dispose();
                SwingUtilities.invokeLater(() -> new FacultyLeaveRequestPermission(userID).setVisible(true));
            }
        });
        sidebar.add(optionLeaveRequestLabel);
        JLabel optionMarkAttendanceLabel = new JLabel(options[2], SwingConstants.CENTER);
        optionMarkAttendanceLabel.setForeground(Color.WHITE);
        optionMarkAttendanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionMarkAttendanceLabel.setOpaque(true);
        optionMarkAttendanceLabel.setBackground(new Color(51, 51, 51));
        optionMarkAttendanceLabel.setBounds(0, 260, 200, 40);
        optionMarkAttendanceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                optionMarkAttendanceLabel.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) 
            {
                optionMarkAttendanceLabel.setBackground(new Color(51, 51, 51));
            }

            @Override
            public void mouseClicked(MouseEvent e) 
            {
                StudentAttendanceMarkingPage.this.dispose();
                SwingUtilities.invokeLater(() -> new StudentAttendanceMarkingPage(userID).setVisible(true));
            }
        });
        sidebar.add(optionMarkAttendanceLabel);

        JLabel optionStudentAttendanceReportLabel = new JLabel(options[3], SwingConstants.CENTER);
        optionStudentAttendanceReportLabel.setForeground(Color.WHITE);
        optionStudentAttendanceReportLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionStudentAttendanceReportLabel.setOpaque(true);
        optionStudentAttendanceReportLabel.setBackground(new Color(51, 51, 51));
        optionStudentAttendanceReportLabel.setBounds(0, 310, 200, 40);
        optionStudentAttendanceReportLabel.addMouseListener(new MouseAdapter() {
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
                StudentAttendanceMarkingPage.this.dispose();
                SwingUtilities.invokeLater(() -> new FacultyCalendar(userID).setVisible(true));
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);
 
        return sidebar;
    }
    private JPanel createTopPanel() 
    {
        JPanel topPanel = new JPanel(null) 
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setBackground(new Color(255, 255, 255, 80));
        semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setBounds(20, 10, 100, 30);
        topPanel.add(semesterCombo);

        dateCombo = new JComboBox<>(dates);
        dateCombo.setBounds(130, 10, 100, 30);
        topPanel.add(dateCombo);

        subjectCombo = new JComboBox<>(subjects);
        subjectCombo.setBounds(240, 10, 120, 30);
        topPanel.add(subjectCombo);

        ActionListener selectionListener = e -> {
            String selectedSemester = (String) semesterCombo.getSelectedItem();
            String selectedDate = (String) dateCombo.getSelectedItem();
            String selectedSubject = (String) subjectCombo.getSelectedItem();

            int course_id=getCourseId(selectedSubject);
            System.out.println("Selected Semester: " + selectedSemester);
            System.out.println("Selected Date: " + selectedDate);
            System.out.println("Selected Subject: " + selectedSubject);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date=null;
            try {
                date = sdf.parse(selectedDate);
                System.out.println("Converted Date: " + date);
            } 
            catch(ParseException except) 
            {
                except.printStackTrace();
            }

            if(checkDates(date, course_id))
            {
                System.out.println("in");
                List<StudentData> oldStudentData = fetchAttendanceData(date, course_id);
                studentList.clear();
                studentList.addAll(oldStudentData);
                refreshTableData(studentList);
            }
            else
            {
                List<StudentData> newStudentData = fetchStudentData(selectedSubject);
                
                studentList.clear();
                studentList.addAll(newStudentData);
                refreshTableData(studentList);
            }
        };

        semesterCombo.addActionListener(selectionListener);
        dateCombo.addActionListener(selectionListener);
        subjectCombo.addActionListener(selectionListener);

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                StudentAttendanceMarkingPage.this.dispose();
                new UserAuthentication().setVisible(true);
            }
        });
        logoutBtn.setBounds(600, 10, 100, 30);
        styleHeaderButton(logoutBtn);
        addButtonHoverGradient(logoutBtn);
        topPanel.add(logoutBtn);

        return topPanel;
    }

    private void styleHeaderButton(JButton button) 
    {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    private void addButtonHoverGradient(JButton button) 
    {
        button.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                button.setBackground(new Color(200, 0, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255));
            }
        });
    }

    private class TableHeaderRow extends JPanel 
    {
        public TableHeaderRow() 
        {
            setLayout(null);
            setOpaque(false);
            setBackground(new Color(255, 255, 255, 200));
            setBorder(null);
        }
        @Override
        protected void paintComponent(Graphics g) 
        {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(255, 255, 255, 180));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(0, 0, w - 1, h - 1);
            g2.dispose();
            super.paintComponent(g);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(Color.BLACK);
            g.drawString("Regd. No.", 30, h / 2 + 5);
            g.drawString("Name", 280, h / 2 + 5);
            g.drawString("Attendance", 530, h / 2 + 5);
        }
    }
    private class TableDataRow extends JPanel 
    {
        private float fadeAlpha = 0f;
        private int hoverOffset = 0;
        private Timer fadeTimer, hoverTimer;
        private final StudentData data;
        private JRadioButton presentRadio;
        private JRadioButton absentRadio;
        public TableDataRow(StudentData data) 
        {
            this.data = data;
            setLayout(null);
            setOpaque(false);

            fadeTimer = new Timer(20, e -> {
                if (fadeAlpha < 1f) {
                    fadeAlpha += 0.05f;
                    repaint();
                } 
                else 
                {
                    fadeAlpha = 1f;
                    fadeTimer.stop();
                }
            });
            fadeTimer.start();
            addMouseListener(new MouseAdapter() 
            {
                @Override
                public void mouseEntered(MouseEvent e) 
                {
                    if (hoverTimer != null && hoverTimer.isRunning()) 
                    {
                        hoverTimer.stop();
                    }

                    hoverTimer = new Timer(10, ee -> {
                        if (hoverOffset > -5) {
                            hoverOffset--;
                            repaint();
                        } 
                        else 
                        {
                            hoverTimer.stop();
                        }
                    });
                    hoverTimer.start();
                }
                @Override
                public void mouseExited(MouseEvent e) 
                {
                    if (hoverTimer != null && hoverTimer.isRunning()) 
                    {
                        hoverTimer.stop();
                    }
                    hoverTimer = new Timer(10, ee -> {
                        if (hoverOffset < 0) {
                            hoverOffset++;
                            repaint();
                        } 
                        else 
                        {
                            hoverTimer.stop();
                        }
                    });
                    hoverTimer.start();
                }
            });
            presentRadio = new JRadioButton("Present");
            absentRadio = new JRadioButton("Absent");
            ButtonGroup group = new ButtonGroup();
            group.add(presentRadio);
            group.add(absentRadio);
            if (data.isPresent) {
                presentRadio.setSelected(true);
            } else {
                absentRadio.setSelected(true);
            }

            presentRadio.setBounds(500, 10, 80, 30);
            absentRadio.setBounds(600, 10, 70, 30);
            add(presentRadio);
            add(absentRadio);
        }

        @Override
        protected void paintComponent(Graphics g) 
        {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            Shape rect = new RoundRectangle2D.Float(0, hoverOffset, w, h, 20, 20);
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fill(rect);
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2));
            g2.draw(rect);
            g2.dispose();
            super.paintComponent(g);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.setColor(Color.BLACK);
            g.drawString(data.regdNo, 30, getHeight() / 2 + 5);
            g.drawString(data.name, 280, getHeight() / 2 + 5);
        }
        
        public String getRegdNo() 
        {
            return data.regdNo;
        }
        public String getName() 
        {
            return data.name;
        }
        public String getAttendance() 
        {
            return presentRadio.isSelected() ? "Present" : "Absent";
        }
    }
    private static class StudentData 
    {
        String regdNo;
        String name;
        boolean isPresent;
        public StudentData(String regdNo, String name, boolean isPresent) 
        {
            this.regdNo = regdNo;
            this.name = name;
            this.isPresent = isPresent;
        }

        @Override
        public String toString() 
        {
            return "StudentData{" +
                   "regdNo='" + regdNo + '\'' +
                   ", name='" + name + '\'' +
                   ", isPresent=" + isPresent +
                   '}';
        }
    }
    private class GradientPanel extends JPanel 
    {
        @Override
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(44, 62, 80),
                    getWidth(), getHeight(), new Color(189, 195, 199)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    private String[] getSubjectCodes(int user_id) 
    {
        List<String> subjectList = new ArrayList<>();
        String query = "SELECT c.code FROM courses AS c, faculty AS f, faculty_courses AS fc WHERE f.id = fc.faculty_id AND fc.course_id = c.id AND f.user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) 
            {
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                subjectList.add(rs.getString("code"));
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return subjectList.toArray(new String[0]);
    }

    public static List<String> getRecent7Days() 
    {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) 
        {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
        }
        return dates;
    }


    private List<StudentData> fetchStudentData(String subjectCode) 
    {
        List<StudentData> studentDataList = new ArrayList<>();
        String query = "SELECT s.roll, s.name FROM students AS s, courses AS c, student_courses AS sc WHERE s.id = sc.student_id AND sc.course_id = c.id AND c.code = ? ORDER BY s.roll ASC";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) 
            {
            pstmt.setString(1, subjectCode);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                String regdNo = rs.getString("roll");
                String name = rs.getString("name");
                studentDataList.add(new StudentData(regdNo, name, true));
            }
        } 
        catch(SQLException e) 
        {
            e.printStackTrace();
        }
        return studentDataList;
    }

    private int getStudentId(String roll) 
    {
        int studentId=0;
        String query = "SELECT id FROM students WHERE roll = ?";
        try(Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setString(1, roll);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()) 
            {
                studentId=rs.getInt("id");
            }
            return studentId;
        } 
        catch(SQLException e) 
        {
            e.printStackTrace();
        }
        return studentId;
    }

    private int getCourseId(String CourseCode) 
    {
        int courseId=0;
        String query = "SELECT id FROM courses WHERE code = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setString(1, CourseCode);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()) 
            {
                courseId=rs.getInt("id");
            }
            return courseId;
        } 
        catch(SQLException e) 
        {
            e.printStackTrace();
        }
        return courseId;
    }

    private void SetAttendance(int student_id, Date date, int course_id, String status)
    {
        String query="INSERT INTO attendances (student_id, date, course_id, status) VALUES (?, ?, ?, ?)";
        java.util.Date utilDate = date; // your java.util.Date instance
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try(Connection conn=DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {
            pstmt.setInt(1,student_id);
            pstmt.setDate(2,sqlDate);
            pstmt.setInt(3,course_id);
            pstmt.setString(4,status);

            int rowsInserted=pstmt.executeUpdate();

            if(rowsInserted>0)
            {
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean checkDates(Date date, int course_id)
    {
        String query="SELECT * FROM attendances WHERE date = ? AND course_id = ?";
        java.util.Date utilDate = date;
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try(Connection conn=DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {
            pstmt.setDate(1,sqlDate);
            pstmt.setInt(2, course_id);
            ResultSet rs=pstmt.executeQuery();

            if(rs.next())
            {
                return true;
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private List<StudentData> fetchAttendanceData(Date date, int course_id) 
    {
        List<StudentData> studentDataList = new ArrayList<>();
        java.util.Date utilDate = date;
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String query = "SELECT s.roll, s.name, a.status FROM students AS s, attendances AS a WHERE s.id=a.student_id AND a.date=? AND a.course_id=? ORDER BY s.roll ASC";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(query)) 
            {
            pstmt.setDate(1, sqlDate);
            pstmt.setInt(2, course_id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String roll=rs.getString("roll");
                String name = rs.getString("name");
                String status = rs.getString("status");
                if(status.equals("present"))
                {
                    studentDataList.add(new StudentData(roll, name, true));
                }
                else
                {
                    studentDataList.add(new StudentData(roll, name, false));
                }
                
            }
        } 
        catch(SQLException e) 
        {
            e.printStackTrace();
        }
        return studentDataList;
    }

    private void UpdateAttendance(int student_id, Date date, int course_id, String currStatus)
    {
        String query="UPDATE attendances SET student_id=?, date=?, course_id=?, status=? WHERE student_id=? AND date=? AND course_id=?";
        java.util.Date utilDate = date; // your java.util.Date instance
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try(Connection conn= DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {
            pstmt.setInt(1,student_id);
            pstmt.setDate(2,sqlDate);
            pstmt.setInt(3,course_id);
            pstmt.setString(4,currStatus);
            pstmt.setInt(5,student_id);
            pstmt.setDate(6,sqlDate);
            pstmt.setInt(7,course_id);

            int rowsInserted=pstmt.executeUpdate();

            if(rowsInserted>0)
            {
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new StudentAttendanceMarkingPage(6).setVisible(true));
    }
}