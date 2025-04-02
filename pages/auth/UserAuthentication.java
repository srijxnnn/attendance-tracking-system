package pages.auth;

import pages.faculty.FacultyDashboard;
import pages.student.StudentDashboard;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;

public class UserAuthentication extends JFrame
{
    public GradientPanel formPanel;
    public GradientPanel logoPanel;
    public ImageIcon img;
    private String[] options={"Faculty", "Student"};
    private JComboBox<String> loginTypeCombo;
    //Signup -> True ;; Signin -> False
    public boolean isRegisterMode=true;

    public UserAuthentication()
    {
        setTitle("User Authentication");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        formPanel=new GradientPanel(new Color(220, 0, 100), new Color(0, 0, 0));
        formPanel.setLayout(null);
        formPanel.setBounds(0, 0, 500, 600);

        img=new ImageIcon("pages/auth/Presenz.png");
        Image scaledImg=img.getImage().getScaledInstance(500, 600, Image.SCALE_SMOOTH);
        img=new ImageIcon(scaledImg);
        logoPanel=new GradientPanel(new Color(100, 149, 237), new Color(65, 105, 225));
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBounds(500, 0, 500, 600);

        JLabel logoLabel=new JLabel();
        logoLabel.setFont(new Font("Segoe Script", Font.BOLD, 48));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setIcon(img);
        logoPanel.add(logoLabel, BorderLayout.CENTER);

        add(formPanel);
        add(logoPanel);


        buildRegisterForm();
    }


    public void buildRegisterForm()
    {
        formPanel.removeAll();

        JLabel title=new JLabel("Create Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(0, 0, 0));
        title.setBounds(50, 30, 300, 40);
        formPanel.add(title);

        JLabel userLabel=new JLabel("Username :");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        userLabel.setForeground(new Color(0, 0, 0));
        userLabel.setBounds(50, 100, 300, 25);
        formPanel.add(userLabel);

        JTextField userField=new JTextField();
        userField.setBounds(50, 130, 300, 35);
        userField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        userField.setBackground(new Color(250, 250, 255));
        formPanel.add(userField);

        JLabel emailLabel=new JLabel("Email :");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        emailLabel.setForeground(new Color(0, 0, 0));
        emailLabel.setBounds(50, 180, 300, 25);
        formPanel.add(emailLabel);

        JTextField emailField=new JTextField();
        emailField.setBounds(50, 210, 300, 35);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        emailField.setBackground(new Color(250, 250, 255));
        formPanel.add(emailField);

        JLabel passLabel=new JLabel("Password :");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passLabel.setForeground(new Color(0, 0, 0));
        passLabel.setBounds(50, 260, 300, 25);
        formPanel.add(passLabel);

        JPasswordField passField=new JPasswordField();
        passField.setBounds(50, 290, 300, 35);
        passField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passField.setBackground(new Color(250, 250, 255));
        formPanel.add(passField);

        JLabel loginTypeLabel=new JLabel("Login Type :");
        loginTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        loginTypeLabel.setForeground(new Color(0, 0, 0));
        loginTypeLabel.setBounds(50, 340, 300, 25);
        formPanel.add(loginTypeLabel);

        loginTypeCombo=new JComboBox<>(options);
        loginTypeCombo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        loginTypeCombo.setBounds(50, 370, 300, 35);
        loginTypeCombo.setSelectedIndex(0);
        formPanel.add(loginTypeCombo);

        GradientButton registerButton=new GradientButton("Register",
                new Color(65, 105, 225), new Color(30, 144, 255),
                new Color(0, 144, 255), new Color(0, 0, 25));
        registerButton.setBounds(50, 430, 300, 45);

        registerButton.addActionListener(e->
        {
            String username=userField.getText();
            String email=emailField.getText();
            String password=new String(passField.getPassword());
            String role=loginTypeCombo.getSelectedItem().toString().toLowerCase();

            System.out.println("Username: "+username);
            System.out.println("Email: "+email);
            System.out.println("Password: "+password);
            System.out.println("Role: "+role);

            userField.setText("");
            emailField.setText("");
            passField.setText("");


            if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty())
            {
                SwingUtilities.invokeLater(() -> OtpVerificationDialog.showOtpDialog(username, email, password, role));
                // RegisterUser registerUser=new RegisterUser();
                // registerUser.register(username, email, password, role);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });
        formPanel.add(registerButton);

        JLabel switchLabel=new JLabel("<HTML><U>Already have an account? Sign In</U></HTML>");
        switchLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        switchLabel.setForeground(new Color(100, 100, 200));
        switchLabel.setBounds(50, 490, 300, 25);
        switchLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                animateTransition(true);
            }
        });

        formPanel.add(switchLabel);

        formPanel.revalidate();
        formPanel.repaint();
    }


