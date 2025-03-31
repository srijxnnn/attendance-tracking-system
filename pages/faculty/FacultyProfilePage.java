package pages.faculty;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

public class FacultyProfilePage extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField designationField;
    private JTextField expertiseField;
    private JTextArea coursesArea;
    private JButton saveButton;
    private JLabel ProfileLabel;

    public FacultyProfilePage(int userId) 
    {
        setTitle("Profile Form");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Main gradient background panel with a lighter grayish gradient
        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // Create a form container panel (centered)
        JPanel formContainer = new JPanel(null) {
            // Use hover effect to change background and border color.
            private Color normalBg = new Color(255, 255, 255, 230);
            private Color hoverBg = new Color(235, 235, 235, 230);
            private Color normalBorder = new Color(200, 200, 200);
            private Color hoverBorder = new Color(100, 100, 100);

            @Override
            protected void paintComponent(Graphics g) {
                // Fill background of container with current background color.
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        JLabel welcomeLabel = new JLabel("Set Faculty Profile");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcomeLabel.setBounds(400, 20, 400, 30);
        backgroundPanel.add(welcomeLabel);

        formContainer.setOpaque(false);
        // Center the container in the frame: container size 500x450
        formContainer.setBounds(200, 65, 600, 575);
        // Set a rounded line border
        formContainer.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
        backgroundPanel.add(formContainer);

        // Add hover effect to the container (change border color)
        formContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                formContainer.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true));
                formContainer.setBackground(new Color(235, 235, 235, 230));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                formContainer.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
                formContainer.setBackground(new Color(255, 255, 255, 230));
            }
        });

        ImageIcon profileIcon = new ImageIcon(getClass().getResource("facultyProfile.png"));
        // Scale the image to fit the desired dimensions (100x170)
        Image scaledImage = profileIcon.getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH);
        profileIcon = new ImageIcon(scaledImage);
        ProfileLabel = new JLabel(profileIcon);
        ProfileLabel.setBounds(220, 20, 170, 170);
        formContainer.add(ProfileLabel);


        // Set vertical starting point for form fields (below the image)
        int currentY = 225;
        int labelWidth = 100, fieldWidth = 300, fieldHeight = 30, gap = 20;
        int centerX = (500 - fieldWidth) / 2;

        // Name label and text field
        JLabel nameLabel = new JLabel("NAME:");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setBounds(50, currentY, labelWidth, fieldHeight);
        formContainer.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, currentY, fieldWidth, fieldHeight);
        formContainer.add(nameField);
        currentY += fieldHeight + gap;

        // Roll No.
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLabel.setBounds(50, currentY, labelWidth, fieldHeight);
        formContainer.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, currentY, fieldWidth, fieldHeight);
        formContainer.add(emailField);
        currentY += fieldHeight + gap;

        // Regd No.
        JLabel designationLabel = new JLabel("DESIGNATION:");
        designationLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        designationLabel.setBounds(50, currentY, labelWidth, fieldHeight);
        formContainer.add(designationLabel);

        designationField = new JTextField();
        designationField.setBounds(150, currentY, fieldWidth, fieldHeight);
        formContainer.add(designationField);
        currentY += fieldHeight + gap;

        // Semester dropdown
        JLabel expertiseLabel = new JLabel("EXPERTISE:");
        expertiseLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        expertiseLabel.setBounds(50, currentY, labelWidth, fieldHeight);
        formContainer.add(expertiseLabel);
 
        expertiseField = new JTextField();
        expertiseField.setBounds(150, currentY, fieldWidth, fieldHeight);
        formContainer.add(expertiseField);
        currentY += fieldHeight + gap;

        // Courses text area
        JLabel coursesLabel = new JLabel("COURSES:");
        coursesLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        coursesLabel.setBounds(50, currentY, labelWidth, fieldHeight);
        formContainer.add(coursesLabel);

        coursesArea = new JTextArea();
        coursesArea.setLineWrap(true);
        coursesArea.setWrapStyleWord(true);
        JScrollPane coursesScroll = new JScrollPane(coursesArea);
        coursesScroll.setBounds(150, currentY, fieldWidth, 80);
        formContainer.add(coursesScroll);
        currentY += 80 + gap;

        // Save button (centered)
        saveButton = new JButton("SAVE");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(0,122,204));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(new RoundedBorder(10));
        int btnWidth = 120, btnHeight = 40;
        int btnX = (500 - btnWidth) / 2;
        saveButton.setBounds(250, currentY, btnWidth, btnHeight);
        addButtonHoverEffect(saveButton);
        formContainer.add(saveButton);

        // On Save button click, print the form values to the console.
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String desg = designationField.getText();
            String expertise = expertiseField.getText();
            String courses = coursesArea.getText();

            String[] courseArr=courses.split(",");
            ArrayList<String> courseList = new ArrayList<>(Arrays.asList(courseArr));
            ArrayList<Integer> courseIds = new ArrayList<>();

            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Designation: " + desg);
            System.out.println("Expertise: " + expertise);
            System.out.println("Courses: " + courseList);

            for(int i=0;i<courseList.size();i++)
            {
                courseIds.add(checkCourses(courseList.get(i)));
            }
            
            if(courseIds.contains(-1))
            {
                JOptionPane.showMessageDialog(null, "Invalid Subject Codes !!!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                SetFacultyProfile(userId, name, expertise, desg);
                int facultyId=getFacultyId(userId);
                for(int i=0;i<courseIds.size();i++)
                {
                    SetFacultyCourses(facultyId, courseIds.get(i));
                }

                SwingUtilities.invokeLater(() -> {
                    new FacultyDashboard(userId).setVisible(true);
                });
            }

        });
    }

    // Hover effect for the Save button: on hover, change background to greenish-blue and font color to black.
    private void addButtonHoverEffect(JButton btn) {
        Color normalBg = new Color(0,122,204);
        btn.setBackground(normalBg);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0,150,150));
                btn.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normalBg);
                btn.setForeground(Color.WHITE);
            }
        });
    }

    // Custom GradientPanel with a lighter grayish gradient background.
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Lighter gradient: from light gray at top to very light gray at bottom.
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(200,200,200),
                    getWidth(), getHeight(), new Color(240,240,240)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Rounded border for buttons
    private static class RoundedBorder implements Border {
        private int radius;
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius+1, radius+1, radius+1, radius+1);
        }
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(c.getBackground());
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    private void SetFacultyProfile(int user_id, String name,String expertise,String designation)
    {
        String query="INSERT INTO faculty (user_id, name, expertise, designation) VALUES (?, ?, ?, ?)";

        try(Connection conn= DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {

            pstmt.setInt(1,user_id);
            pstmt.setString(2,name);
            pstmt.setString(3,expertise);
            pstmt.setString(4,designation);

            int rowsInserted=pstmt.executeUpdate();

            if(rowsInserted>0)
            {
                System.out.println("Faculty profile saved successfully!");
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    private int checkCourses(String checkCourseCode)
    {
        List<String> courseNames=new ArrayList<>();
        List<String> courseCodes=new ArrayList<>();
        List<Integer> courseIds=new ArrayList<>();
        String courseName, courseCode;
        int courseId;
        int idx=-1;
        String query="SELECT * FROM courses";

        try(Connection conn=DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {

            ResultSet rs=pstmt.executeQuery();
            
            while(rs.next())
            {
                courseId=rs.getInt("id");
                courseName=rs.getString("name");
                courseCode=rs.getString("code");

                courseIds.add(courseId);
                courseNames.add(courseName);
                courseCodes.add(courseCode);
            }
            idx=courseCodes.indexOf(checkCourseCode);
            if(idx!=-1)
            {
                return courseIds.get(idx);
            }
            return -1;

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return idx;
    }

    private void SetFacultyCourses(int faculty_id, int course_id)
    {
        String query="INSERT INTO faculty_courses (faculty_id, course_id) VALUES (?, ?)";

        try(Connection conn= DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {


            pstmt.setInt(1,faculty_id);
            pstmt.setInt(2,course_id);
            
            int rowsInserted=pstmt.executeUpdate();

            if(rowsInserted>0)
            {
                System.out.println("Faculty Courses profile saved successfully!");
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    private int getFacultyId(int user_id)
    {
        int facultyId=-1;
        String query="SELECT id FROM faculty WHERE user_id = ?";

        try(Connection conn=DatabaseConnection.getInstance();
            PreparedStatement pstmt=conn.prepareStatement(query))
        {

            pstmt.setInt(1,user_id);
            ResultSet rs=pstmt.executeQuery();
            
            if(rs.next())
            {
                facultyId=rs.getInt("id");
            }
            return facultyId;

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return facultyId;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyProfilePage(6).setVisible(true);
        });
    }
}

