package pages.faculty;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

public class FacultyLeaveRequestPermission extends JFrame {

    private final String[] sidebarOptions = {
            "Dashboard", "Leave Request", "Mark Attendance", "Student Attendance Report"
    };

    // Dummy data for leave requests
    private final List<LeaveRequestData> requestList = new ArrayList<>();
    private int userId;

    public FacultyLeaveRequestPermission(int userId) {
        this.userId = userId;
        setTitle("Faculty Leave Requests");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Populate dummy data
        requestList.add(new LeaveRequestData("Srijan Dhak", "1149", "CSE/23101", "4", "Vacation Trip"));
        requestList.add(new LeaveRequestData("Sumanta P", "1150", "CSE/23102", "6", "Medical Leave"));
        requestList.add(new LeaveRequestData("Riya Sen", "1160", "CSE/23110", "4", "Family Function"));
        requestList.add(new LeaveRequestData("Souvik Das", "1201", "CSE/23111", "8", "Personal Work"));
        requestList.add(new LeaveRequestData("Suman T", "1155", "CSE/23155", "3", "Urgent Travel"));

        // Main gradient background panel (grayish gradient)
        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // SIDEBAR – fixed on left; ensure layout is null so positions are respected.
        JPanel sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setBounds(0, 0, 200, getHeight());
        backgroundPanel.add(sidebar);

        // User Panel at top of sidebar
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

        JLabel usernameLabel = new JLabel("Faculty Name", SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setBounds(0, 90, 200, 20);
        userPanel.add(usernameLabel);

        JLabel editLabel = new JLabel("Edit Profile", SwingConstants.CENTER);
        editLabel.setForeground(new Color(200, 200, 200));
        editLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        editLabel.setBounds(0, 115, 200, 20);
        editLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { editLabel.setForeground(Color.WHITE); }
            @Override
            public void mouseExited(MouseEvent e) { editLabel.setForeground(new Color(200,200,200)); }
            @Override
            public void mouseClicked(MouseEvent e) { JOptionPane.showMessageDialog(null, "Edit Profile clicked!"); }
        });
        userPanel.add(editLabel);