    public void buildSignInForm()
    {
        formPanel.removeAll();

        JLabel title=new JLabel("Welcome Back");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(0, 0, 0));
        title.setBounds(50, 30, 300, 40);
        formPanel.add(title);

        JLabel emailLabel=new JLabel("Email :");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        emailLabel.setForeground(new Color(0, 0, 0));
        emailLabel.setBounds(50, 100, 300, 25);
        formPanel.add(emailLabel);

        JTextField emailField=new JTextField();
        emailField.setBounds(50, 130, 300, 35);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        emailField.setBackground(new Color(250, 250, 255));
        formPanel.add(emailField);

        JLabel passLabel=new JLabel("Password :");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passLabel.setForeground(new Color(0, 0, 0));
        passLabel.setBounds(50, 180, 300, 25);
        formPanel.add(passLabel);

        JPasswordField passField=new JPasswordField();
        passField.setBounds(50, 210, 300, 35);
        passField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passField.setBackground(new Color(250, 250, 255));
        formPanel.add(passField);

        GradientButton signInButton=new GradientButton("Sign In",
                new Color(65, 105, 225), new Color(30, 144, 255),
                new Color(0, 144, 255), new Color(0, 0, 25));
        signInButton.setBounds(50, 270, 300, 45);

        signInButton.addActionListener((actionEvent)->
        {
            String email=emailField.getText();
            String password=new String(passField.getPassword());

            System.out.println("Email : "+email);
            System.out.println("Password : "+password);

            emailField.setText("");
            passField.setText("");

            RegisterUser registerUser=new RegisterUser();
            boolean isAuthenticated=registerUser.login(email, password);

            if(isAuthenticated)
            {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                int userID=registerUser.getUserID();
                String logedInUserName=registerUser.getUserName();
                String logedInEmail=registerUser.getUserEmail();
                String role=registerUser.getRoleType();

                if(role.equals("faculty"))
                {
                    SwingUtilities.invokeLater(() ->
                    {
                        new FacultyDashboard(userID).setVisible(true);
                    });
                } else if (role.equals("student")) {
                    SwingUtilities.invokeLater(() ->
                    {
                        new StudentDashboard(userID).setVisible(true);
                    });
                }

            }
            else
            {
                JOptionPane.showMessageDialog(null, "Invalid Email or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(signInButton);

        JLabel switchLabel=new JLabel("<HTML><U>Don't have an account? Register</U></HTML>");
        switchLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        switchLabel.setForeground(new Color(100, 100, 200));
        switchLabel.setBounds(50, 330, 300, 25);
        switchLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                animateTransition(false);
            }
        });
        formPanel.add(switchLabel);

        formPanel.revalidate();
        formPanel.repaint();
    }

    public void animateTransition(boolean toSignIn)
    {
        int startFormX=formPanel.getX();
        int endFormX=toSignIn ? 500 : 0;
        int startLogoX=logoPanel.getX();
        int endLogoX=toSignIn ? 0 : 500;

        Timer timer=new Timer(10, null);
        timer.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int currentFormX=formPanel.getX();
                int currentLogoX=logoPanel.getX();
                int step=20;
                boolean done=false;
                if(toSignIn)
                {
                    if(currentFormX<endFormX)
                    {
                        formPanel.setLocation(Math.min(currentFormX+step, endFormX), 0);
                        logoPanel.setLocation(Math.max(currentLogoX-step, endLogoX), 0);
                    }
                    else
                    {
                        done=true;
                    }
                }
                else
                {
                    if(currentFormX>endFormX)
                    {
                        formPanel.setLocation(Math.max(currentFormX-step, endFormX), 0);
                        logoPanel.setLocation(Math.min(currentLogoX+step, endLogoX), 0);
                    }
                    else
                    {
                        done=true;
                    }
                }
                if(done)
                {
                    timer.stop();
                    if(toSignIn)
                    {
                        buildSignInForm();
                    }
                    else
                    {
                        buildRegisterForm();
                    }
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            new UserAuthentication().setVisible(true);
        });
    }
}


class GradientPanel extends JPanel
{
    public Color color1;
    public Color color2;

    public GradientPanel(Color c1, Color c2)
    {
        this.color1=c1;
        this.color2=c2;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2=(Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp=new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}

class GradientButton extends JButton
{
    public Color color1;
    public Color color2;
    public Color hoverColor1;
    public Color hoverColor2;
    public boolean hover;

    public GradientButton(String text, Color color1, Color color2, Color hoverColor1, Color hoverColor2)
    {
        super(text);
        this.color1=color1;
        this.color2=color2;
        this.hoverColor1=hoverColor1;
        this.hoverColor2=hoverColor2;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 16));
        setOpaque(false);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                hover=true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                hover=false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2=(Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color start=hover ? hoverColor1 : color1;
        Color end=hover ? hoverColor2 : color2;
        GradientPaint gp=new GradientPaint(0, 0, start, 0, getHeight(), end);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
        super.paintComponent(g);
    }
}

