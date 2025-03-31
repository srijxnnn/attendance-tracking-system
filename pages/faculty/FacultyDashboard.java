package pages.faculty;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FacultyDashboard extends JFrame {

    private int animationProgress = 0;
    // Table data loaded dynamically from DB
    private List<TableRowData> tableData = new ArrayList<>();
    private int userId;

    // Faculty info fields
    private String facultyName = "Unknown";
    private String facultyExpertise = "";
    private String facultyDesignation = "";
    private String facultyLastSeen = "";

    public FacultyDashboard(int userId) {
        this.userId = userId;
        loadFacultyInfo();   // Load name, expertise, designation, last_seen
        loadTableData();     // Load dashboard table data from DB
        setTitle("Faculty Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPanel = new GradientPanel();
        contentPanel.setLayout(null);
        contentPanel.setPreferredSize(new Dimension(1000, 1200));

        JPanel sidebar = createSidebar();
        sidebar.setBounds(0, 0, 200, 1200);
        contentPanel.add(sidebar);

        JPanel headerPanel = createHeaderPanel();
        headerPanel.setBounds(200, 0, 800, 100);
        contentPanel.add(headerPanel);

        // InfoPanel shows faculty details.
        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setBounds(210, 110, 750, 120);
        contentPanel.add(infoPanel);

        JPanel tablePanel = createTablePanel();
        tablePanel.setBounds(210, 250, 750, 400);
        contentPanel.add(tablePanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBounds(0, 0, 1000, 700);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {}
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {}
        });
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {}
        });
        scrollPane.setBorder(null);
        add(scrollPane);

        Timer timer = new Timer(20, e -> {
            if (animationProgress < 100) {
                animationProgress++;
                contentPanel.repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();

        updateLastSeen();
    }

    /**
     * Loads the faculty's name, expertise, designation, and last_seen
     * from the database based on the userId.
     */
    private void loadFacultyInfo() {
        String query = "SELECT name, expertise, designation, last_seen FROM faculty WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    facultyName = rs.getString("name");
                    facultyExpertise = rs.getString("expertise");
                    facultyDesignation = rs.getString("designation");
                    // Retrieve last_seen as a Timestamp and convert to string.
                    Timestamp ts = rs.getTimestamp("last_seen");
                    facultyLastSeen = (ts != null) ? ts.toString() : "Unknown";
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads table data from the database.
     * For each course taught by this faculty, retrieve:
     *   - course code
     *   - semester (assumed to be a column in courses)
     *   - total number of students enrolled (from student_courses)
     */
    private void loadTableData() {
        tableData.clear();
        int facultyId = 0;
        String getFacultyQuery = "SELECT id FROM faculty WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(getFacultyQuery)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    facultyId = rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String query = "SELECT c.code, c.semester, COUNT(sc.student_id) AS total_students " +
                "FROM faculty_courses fc " +
                "JOIN courses c ON fc.course_id = c.id " +
                "LEFT JOIN student_courses sc ON c.id = sc.course_id " +
                "WHERE fc.faculty_id = ? " +
                "GROUP BY c.id, c.code, c.semester";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("code");
                    String semester = rs.getString("semester");
                    String totalStudents = rs.getString("total_students");
                    tableData.add(new TableRowData(code, semester, totalStudents));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

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

        // Use the loaded facultyName.
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
                new FacultyEditProfile(userId).setVisible(true);
            }
        });
        userPanel.add(usernameLabel);
        userPanel.add(editLabel);
        sidebar.add(userPanel);

        String[] options = {"Dashboard", "Leave Requests", "Mark Attendance", "Student Attendance Report"};
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
//                    new StudentAttendanceMarkingPage(userId).setVisible(true);
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
        JLabel titleLabel = new JLabel("Faculty Dashboard");
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

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        tablePanel.setOpaque(false);

        // Header row.
        TableRow headerRow = new TableRow("Subject Code", "Semester", "Total Students Opted", true);
        headerRow.setBounds(0, 0, 750, 40);
        tablePanel.add(headerRow);

        for (int i = 0; i < tableData.size(); i++) {
            TableRowData rowData = tableData.get(i);
            TableRow row = new TableRow(rowData.subjectCode, rowData.semester, rowData.totalStudents, false);
            row.setBounds(0, 45 + i * 45, 750, 40);
            tablePanel.add(row);
        }
        int height = 45 + tableData.size() * 45;
        tablePanel.setPreferredSize(new Dimension(750, height));
        return tablePanel;
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

    private void updateLastSeen() {
        String query = "UPDATE faculty SET last_seen = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80),
                    getWidth(), getHeight(), new Color(189, 195, 199));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private static class TableRowData {
        String subjectCode, semester, totalStudents;
        public TableRowData(String sc, String sem, String total) {
            subjectCode = sc;
            semester = sem;
            totalStudents = total;
        }
    }

    private class TableRow extends JPanel {
        private float fadeAlpha = 0f;
        private int hoverOffset = 0;
        private Timer fadeTimer, hoverTimer;
        private final boolean isHeader;
        private final String col1, col2, col3;

        public TableRow(String c1, String c2, String c3, boolean isHeader) {
            this.col1 = c1;
            this.col2 = c2;
            this.col3 = c3;
            this.isHeader = isHeader;
            setLayout(null);
            setOpaque(false);

            fadeTimer = new Timer(20, e -> {
                if (fadeAlpha < 1f) {
                    fadeAlpha += 0.05f;
                    repaint();
                } else {
                    fadeAlpha = 1f;
                    fadeTimer.stop();
                }
            });
            fadeTimer.start();

            if (!isHeader) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (hoverTimer != null && hoverTimer.isRunning()) {
                            hoverTimer.stop();
                        }
                        hoverTimer = new Timer(10, ee -> {
                            if (hoverOffset > -3) {
                                hoverOffset--;
                                repaint();
                            } else {
                                hoverTimer.stop();
                            }
                        });
                        hoverTimer.start();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hoverTimer != null && hoverTimer.isRunning()) {
                            hoverTimer.stop();
                        }
                        hoverTimer = new Timer(10, ee -> {
                            if (hoverOffset < 0) {
                                hoverOffset++;
                                repaint();
                            } else {
                                hoverTimer.stop();
                            }
                        });
                        hoverTimer.start();
                    }
                });
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            Shape rect = new RoundRectangle2D.Float(0, hoverOffset, w, h, 10, 10);
            if (isHeader) {
                g2.setColor(new Color(200, 200, 200, 200));
            } else {
                g2.setColor(new Color(255, 255, 255, 200));
            }
            g2.fill(rect);
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(2));
            g2.draw(rect);
            g2.dispose();

            super.paintComponent(g);

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", isHeader ? Font.BOLD : Font.PLAIN, 13));
            g.drawString(col1, 20, getHeight() / 2 + 4);
            g.drawString(col2, 300, getHeight() / 2 + 4);
            g.drawString(col3, 500, getHeight() / 2 + 4);
        }
    }

    // InfoPanel displays the faculty's details dynamically.
    private class InfoPanel extends JPanel {
        private float fadeAlpha = 0f;
        private Timer fadeTimer;

        public InfoPanel() {
            setOpaque(false);
            setLayout(null);
            fadeTimer = new Timer(20, e -> {
                if (fadeAlpha < 1f) {
                    fadeAlpha += 0.05f;
                    repaint();
                } else {
                    fadeAlpha = 1f;
                    fadeTimer.stop();
                }
            });
            fadeTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            Shape rect = new RoundRectangle2D.Float(0, 0, w, h, 20, 20);
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fill(rect);
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2));
            g2.draw(rect);
            g2.dispose();

            super.paintComponent(g);

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString("Designation: " + facultyDesignation, 20, 30);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString("Expertise: " + facultyExpertise, 20, 50);
            g.drawString("Last Seen: " + facultyLastSeen, 20, 70);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyDashboard(6).setVisible(true);
        });
    }
}