        // Sidebar options
        // for (int i = 0; i < sidebarOptions.length; i++) {
        //     JLabel optionLabel = new JLabel(sidebarOptions[i], SwingConstants.CENTER);
        //     optionLabel.setForeground(Color.WHITE);
        //     optionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        //     optionLabel.setOpaque(true);
        //     optionLabel.setBackground(new Color(51, 51, 51));
        //     optionLabel.setBounds(0, 160 + i * 50, 200, 40);
        //     optionLabel.addMouseListener(new MouseAdapter() {
        //         @Override public void mouseEntered(MouseEvent e) { optionLabel.setBackground(new Color(70,70,70)); }
        //         @Override public void mouseExited(MouseEvent e) { optionLabel.setBackground(new Color(51,51,51)); }
        //         @Override public void mouseClicked(MouseEvent e) { JOptionPane.showMessageDialog(null, optionLabel.getText() + " clicked"); }
        //     });
        //     sidebar.add(optionLabel);
        // }
        String[] options={"Dashboard", "Leave Request", "Mark Attendance", "Student Attendance Report"};
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
                });

            }
        });
        sidebar.add(optionDashboardLabel);

        JLabel optionLeaveRequestLabel=new JLabel(options[1], SwingConstants.CENTER);
        optionLeaveRequestLabel.setForeground(Color.WHITE);
        optionLeaveRequestLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionLeaveRequestLabel.setOpaque(true);
        optionLeaveRequestLabel.setBackground(new Color(51, 51, 51));
        optionLeaveRequestLabel.setBounds(0, 160+1*50, 200, 40);

        optionLeaveRequestLabel.addMouseListener(new MouseAdapter()
        {
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
                JOptionPane.showMessageDialog(null, optionLeaveRequestLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() -> {
                    new FacultyLeaveRequestPermission(userId).setVisible(true);
                });

            }
        });
        sidebar.add(optionLeaveRequestLabel);

        JLabel optionMarkAttendanceLabel=new JLabel(options[2], SwingConstants.CENTER);
        optionMarkAttendanceLabel.setForeground(Color.WHITE);
        optionMarkAttendanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionMarkAttendanceLabel.setOpaque(true);
        optionMarkAttendanceLabel.setBackground(new Color(51, 51, 51));
        optionMarkAttendanceLabel.setBounds(0, 160+2*50, 200, 40);

        optionMarkAttendanceLabel.addMouseListener(new MouseAdapter()
        {
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
                JOptionPane.showMessageDialog(null, optionMarkAttendanceLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() ->
                {
                    new StudentAttendanceMarkingPage(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionMarkAttendanceLabel);

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

        // Header Panel with title and logout button
        JPanel headerPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
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
        // Logout button: when hovered, turn red.
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(Color.RED);
                logoutBtn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(33,150,243));
                logoutBtn.setForeground(Color.WHITE);
            }
        });
        headerPanel.add(logoutBtn);

        // Content Panel for table with background (whitish gray for rows to appear lighter against darker gaps)
        JPanel contentPanel = new JPanel(null);
        // Set the background of the panel to a slightly darker gray (for gaps)
        contentPanel.setOpaque(true);
        contentPanel.setBackground(new Color(180,180,180));
        contentPanel.setPreferredSize(new Dimension(800, 600));

        // Table header row with fixed column widths: NAME(140), ROLL NO(90), REGD NO(110), SEMESTER(90), REASON(190), STATUS(110)
        int[] colWidths = {140, 90, 110, 90, 150, 180};
        TableHeaderRow headerRow = new TableHeaderRow(colWidths);
        headerRow.setBounds(0, 0, 800, 50);
        contentPanel.add(headerRow);

        // Data rows – each row's cell text wraps if too long; row height is computed dynamically.
        int startY = 60;
        for (LeaveRequestData data : requestList) {
            TableDataRow rowPanel = new TableDataRow(data, colWidths);
            rowPanel.setBounds(0, startY, 800, rowPanel.getPreferredHeight());
            contentPanel.add(rowPanel);
            startY += rowPanel.getPreferredHeight() + 10; // gap between rows (this gap shows the darker background)
        }
        contentPanel.setPreferredSize(new Dimension(800, startY));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBounds(200, 60, 800, 640);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        backgroundPanel.add(scrollPane);
    }

    // Style for logout button with rounded border
    private void styleHeaderButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(new Color(33,150,243));
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(15));
    }

    // Gradient background panel with grayish gradient (darker top, lighter bottom)
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(70,70,70),
                    getWidth(), getHeight(), new Color(150,150,150)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Table header row with customizable column widths
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

    // Table data row with dynamic height, improved hover effect, and auto-wrapping text.
    private class TableDataRow extends JPanel {
        private int hoverOffset = 0;
        private Timer hoverTimer;
        private int rowHeight;
        public TableDataRow(LeaveRequestData data, int[] colWidths) {
            setLayout(null);
            setOpaque(false);
            // Create cell labels using HTML for wrapping if text is long.
            String[] cellTexts = {data.name, data.rollNo, data.regdNo, data.semester};
            int x = 0;
            int maxHeight = 50; // default minimum row height
            List<JLabel> cellLabels = new ArrayList<>();
            for (int i = 0; i < cellTexts.length; i++) {
                JLabel label = new JLabel("<html>" + cellTexts[i] + "</html>", SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                label.setVerticalAlignment(SwingConstants.TOP);
                // Set bounds with a slight top margin for padding
                label.setBounds(x, 10, colWidths[i], 50);
                Dimension pref = label.getPreferredSize();
                if (pref.height > maxHeight) {
                    maxHeight = pref.height;
                }
                cellLabels.add(label);
                x += colWidths[i];
            }
            // rowHeight = maxHeight;
            // for (JLabel label : cellLabels) {
            //     Rectangle b = label.getBounds();
            //     label.setBounds(b.x, b.y, b.width, rowHeight);
            //     add(label);
            // }
            int seeReasonWidth = Math.min(100, colWidths[4]);
            JButton seeReasonBtn = new JButton("See Reason");
            seeReasonBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
            seeReasonBtn.setForeground(Color.WHITE);
            seeReasonBtn.setBackground(new Color(66,133,244));
            seeReasonBtn.setFocusPainted(false);
            // Vertically align with other cells (same top margin)
            seeReasonBtn.setBounds(0, 0, seeReasonWidth, 30);
            seeReasonBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, data.reason, "Leave Reason", JOptionPane.INFORMATION_MESSAGE);
            });
            // We'll consider the REASON cell height as 50.
            int reasonCellHeight = 50;
            rowHeight = Math.max(maxHeight, reasonCellHeight);
            // Set each label's height to rowHeight.
            for (JLabel label : cellLabels) {
                Rectangle b = label.getBounds();
                label.setBounds(b.x, b.y, b.width, rowHeight);
                add(label);
            }
            // Add the See Reason button to its column (its column occupies colWidths[4] width)
            seeReasonBtn.setBounds(x+25, 10, seeReasonWidth, 30);
            add(seeReasonBtn);
            x += colWidths[4];
            // STATUS column: add status panel with Approve and Deny buttons
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
                statusPanel.removeAll();
                JLabel statusLabel = new JLabel("<html><b>Accepted</b></html>", SwingConstants.CENTER);
                statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                statusLabel.setForeground(new Color(46,204,113));
                statusLabel.setBounds(0, 0, colWidths[5], rowHeight);
                statusPanel.add(statusLabel);
                statusPanel.revalidate();
                statusPanel.repaint();
                System.out.println("APPROVED: " + rowDataString(data));
            });
            
            JButton denyBtn = new JButton("Deny");
            denyBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            denyBtn.setForeground(Color.WHITE);
            denyBtn.setBackground(new Color(231,76,60));
            denyBtn.setFocusPainted(false);
            denyBtn.setBounds(90, (rowHeight - 30) / 2, 80, 30);
            addButtonHoverEffect(denyBtn, new Color(231,76,60));
            denyBtn.addActionListener(e -> {
                statusPanel.removeAll();
                JLabel statusLabel = new JLabel("<html><b>Denied</b></html>", SwingConstants.CENTER);
                statusLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
                statusLabel.setForeground(new Color(231,76,60));
                statusLabel.setBounds(0, 0, colWidths[5], rowHeight);
                statusPanel.add(statusLabel);
                statusPanel.revalidate();
                statusPanel.repaint();
                System.out.println("DENIED: " + rowDataString(data));
            });
            
            statusPanel.add(approveBtn);
            statusPanel.add(denyBtn);
            add(statusPanel);
            
            setPreferredSize(new Dimension(800, rowHeight));
            
            // Row hover effect: subtle drop shadow and slight elevation (without altering positions of internal elements)
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (hoverTimer != null && hoverTimer.isRunning()) {
                        hoverTimer.stop();
                    }
                    hoverTimer = new Timer(10, (ee) -> {
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
                    hoverTimer = new Timer(10, (ee) -> {
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
        
        private String rowDataString(LeaveRequestData data) {
            return String.format("[Name=%s, RollNo=%s, RegdNo=%s, Sem=%s, Reason=%s]",
                    data.name, data.rollNo, data.regdNo, data.semester, data.reason);
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
            // Draw drop shadow if hovered
            if (hoverOffset < 0) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(4, getHeight() - 4 + hoverOffset, getWidth(), 5, 15, 15);
            }
            // Draw row background with slight darkening on hover
            g2.setColor(new Color(255, 255, 255, 230));
            g2.fillRect(0, hoverOffset, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Rounded border for buttons (logout and others)
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

    // Data class for leave request row data (defined once)
    private static class LeaveRequestData {
        String name;
        String rollNo;
        String regdNo;
        String semester;
        String reason;
        public LeaveRequestData(String name, String rollNo, String regdNo, String semester, String reason) {
            this.name = name;
            this.rollNo = rollNo;
            this.regdNo = regdNo;
            this.semester = semester;
            this.reason = reason;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyLeaveRequestPermission(1).setVisible(true);
        });
    }
}
