package pages.faculty;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.Random;
import javax.swing.*;

public class FacultyCalendar extends JFrame
{
    private final String[] options={"Dashboard", "Leave Application", "Student Calendar", "Attendance Report"};
    private final String[] months={
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private final String[] semesters={"Semester 1", "Semester 2", "Semester 3", "Semester 4"};
    private final String[] regdNos={"CSE/23101/1149", "CSE/23101/1150", "CSE/23101/1151", "CSE/23101/1152"};
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
    private JComboBox<String> semesterCombo;
    private JComboBox<String> regdNoCombo;
    int userID;

    public FacultyCalendar(int userID)
    {
        this.userID=userID;
        setTitle("Faculty Calendar");
        setSize(1000, 700);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel=new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2d=(Graphics2D) g;
                GradientPaint gp=new GradientPaint(
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

        JPanel sidebar=new JPanel(null);
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setBounds(0, 0, 200, getHeight());
        backgroundPanel.add(sidebar);

        JPanel userPanel=new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D) g;
                g2.setColor(Color.LIGHT_GRAY);
                int circleDiameter=60;
                int x=(getWidth()-circleDiameter)/2;
                int y=20;
                g2.fillOval(x, y, circleDiameter, circleDiameter);
            }
        };
        userPanel.setBounds(0, 0, 200, 150);
        userPanel.setBackground(new Color(51, 51, 51));

        JLabel usernameLabel=new JLabel("Sumanta", SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setBounds(0, 90, 200, 20);

        JLabel editLabel=new JLabel("Edit Profile", SwingConstants.CENTER);
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
            public void mouseClicked(MouseEvent e)
            {
                JOptionPane.showMessageDialog(null, "Edit Profile clicked!");
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
                JOptionPane.showMessageDialog(null, optionDashboardLabel.getText() + " clicked");
                SwingUtilities.invokeLater(() ->
                {
                    new FacultyDashboard(userID).setVisible(true);
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
                    new StudentAttendanceMarkingPage(userID).setVisible(true);
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
                    new FacultyCalendar(userID).setVisible(true);
                });
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);

        JPanel topPanel=new JPanel(null);
        topPanel.setBounds(210, 20, 760, 50);
        topPanel.setBackground(new Color(255, 255, 255, 80));
        backgroundPanel.add(topPanel);

        monthCombo=new JComboBox<>(months);
        monthCombo.setBounds(20, 10, 120, 30);
        monthCombo.setSelectedIndex(2);
        topPanel.add(monthCombo);

        semesterCombo=new JComboBox<>(semesters);
        semesterCombo.setBounds(160, 10, 100, 30);
        topPanel.add(semesterCombo);

        regdNoCombo=new JComboBox<>(regdNos);
        regdNoCombo.setBounds(280, 10, 120, 30);
        topPanel.add(regdNoCombo);

        JButton logoutBtn=new JButton("LOGOUT");
        logoutBtn.setBounds(620, 10, 100, 30);
        topPanel.add(logoutBtn);

        calendarPanel=new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        calendarPanel.setBounds(210, 80, 500, 380);
        calendarPanel.setOpaque(false);
        backgroundPanel.add(calendarPanel);

        dayLabels=new JLabel[42];

        progressPanel=new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D) g;
                g2.setStroke(new BasicStroke(10));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(10, 10, 120, 120);

                g2.setColor(new Color(46, 204, 113));
                g2.drawArc(10, 10, 120, 120, 90, progress*360/100);

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

        JPanel attPercentPanel=new JPanel(null);
        attPercentPanel.setBackground(new Color(255, 255, 255, 80));
        attPercentPanel.setBounds(730, 270, 180, 40);
        JLabel attPercentLabel=new JLabel("ATTENDANCE PERCENTAGE", SwingConstants.CENTER);
        attPercentLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        attPercentLabel.setBounds(0, 0, 180, 40);
        attPercentPanel.add(attPercentLabel);
        backgroundPanel.add(attPercentPanel);

        JPanel bottomPanel=new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bottomPanel.setBounds(210, 480, 760, 100);
        bottomPanel.setOpaque(false);
        backgroundPanel.add(bottomPanel);

        totalWorkingDaysLabel=new JLabel("TOTAL WORKING DAYS: 0");
        totalWorkingDaysLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalWorkingDaysLabel.setBounds(20, 20, 300, 30);
        bottomPanel.add(totalWorkingDaysLabel);

        presentLabel=new JLabel("Presents: 0");
        presentLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        presentLabel.setBounds(350, 20, 150, 30);
        bottomPanel.add(presentLabel);

        absentLabel=new JLabel("Absents: 0");
        absentLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        absentLabel.setBounds(500, 20, 150, 30);
        bottomPanel.add(absentLabel);

        buildDynamicCalendar();
        updateBottomPanel();

        Timer timer=new Timer(20, e ->
        {
            if(progress<attendancePercentage)
            {
                progress++;
                progressPanel.repaint();
            }
            else
            {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
        monthCombo.addActionListener(e -> rebuildCalendar());
    }

    private void rebuildCalendar()
    {
        calendarPanel.removeAll();
        buildDynamicCalendar();
        calendarPanel.revalidate();
        calendarPanel.repaint();
        updateBottomPanel();
    }

    private void buildDynamicCalendar()
    {
        int selectedMonth=monthCombo.getSelectedIndex()+1;
        int fixedYear=2025;

        createDayHeaders();

        LocalDate firstDayOfMonth=LocalDate.of(fixedYear, selectedMonth, 1);
        int daysInMonth=firstDayOfMonth.lengthOfMonth();
        int startDayOfWeek=firstDayOfMonth.getDayOfWeek().getValue();
        int row=1;
        int col=startDayOfWeek-1;

        presentCount=0;
        absentCount=0;
        unmarkedCount=0;

        Random rand=new Random();

        for(int day=1;day<=daysInMonth;day++)
        {
            JLabel dayLabel=new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setOpaque(true);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

            int status=rand.nextInt(3);

            if(col==6)
            {
                dayLabel.setBackground(Color.BLACK);
                dayLabel.setForeground(Color.WHITE);
            }
            else
            {
                if(status==1)
                {
                    dayLabel.setBackground(Color.GREEN);
                    presentCount++;
                }
                else if(status==2)
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
    }

    private void createDayHeaders()
    {
        String[] dayNames={"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        int xStart=20, yStart=20;
        int cellWidth=50, cellHeight=40;
        int spacingX=10;

        for (int col=0;col<7;col++)
        {
            JLabel headerLabel=new JLabel(dayNames[col], SwingConstants.CENTER);
            headerLabel.setOpaque(true);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            headerLabel.setBackground(new Color(230, 230, 230));
            headerLabel.setBounds(xStart+col*(cellWidth+spacingX), yStart, cellWidth, cellHeight);
            if(col==6)
            {
                headerLabel.setBackground(Color.BLACK);
                headerLabel.setForeground(Color.WHITE);
            }
            calendarPanel.add(headerLabel);
        }
    }

    private void updateBottomPanel()
    {
        int totalWorking=presentCount+absentCount;
        totalWorkingDaysLabel.setText("TOTAL WORKING DAYS: "+totalWorking);
        presentLabel.setText("Presents: "+presentCount);
        absentLabel.setText("Absents: "+absentCount);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(()->
        {
            new FacultyCalendar(1).setVisible(true);
        });
    }
}