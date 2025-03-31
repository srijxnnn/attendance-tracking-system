package pages.faculty;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class StudentAttendanceMarkingPage extends JFrame
{

    private final String[] options={"Dashboard", "Leave Request", "Mark Attendance", "Student Attendance Report"};
    private final String[] semesters={"Semester 1", "Semester 2", "Semester 3", "Semester 4","Semester 5", "Semester 6", "Semester 7","Semester 8"};
    private final String[] dates={"01/03/25", "02/03/25", "03/03/25", "04/03/25"};
    private final String[] subjects={"MAC401", "CSC401", "CSC402", "CSC403"};

    private final List<StudentData> studentList=new ArrayList<>();
    int userID;
    public StudentAttendanceMarkingPage(int userID)
    {
        this.userID=userID;
        setTitle("Student Attendance Marking");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        studentList.add(new StudentData("CSE/23101/1149", "John Doe", true));
        studentList.add(new StudentData("CSE/23101/1150", "Alice Smith", true));
        studentList.add(new StudentData("CSE/23101/1151", "Bob Johnson", true));
        studentList.add(new StudentData("CSE/23101/1152", "Carol White", true));
        studentList.add(new StudentData("CSE/23101/1153", "David Green", true));

        GradientPanel backgroundPanel=new GradientPanel();
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        backgroundPanel.setLayout(null);
        add(backgroundPanel);


        JPanel sidebar=createSidebar();
        sidebar.setBounds(0, 0, 200, 1000);
        backgroundPanel.add(sidebar);

        JPanel topPanel=createTopPanel();
        topPanel.setBounds(210, 20, 760, 50);
        backgroundPanel.add(topPanel);

        JPanel contentPanel=new JPanel(null);
        contentPanel.setOpaque(false);
        contentPanel.setPreferredSize(new Dimension(760, 600));

        TableHeaderRow headerRow=new TableHeaderRow();
        headerRow.setBounds(0, 0, 760, 50);
        contentPanel.add(headerRow);

        int startY=60;
        for(StudentData student:studentList)
        {
            TableDataRow dataRow=new TableDataRow(student);
            dataRow.setBounds(0, startY, 760, 50);
            contentPanel.add(dataRow);
            startY+=60;
        }

        JScrollPane scrollPane=new JScrollPane(contentPanel);
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
                JButton btn=new JButton();
                btn.setPreferredSize(new Dimension(0,0));
                btn.setMinimumSize(new Dimension(0,0));
                btn.setMaximumSize(new Dimension(0,0));
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

    private JPanel createSidebar()
    {
        JPanel sidebar=new JPanel(null);
        sidebar.setBackground(new Color(51, 51, 51));

        JPanel userPanel=new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D) g;
                g2.setColor(Color.LIGHT_GRAY);
                int d=60;
                int x=(getWidth()-d)/2;
                int y=20;
                g2.fillOval(x,y,d,d);
            }
        };
        userPanel.setBounds(0, 0, 200, 150);
        userPanel.setBackground(new Color(51, 51, 51));

        JLabel usernameLabel=new JLabel("Faculty Name", SwingConstants.CENTER);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setBounds(0, 90, 200, 20);

        JLabel editLabel=new JLabel("Edit Profile", SwingConstants.CENTER);
        editLabel.setForeground(new Color(200,200,200));
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
                editLabel.setForeground(new Color(200,200,200));
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                new FacultyEditProfile(userID).setVisible(true);
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
                SwingUtilities.invokeLater(() -> {
                    new FacultyLeaveRequestPermission(userID).setVisible(true);
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
                SwingUtilities.invokeLater(()->
                {
                    new FacultyCalendar(userID).setVisible(true);
                });
            }
        });
        sidebar.add(optionStudentAttendanceReportLabel);

        return sidebar;
    }

    private JPanel createTopPanel()
    {
        JPanel topPanel=new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D) g;
                g2.setColor(new Color(255,255,255,80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setBackground(new Color(255,255,255,80));

        JComboBox<String> semesterCombo=new JComboBox<>(semesters);
        semesterCombo.setBounds(20, 10, 100, 30);
        topPanel.add(semesterCombo);

        JComboBox<String> dateCombo=new JComboBox<>(dates);
        dateCombo.setBounds(130, 10, 100, 30);
        topPanel.add(dateCombo);

        JComboBox<String> subjectCombo=new JComboBox<>(subjects);
        subjectCombo.setBounds(240, 10, 120, 30);
        topPanel.add(subjectCombo);

        JButton logoutBtn=new JButton("LOGOUT");
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
        button.setBackground(new Color(0,123,255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    private void addButtonHoverGradient(JButton button)
    {
        button.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                button.setBackground(new Color(200,200,200));
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                button.setBackground(new Color(0,123,255));
            }
        });
    }

    private class TableHeaderRow extends JPanel
    {
        public TableHeaderRow()
        {
            setLayout(null);
            setOpaque(false);
            setBackground(new Color(255,255,255,200));
            setBorder(null);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2=(Graphics2D) g.create();
            int w=getWidth();
            int h=getHeight();
            g2.setColor(new Color(255, 255, 255, 180));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(180,180,180));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(0, 0, w-1, h-1);
            g2.dispose();

            super.paintComponent(g);

            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(Color.BLACK);
            g.drawString("Regd. No.", 30, h/2+5);
            g.drawString("Name", 280, h/2+5);
            g.drawString("Attendance", 530, h/2+5);
        }
    }

    private class TableDataRow extends JPanel
    {
        private float fadeAlpha=0f;
        private int hoverOffset=0;
        private Timer fadeTimer,hoverTimer;
        private final StudentData data;

        private JRadioButton presentRadio;
        private JRadioButton absentRadio;

        public TableDataRow(StudentData data)
        {
            this.data=data;
            setLayout(null);
            setOpaque(false);

            fadeTimer=new Timer(20, e ->
            {
                if(fadeAlpha<1f)
                {
                    fadeAlpha+=0.05f;
                    repaint();
                }
                else
                {
                    fadeAlpha=1f;
                    fadeTimer.stop();
                }
            });
            fadeTimer.start();

            addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {
                    if(hoverTimer!=null && hoverTimer.isRunning())
                    {
                        hoverTimer.stop();
                    }
                    hoverTimer=new Timer(10, ee ->
                    {
                        if(hoverOffset>-5)
                        {
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
                    if(hoverTimer!=null && hoverTimer.isRunning())
                    {
                        hoverTimer.stop();
                    }
                    hoverTimer=new Timer(10, ee ->
                    {
                        if(hoverOffset<0)
                        {
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

            presentRadio=new JRadioButton("Present");
            absentRadio=new JRadioButton("Absent");

            ButtonGroup group=new ButtonGroup();
            group.add(presentRadio);
            group.add(absentRadio);

            if(data.isPresent)
            {
                presentRadio.setSelected(true);
            }
            else
            {
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
            Graphics2D g2=(Graphics2D) g.create();
            int w=getWidth();
            int h=getHeight();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            Shape rect = new RoundRectangle2D.Float(0, hoverOffset, w, h, 20, 20);
            g2.setColor(new Color(255,255,255,220));
            g2.fill(rect);
            g2.setColor(new Color(200,200,200));
            g2.setStroke(new BasicStroke(2));
            g2.draw(rect);
            g2.dispose();

            super.paintComponent(g);

            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.setColor(Color.BLACK);
            g.drawString(data.regdNo, 30, getHeight()/2 + 5);
            g.drawString(data.name, 280, getHeight()/2 + 5);
        }
    }

    private static class StudentData
    {
        String regdNo;
        String name;
        boolean isPresent;

        public StudentData(String regdNo,String name,boolean isPresent)
        {
            this.regdNo=regdNo;
            this.name=name;
            this.isPresent=isPresent;
        }
    }

    private class GradientPanel extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d=(Graphics2D) g;
            GradientPaint gp=new GradientPaint(
                    0, 0, new Color(44,62,80),
                    getWidth(), getHeight(), new Color(189,195,199)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            new StudentAttendanceMarkingPage(1).setVisible(true);
        });
    }
}