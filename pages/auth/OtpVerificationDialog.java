package pages.auth;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import pages.faculty.FacultyDashboard;
import pages.student.StudentDashboard;

public class OtpVerificationDialog 
{

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> OtpVerificationDialog.showOtpDialog("username", "email", "password", "student"));
    }

    public static void showOtpDialog(String username, String email, String password, String role) 
    {
        OtpPanel panel=new OtpPanel(username, email, password, role);
        JOptionPane.showOptionDialog(
                null,
                panel,
                "OTP Verification",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{},
                null
        );
    }

    private static class OtpPanel extends JPanel 
    {
        private float alpha = 0f; // for fade-in effect
        private Timer fadeTimer;
        private boolean phoneHover = false;
        private JLabel phoneLabel; // will hold our mobile.png image
        private JTextField[] otpFields = new JTextField[6];
        private JButton verifyButton;
        private String generatedOTP; // Store OTP for verification
        private OTPService otpService; // OTP service object
        // private String reviever = "cse23058@iiitkalyani.ac.in";
        private String username;
        private String email;
        private String password;
        private String role;

        public OtpPanel(String username, String email, String password, String role) 
        {
            this.username = username;
            this.email = email;
            this.password = password;
            this.role = role;
            setLayout(null);
            setPreferredSize(new Dimension(450, 400));
            setOpaque(false);

            otpService = new OTPService(); // Create OTPService object
            generatedOTP = OTPService.generateOTP(); // Generate OTP
            OTPService.sendOTP(email, generatedOTP); // Send OTP on dialog open
            System.out.println(generatedOTP);

            // Start fade-in timer
            fadeTimer = new Timer(20, e -> {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadeTimer.stop();
                }
                repaint();
            });
            fadeTimer.start();

            // Instead of drawing the phone, load the image "mobile.png"
            // Ensure that "mobile.png" is in your resource folder.
            ImageIcon phoneIcon = new ImageIcon(getClass().getResource("isolated-phone-icon-png.png"));
            // Scale the image to fit the desired dimensions (100x170)
            Image scaledImage = phoneIcon.getImage().getScaledInstance(100, 170, Image.SCALE_SMOOTH);
            phoneIcon = new ImageIcon(scaledImage);
            phoneLabel = new JLabel(phoneIcon);
            phoneLabel.setBounds(175, 20, 100, 170);
            add(phoneLabel);

            // OTP Verification label
            JLabel otpLabel = new JLabel("OTP VERIFICATION", SwingConstants.CENTER);
            otpLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            otpLabel.setForeground(Color.WHITE);
            otpLabel.setBounds(0, 190, getPreferredSize().width, 30);
            add(otpLabel);

            // Instruction label
            JLabel instructionLabel = new JLabel("Code has been sent to your email", SwingConstants.CENTER);
            instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            instructionLabel.setForeground(Color.WHITE);
            instructionLabel.setBounds(0, 230, getPreferredSize().width, 25);
            add(instructionLabel);

            // OTP Fields: 6 text fields arranged horizontally
            int fieldWidth = 40;
            int fieldHeight = 40;
            int spacing = 10;
            int startX = (getPreferredSize().width - (fieldWidth * 6 + spacing * 5)) / 2;
            int otpY = 260;
            for (int i = 0; i < 6; i++) {
                JTextField tf = new JTextField();
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setFont(new Font("SansSerif", Font.BOLD, 24));
                tf.setBounds(startX + i * (fieldWidth + spacing), otpY, fieldWidth, fieldHeight);
                // Limit to 1 character using DocumentFilter.
                ((AbstractDocument) tf.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                            throws BadLocationException {
                        if (fb.getDocument().getLength() + string.length() <= 1) {
                            super.insertString(fb, offset, string, attr);
                        }
                    }
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                            throws BadLocationException {
                        if (fb.getDocument().getLength() - length + text.length() <= 1) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });
                // Auto move focus to next field when a digit is entered.
                tf.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    private void update() {
                        if (tf.getText().length() == 1) {
                            for (int j = 0; j < otpFields.length - 1; j++) {
                                if (otpFields[j] == tf) {
                                    otpFields[j + 1].requestFocusInWindow();
                                    break;
                                }
                            }
                        }
                    }
                    @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
                    @Override public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        // If field becomes empty, move focus back if needed.
                        if(tf.getText().isEmpty()){
                            for(int j = 1; j < otpFields.length; j++){
                                if(otpFields[j] == tf){
                                    otpFields[j - 1].requestFocusInWindow();
                                    break;
                                }
                            }
                        }
                    }
                    @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
                });
                otpFields[i] = tf;
                add(tf);
            }

            // Resend label: centered, with "Resend" bold and blue; clickable with hand cursor.
            JLabel resendLabel = new JLabel("<html><div style='text-align: center;'>Didn't get the OTP? <b><font color='blue'>Resend</font></b></div></html>", SwingConstants.CENTER);
            resendLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            resendLabel.setBounds(0, otpY + 50, getPreferredSize().width, 20);
            resendLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            resendLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(OtpPanel.this, "Otp Resent, Check your Inbox!");
                    generatedOTP = OTPService.generateOTP(); // Generate OTP
                    OTPService.sendOTP(email, generatedOTP); // Send OTP on dialog open
                    System.out.println(generatedOTP);
                }
            });
            add(resendLabel);

            // Verify button: rounded corners (25% radius) and centered.
            verifyButton = new JButton("VERIFY");
            verifyButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            verifyButton.setForeground(Color.WHITE);
            verifyButton.setBackground(new Color(33, 150, 243));
            verifyButton.setFocusPainted(false);
            int btnWidth = 120, btnHeight = 40;
            verifyButton.setBounds((getPreferredSize().width - btnWidth) / 2, otpY + 80, btnWidth, btnHeight);
            verifyButton.setBorder(new RoundedBorder(btnHeight / 4));
            addHoverEffect(verifyButton);
            add(verifyButton);

            verifyButton.addActionListener(e -> {
                StringBuilder sb = new StringBuilder();
                for (JTextField tf : otpFields) {
                    sb.append(tf.getText());
                }
                String enteredOtp = sb.toString();
            
                if (enteredOtp.equals(generatedOTP)) {
                    JOptionPane.showMessageDialog(this, "Registered Successfully");
                    RegisterUser registerUser=new RegisterUser();
                    registerUser.register(username, email, password, role);
                    int userID = registerUser.getUserID();

                    Window window = SwingUtilities.getWindowAncestor(OtpPanel.this);
                    if (window != null) {
                        window.dispose();
                    }

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

                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect OTP! Please enter the correct OTP.", "Error", JOptionPane.ERROR_MESSAGE);
                    // Clear OTP fields
                    for (JTextField tf : otpFields) {
                        tf.setText("");
                    }
                    
                    // Refocus on first field
                    otpFields[0].requestFocusInWindow();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Draw fade-in gradient background
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(44, 62, 80),
                    getWidth(), getHeight(), new Color(189, 195, 199)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
            super.paintComponent(g);
        }

        private void addHoverEffect(JButton btn) {
            Color normalBg = btn.getBackground();
            Color hoverBg = normalBg.brighter();
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(hoverBg);
                    btn.setForeground(Color.BLACK);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(normalBg);
                    btn.setForeground(Color.WHITE);
                }
            });
        }

        // Rounded border for the button
        private class RoundedBorder implements Border {
            private int radius;
            public RoundedBorder(int radius) {
                this.radius = radius;
            }
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(this.radius+1, this.radius+1, this.radius+1, this.radius+1);
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
    }
}
