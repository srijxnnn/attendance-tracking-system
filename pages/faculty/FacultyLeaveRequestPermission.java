package pages.faculty;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FacultyLeaveRequestPermission extends JFrame {

    // We'll load leave requests from the database
    private final List<LeaveRequestData> requestList = new ArrayList<>();
    private int userId;
    private String facultyName = "";

    public FacultyLeaveRequestPermission(int userId) {
        this.userId = userId;
        loadFacultyInfo();
        // Load leave requests from the database
        loadLeaveRequests();

        setTitle("Faculty Leave Requests");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Main gradient background panel (grayish gradient)
        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // SIDEBAR – fixed on left
        JPanel sidebar = createSidebar();
        sidebar.setBounds(0, 0, 200, getHeight());
        backgroundPanel.add(sidebar);

        // Header Panel
        JPanel headerPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255,255,255,80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBackground(new Color(255,255,255,80));
        headerPanel.setBounds(200, 0, 800, 60);
        backgroundPanel.add(headerPanel);

        JLabel titleLabel = new JLabel("Leave Requests");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setBounds(20, 10, 300, 40);
        headerPanel.add(titleLabel);

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setBounds(650, 15, 100, 30);
        styleHeaderButton(logoutBtn);
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(Color.RED);
                logoutBtn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(33,150,243));
                logoutBtn.setForeground(Color.WHITE);
            }
        });
        headerPanel.add(logoutBtn);

        // TABLE PANEL: Create a panel that will display leave requests.
        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(new Color(180,180,180));
        contentPanel.setPreferredSize(new Dimension(800, 600));

        // Table header row with fixed column widths: NAME(140), ROLL NO(90), REGD NO(110), SEMESTER(90), REASON(190), STATUS(110)
        int[] colWidths = {140, 90, 110, 90, 150, 180};
        TableHeaderRow headerRow = new TableHeaderRow(colWidths);
        headerRow.setBounds(0, 0, 800, 50);
        contentPanel.add(headerRow);

        int startY = 60;
        for (LeaveRequestData data : requestList) {
            TableDataRow rowPanel = new TableDataRow(data, colWidths);
            rowPanel.setBounds(0, startY, 800, rowPanel.getPreferredHeight());
            contentPanel.add(rowPanel);
            startY += rowPanel.getPreferredHeight() + 10;
        }
        contentPanel.setPreferredSize(new Dimension(800, startY));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBounds(200, 60, 800, 640);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {}
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
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
        backgroundPanel.add(scrollPane);
    }

    private JPanel createSidebar() {
        // Create the sidebar panel with a null layout.
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(51, 51, 51));

        // Create the user panel (shows profile picture and faculty name).
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
        sidebar.add(userPanel);

        // Faculty name (should be set from DB; here assumed available as facultyName).
        JLabel usernameLabel = new JLabel(facultyName, SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setBounds(0, 90, 200, 20);
        userPanel.add(usernameLabel);

        // Edit profile label.
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
        userPanel.add(editLabel);

        // Sidebar option labels.
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


    /**
     * Loads leave requests from the database for courses taught by the faculty.
     * Joins student_leaves, student_courses, courses, students, faculty_courses, and faculty.
     */
    private void loadLeaveRequests() {
        requestList.clear();
        String query = "SELECT sl.id as leave_id, s.name, s.roll, s.reg_no, c.semester, sl.reason " +
                "FROM student_leaves sl " +
                "JOIN student_courses sc ON sl.course_id = sc.course_id AND sl.student_id = sc.student_id " +
                "JOIN courses c ON sl.course_id = c.id " +
                "JOIN students s ON s.id = sl.student_id " +
                "JOIN faculty_courses fc ON c.id = fc.course_id " +
                "JOIN faculty f ON fc.faculty_id = f.id " +
                "WHERE f.user_id = ? AND sl.status = 'pending'";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int leaveId = rs.getInt("leave_id");
                    String name = rs.getString("name");
                    String roll = rs.getString("roll");
                    String regdNo = rs.getString("reg_no");
                    String semester = rs.getString("semester");
                    String reason = rs.getString("reason");
                    requestList.add(new LeaveRequestData(leaveId, name, roll, regdNo, semester, reason));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void styleHeaderButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(new Color(33,150,243));
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(15));
    }

    private void addButtonHoverGradient(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200,200,200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(33,150,243));
            }
        });
    }

    /**
     * Updates the leave request status in the database.
     */
    private void updateLeaveStatus(int leaveId, String newStatus) {
        String query = "UPDATE student_leaves SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setInt(2, leaveId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Leave request " + leaveId + " updated to " + newStatus);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

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


    // GradientPanel for background
    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, new Color(44,62,80),
                    getWidth(), getHeight(), new Color(189,195,199));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Table header row with customizable column widths.
    private class TableHeaderRow extends JPanel {
        public TableHeaderRow(int[] colWidths) {
            setLayout(null);
            setOpaque(false);
            setBackground(new Color(255,255,255,80));
            String[] headers = {"NAME", "ROLL NO", "REGD NO", "SEMESTER", "REASON", "STATUS"};
            int x = 0;
            for (int i = 0; i < headers.length; i++) {
                JLabel label = new JLabel("<html><b>" + headers[i] + "</b></html>", SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                label.setBounds(x, 0, colWidths[i], 50);
                add(label);
                x += colWidths[i];
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(255,255,255,150));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Table data row for leave request entries.
    private class TableDataRow extends JPanel {
        private int hoverOffset = 0;
        private Timer hoverTimer;
        private int rowHeight;
        public TableDataRow(LeaveRequestData data, int[] colWidths) {
            setLayout(null);
            setOpaque(false);
            // Create cell labels using HTML for wrapping text.
            String[] cellTexts = {data.name, data.roll, data.regdNo, data.semester};
            int x = 0;
            int maxHeight = 50;
            List<JLabel> cellLabels = new ArrayList<>();
            for (int i = 0; i < cellTexts.length; i++) {
                JLabel label = new JLabel("<html>" + cellTexts[i] + "</html>", SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                label.setVerticalAlignment(SwingConstants.TOP);
                label.setBounds(x, 10, colWidths[i], 50);
                Dimension pref = label.getPreferredSize();
                if (pref.height > maxHeight) {
                    maxHeight = pref.height;
                }
                cellLabels.add(label);
                x += colWidths[i];
            }
            // Use column 5 for REASON.
            JButton seeReasonBtn = new JButton("See Reason");
            seeReasonBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
            seeReasonBtn.setForeground(Color.WHITE);
            seeReasonBtn.setBackground(new Color(66,133,244));
            seeReasonBtn.setFocusPainted(false);
            seeReasonBtn.setBounds(0, 0, Math.min(100, colWidths[4]), 30);
            seeReasonBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, data.reason, "Leave Reason", JOptionPane.INFORMATION_MESSAGE);
            });
            int reasonCellHeight = 50;
            rowHeight = Math.max(maxHeight, reasonCellHeight);
            for (JLabel label : cellLabels) {
                Rectangle b = label.getBounds();
                label.setBounds(b.x, b.y, b.width, rowHeight);
                add(label);
            }
            // Add See Reason button to REASON column.
            seeReasonBtn.setBounds(x, 10, Math.min(100, colWidths[4]), 30);
            add(seeReasonBtn);
            x += colWidths[4];
            // STATUS column: add buttons for Approve and Deny.
            JPanel statusPanel = new JPanel(null);
            statusPanel.setOpaque(false);
            statusPanel.setBounds(x, 0, colWidths[5], rowHeight);

            JButton approveBtn = new JButton("Approve");
            approveBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setBackground(new Color(46,204,113));
            approveBtn.setFocusPainted(false);
            approveBtn.setBounds(0, (rowHeight - 30) / 2, 80, 30);
            addButtonHoverEffect(approveBtn, new Color(46,204,113));
            approveBtn.addActionListener(e -> {
                updateLeaveStatus(data.leaveId, "accepted");
                statusPanel.removeAll();
                JLabel statusLabel = new JLabel("<html><b>Accepted</b></html>", SwingConstants.CENTER);
                statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                statusLabel.setForeground(new Color(46,204,113));
                statusLabel.setBounds(0, 0, colWidths[5], rowHeight);
                statusPanel.add(statusLabel);
                statusPanel.revalidate();
                statusPanel.repaint();
                System.out.println("APPROVED: " + data);
            });

            JButton denyBtn = new JButton("Deny");
            denyBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            denyBtn.setForeground(Color.WHITE);
            denyBtn.setBackground(new Color(231,76,60));
            denyBtn.setFocusPainted(false);
            denyBtn.setBounds(90, (rowHeight - 30) / 2, 80, 30);
            addButtonHoverEffect(denyBtn, new Color(231,76,60));
            denyBtn.addActionListener(e -> {
                updateLeaveStatus(data.leaveId, "rejected");
                statusPanel.removeAll();
                JLabel statusLabel = new JLabel("<html><b>Denied</b></html>", SwingConstants.CENTER);
                statusLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
                statusLabel.setForeground(new Color(231,76,60));
                statusLabel.setBounds(0, 0, colWidths[5], rowHeight);
                statusPanel.add(statusLabel);
                statusPanel.revalidate();
                statusPanel.repaint();
                System.out.println("DENIED: " + data);
            });

            statusPanel.add(approveBtn);
            statusPanel.add(denyBtn);
            add(statusPanel);

            setPreferredSize(new Dimension(800, rowHeight));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (hoverTimer != null && hoverTimer.isRunning()) {
                        hoverTimer.stop();
                    }
                    hoverTimer = new Timer(10, ee -> {
                        if (hoverOffset > -8) {
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

        public int getPreferredHeight() {
            return rowHeight;
        }

        private void addButtonHoverEffect(JButton btn, Color normalColor) {
            Color hoverColor = normalColor.brighter();
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(hoverColor);
                    btn.setForeground(Color.BLACK);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(normalColor);
                    btn.setForeground(Color.WHITE);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (hoverOffset < 0) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(4, getHeight() - 4 + hoverOffset, getWidth(), 5, 15, 15);
            }
            g2.setColor(new Color(255, 255, 255, 230));
            g2.fillRect(0, hoverOffset, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Rounded border for buttons.
    private static class RoundedBorder implements Border {
        private int radius;
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(c.getBackground());
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // Data class for leave request row data.
    private static class LeaveRequestData {
        int leaveId;
        String name, roll, regdNo, semester, reason;
        public LeaveRequestData(int leaveId, String name, String roll, String regdNo, String semester, String reason) {
            this.leaveId = leaveId;
            this.name = name;
            this.roll = roll;
            this.regdNo = regdNo;
            this.semester = semester;
            this.reason = reason;
        }
        @Override
        public String toString() {
            return String.format("[ID: %d, Name: %s, Roll: %s, RegdNo: %s, Sem: %s, Reason: %s]",
                    leaveId, name, roll, regdNo, semester, reason);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyLeaveRequestPermission(6).setVisible(true);
        });
    }
}
