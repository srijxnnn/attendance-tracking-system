package pages.student;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AttendanceReportPage extends JFrame {

    private SubjectData[] subjectData;
    private String[] chartSubjects;
    private int[] chartAttendance;

    private int animationProgress = 0;

    private PieChartPanel pieChartPanel;
    private BarChartPanel barChartPanel;

    private int userId;
    private int studentId;
    private String studentName = "Unknown";
    String studentEmail = "";

    public AttendanceReportPage(int userId) {
        this.userId = userId;

        subjectData = loadSubjectData();
        createChartData();
        setTitle("Attendance Report");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPanel = new GradientPanel();
        contentPanel.setLayout(null);
        contentPanel.setPreferredSize(new Dimension(1000, 1000));

        JPanel sidebar = createSidebar();
        sidebar.setBounds(0, 0, 200, 1000);
        contentPanel.add(sidebar);

        JPanel headerPanel = createHeaderPanel();
        headerPanel.setBounds(200, 0, 800, 100);
        contentPanel.add(headerPanel);
        int startY = 110;
        for (int i = 0; i < subjectData.length; i++) {
            SubjectPanel subjectPanel = new SubjectPanel(subjectData[i]);
            subjectPanel.setBounds(210, startY + i * 110, 750, 100);
            contentPanel.add(subjectPanel);
        }

        int dynamicY = startY + subjectData.length * 110 + 20;
        JPanel chartsContainer = new RoundedPanel(20, new Color(255, 255, 255, 80));
        chartsContainer.setLayout(null);
        chartsContainer.setBounds(210, dynamicY, 750, 250);
        contentPanel.add(chartsContainer);

        int totalHeight = dynamicY + chartsContainer.getHeight() + 20; // extra margin
        contentPanel.setPreferredSize(new Dimension(1000, totalHeight));
        contentPanel.revalidate();

        pieChartPanel = new PieChartPanel(chartSubjects, chartAttendance);
        pieChartPanel.setBounds(20, 20, 330, 210);
        chartsContainer.add(pieChartPanel);

        barChartPanel = new BarChartPanel(chartSubjects, chartAttendance);
        barChartPanel.setBounds(370, 20, 350, 210);
        chartsContainer.add(barChartPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBounds(0, 0, 1000, 700);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
            }

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
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            }
        });
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            }
        });
        scrollPane.setBorder(null);
        add(scrollPane);

        Timer timer = new Timer(20, e
                -> {
            if (animationProgress < 100) {
                animationProgress++;
                contentPanel.repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private SubjectData[] loadSubjectData() {
        ArrayList<SubjectData> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance()) {
            String emailQuery = "SELECT email FROM users WHERE id = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(emailQuery)) {
                ps1.setInt(1, userId);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        studentEmail = rs.getString("email");
                    }
                }
            }
            String studentIdQuery = "SELECT * FROM students WHERE user_id = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(studentIdQuery)) {
                ps1.setInt(1, userId);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        studentId = rs.getInt("id");
                        studentName = rs.getString("name");
                    }
                }
            }

            String query = "SELECT c.code, f.name AS facultyName, "
                    + "       COUNT(a.id) AS workingDays, "
                    + "       SUM(CASE WHEN a.status = 'present' THEN 1 ELSE 0 END) AS presentDays, "
                    + "       SUM(CASE WHEN a.status = 'absent' THEN 1 ELSE 0 END) AS absentDays "
                    + "FROM student_courses sc "
                    + "JOIN courses c ON sc.course_id = c.id "
                    + "LEFT JOIN faculty_courses fc ON c.id = fc.course_id "
                    + "LEFT JOIN faculty f ON fc.faculty_id = f.id "
                    + "LEFT JOIN attendances a ON a.course_id = c.id AND a.student_id = sc.student_id "
                    + "WHERE sc.student_id = ? "
                    + "GROUP BY c.id, f.name";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("code");
                    String faculty = rs.getString("facultyName");
                    int workingDays = rs.getInt("workingDays");
                    int presentDays = rs.getInt("presentDays");
                    int absentDays = rs.getInt("absentDays");
                    int percentage = (workingDays > 0) ? (presentDays * 100 / workingDays) : 0;
                    list.add(new SubjectData(code, faculty, workingDays, presentDays, absentDays, percentage));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.toArray(new SubjectData[list.size()]);
    }

    private void createChartData() {
        chartSubjects = new String[subjectData.length];
        chartAttendance = new int[subjectData.length];
        for (int i = 0; i < subjectData.length; i++) {
            chartSubjects[i] = subjectData[i].subjectCode;
            chartAttendance[i] = subjectData[i].attendancePercentage;
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

        JLabel optionDashboardLabel = new JLabel(options[0], SwingConstants.CENTER);
        optionDashboardLabel.setForeground(Color.WHITE);
        optionDashboardLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionDashboardLabel.setOpaque(true);
        optionDashboardLabel.setBackground(new Color(51, 51, 51));
        optionDashboardLabel.setBounds(0, 160 + 0 * 50, 200, 40);

        optionDashboardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                optionDashboardLabel.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                optionDashboardLabel.setBackground(new Color(51, 51, 51));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()
                        -> {
                    new StudentDashboard(userId).setVisible(true);
                    System.out.println(userId);
                });

            }
        });
        sidebar.add(optionDashboardLabel);

        JLabel optionLeaveApplicationLabel = new JLabel(options[1], SwingConstants.CENTER);
        optionLeaveApplicationLabel.setForeground(Color.WHITE);
        optionLeaveApplicationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionLeaveApplicationLabel.setOpaque(true);
        optionLeaveApplicationLabel.setBackground(new Color(51, 51, 51));
        optionLeaveApplicationLabel.setBounds(0, 160 + 1 * 50, 200, 40);

        optionLeaveApplicationLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                optionLeaveApplicationLabel.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                optionLeaveApplicationLabel.setBackground(new Color(51, 51, 51));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    new StudentLeaveApplicationPage(userId).setVisible(true);
                });

            }
        });
        sidebar.add(optionLeaveApplicationLabel);

        JLabel optionCalenderLabel = new JLabel(options[2], SwingConstants.CENTER);
        optionCalenderLabel.setForeground(Color.WHITE);
        optionCalenderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionCalenderLabel.setOpaque(true);
        optionCalenderLabel.setBackground(new Color(51, 51, 51));
        optionCalenderLabel.setBounds(0, 160 + 2 * 50, 200, 40);

        optionCalenderLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                optionCalenderLabel.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                optionCalenderLabel.setBackground(new Color(51, 51, 51));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()
                        -> {
                    new StudentCalendar(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionCalenderLabel);

        JLabel optionStudentAttendanceReportLabel = new JLabel(options[3], SwingConstants.CENTER);
        optionStudentAttendanceReportLabel.setForeground(Color.WHITE);
        optionStudentAttendanceReportLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        optionStudentAttendanceReportLabel.setOpaque(true);
        optionStudentAttendanceReportLabel.setBackground(new Color(51, 51, 51));
        optionStudentAttendanceReportLabel.setBounds(0, 160 + 3 * 50, 200, 40);

        optionStudentAttendanceReportLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                optionStudentAttendanceReportLabel.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                optionStudentAttendanceReportLabel.setBackground(new Color(51, 51, 51));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(()
                        -> {
                    new AttendanceReportPage(userId).setVisible(true);
                });
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);

        return sidebar;
    }

    private static File generateUniqueFile(String baseName) {
        File file = new File(baseName);
        int count = 1;

        // Check if file already exists, if so, create a new one with a number
        while (file.exists()) {
            String newName = baseName.replace(".pdf", "_" + count + ".pdf");
            file = new File(newName);
            count++;
        }

        return file;
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
        JLabel titleLabel = new JLabel("Attendance Report");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(20, 30, 300, 40);
        headerPanel.add(titleLabel);

        GradientHoverButton exportBtn = new GradientHoverButton("Export as PDF");
        styleHeaderButton(exportBtn);
        exportBtn.setBounds(500, 35, 140, 30);
        headerPanel.add(exportBtn);
        exportBtn.addActionListener(e -> {
            // Show "Please wait" dialog
            JDialog loadingDialog = new JDialog();
            loadingDialog.setTitle("Generating PDF...");
            JLabel message = new JLabel("Please wait while the report is being generated...", SwingConstants.CENTER);
            loadingDialog.add(message);
            loadingDialog.setSize(300, 100);
            loadingDialog.setLocationRelativeTo(null);
            loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            loadingDialog.setModal(true);

            new Thread(() -> {
                try {
                    // Define fixed file name (this will be replaced if it already exists)
                    String filePath = "C:/Users/Anuj/Desktop/attendance-tracking-system/attendance_report.pdf";

                    // Call the updated getReport method to overwrite the file
                    AttendanceReport.getReport(studentId, filePath);

                    // Close the loading dialog after successful generation
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(null, "PDF successfully generated: " + filePath);
                    JOptionPane.showMessageDialog(null, "A copy is sent to your mail! Check your inbox");

                    String pdfFilePath = "attendance_report.pdf"; 

                    boolean sent = AttendanceMailer.sendAttendanceReport(studentEmail, studentName, pdfFilePath);
                    if (sent) {
                        System.out.println("Report emailed successfully!");
                    } else {
                        System.out.println("Failed to email the report.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(null, "Error generating PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }).start();

            loadingDialog.setVisible(true);
        });

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

    private class SubjectPanel extends JPanel {

        private float fadeAlpha = 0f;
        private int hoverOffset = 0;
        private final SubjectData data;
        private Timer fadeTimer, hoverTimer;
        private SubjectPieChart subjectPieChart;

        public SubjectPanel(SubjectData data) {
            this.data = data;
            setOpaque(false);
            setLayout(null);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            fadeTimer = new Timer(20, e
                    -> {
                if (fadeAlpha < 1f) {
                    fadeAlpha += 0.05f;
                    repaint();
                } else {
                    fadeAlpha = 1f;
                    fadeTimer.stop();
                }
            });
            fadeTimer.start();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (hoverTimer != null && hoverTimer.isRunning()) {
                        hoverTimer.stop();
                    }
                    hoverTimer = new Timer(10, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (hoverOffset > -5) {
                                hoverOffset--;
                                repaint();
                            } else {
                                hoverTimer.stop();
                            }
                        }
                    });
                    hoverTimer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (hoverTimer != null && hoverTimer.isRunning()) {
                        hoverTimer.stop();
                    }
                    hoverTimer = new Timer(10, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (hoverOffset < 0) {
                                hoverOffset++;
                                repaint();
                            } else {
                                hoverTimer.stop();
                            }
                        }
                    });
                    hoverTimer.start();
                }
            });

            subjectPieChart = new SubjectPieChart(data.attendancePercentage);
            subjectPieChart.setBounds(650, 10, 80, 80);
            add(subjectPieChart);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            Shape rect = new RoundRectangle2D.Float(0, hoverOffset, w, h, 20, 20);
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fill(rect);
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2));
            g2.draw(rect);
            g2.dispose();

            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString("Subject Code: " + data.subjectCode, 20, 30);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString("Faculty Name: " + data.facultyName, 20, 50);
            g.drawString("Total Working Days: " + data.workingDays, 220, 30);
            g.drawString("Present Days: " + data.presentDays, 220, 50);
            g.drawString("Absents: " + data.absentDays, 420, 30);
        }
    }

    private class SubjectPieChart extends JPanel {

        private final int finalPercentage;

        public SubjectPieChart(int finalPercentage) {
            this.finalPercentage = finalPercentage;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int progressForThis = (int) (finalPercentage * (animationProgress / 100.0));
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(6));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawOval(5, 5, 60, 60);
            g2.setColor(new Color(46, 204, 113));
            int arcAngle = (int) (-360 * (progressForThis / 100.0));
            g2.drawArc(5, 5, 60, 60, 90, arcAngle);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            String text = progressForThis + "%";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() / 2 + fm.getHeight() / 4;
            g2.drawString(text, x, y);
        }
    }

    private class RoundedPanel extends JPanel {

        private final int arc;

        public RoundedPanel(int arc, Color bgColor) {
            super();
            this.arc = arc;
            setOpaque(false);
            setBackground(bgColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(getBackground());
            g2.fill(round);
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(2));
            g2.draw(round);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class PieChartPanel extends JPanel {

        private final String[] subjects;
        private final int[] attendance;

        public PieChartPanel(String[] subjects, int[] attendance) {
            this.subjects = subjects;
            this.attendance = attendance;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
            double total = 0;
            for (int val : attendance) {
                total += val;
            }
            double currentAngle = 0;
            double fraction = animationProgress / 100.0;
            Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.ORANGE, Color.CYAN};
            for (int i = 0; i < attendance.length; i++) {
                double sliceAngle = (attendance[i] / total) * 360.0 * fraction;
                g2.setColor(colors[i % colors.length]);
                g2.fillArc(x, y, size, size, (int) currentAngle, (int) -sliceAngle);
                currentAngle -= sliceAngle;
            }
            g2.dispose();
        }
    }

    private class BarChartPanel extends JPanel {

        private final String[] subjects;
        private final int[] attendance;

        public BarChartPanel(String[] subjects, int[] attendance) {
            this.subjects = subjects;
            this.attendance = attendance;
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRect(0, 0, w, h);
            int maxAttendance = 100;
            int barWidth;
            if (subjects.length == 0) {
                barWidth = Math.max(20, w / (1 * 2));
            } else {
                barWidth = Math.max(20, w / (subjects.length * 2));
            }
            int gap = barWidth / 2;
            int xStart = 30, yBase = h - 30;
            double fraction = animationProgress / 100.0;
            g2.setColor(Color.BLACK);
            g2.drawLine(xStart, yBase, w - 20, yBase);
            for (int i = 0; i < subjects.length; i++) {
                double scaledVal = attendance[i] / (double) maxAttendance;
                int barHeight = (int) (scaledVal * (h - 60) * fraction);
                int x = xStart + i * (barWidth + gap);
                int y = yBase - barHeight;
                g2.setColor(new Color(100, 149, 237));
                g2.fillRect(x, y, barWidth, barHeight);
                g2.setColor(Color.BLACK);
                g2.drawString(subjects[i], x, yBase + 15);
            }
            g2.dispose();
        }
    }

    private static class SubjectData {

        String subjectCode, facultyName;
        int workingDays, presentDays, absentDays, attendancePercentage;

        public SubjectData(String code, String faculty, int working, int present, int absent, int percent) {
            subjectCode = code;
            facultyName = faculty;
            workingDays = working;
            presentDays = present;
            absentDays = absent;
            attendancePercentage = percent;
        }
    }

    private class GradientPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80), getWidth(), getHeight(), new Color(189, 195, 199));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private class GradientHoverButton extends JButton {

        private boolean hover;

        public GradientHoverButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (hover) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(230, 230, 230), getWidth(), getHeight(), new Color(200, 200, 200)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            } else {
                g.setColor(new Color(0, 123, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }

        @Override
        public void paintBorder(Graphics g) {
            g.setColor(new Color(0, 123, 255));
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        @Override
        public void setContentAreaFilled(boolean b) {

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()
                -> {
            new AttendanceReportPage(1).setVisible(true);
        });
    }
}
