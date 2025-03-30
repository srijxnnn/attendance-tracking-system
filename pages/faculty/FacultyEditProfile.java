package pages.faculty;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class FacultyEditProfile {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            showProfileDialog("Dr. A", "ABCD", "ABCD@gmail.com", "Professor", "Data Science, AI", "CSC401, CSC402, CSC403, HUC404, MAC401");
        });
    }

    /**
     * Displays a custom JOptionPane with gradient background,
     * circular profile placeholder, read-only textfields for name/regd/roll,
     * and editable textfields for semester & courses, plus a Save button.
     */
    public static void showProfileDialog(String username, String name, String regdNo, String rollNo,
                                         String semester, String courses) {

        // Create a custom panel with null layout
        ProfilePanel panel = new ProfilePanel(username, name, regdNo, rollNo, semester, courses);

        // The JOptionPane doesn't allow direct setting of a custom background easily.
        // But we can show our custom panel as the "message" content.
        // We use showOptionDialog to remove the standard buttons.
        JOptionPane.showOptionDialog(
                null,
                panel,
                "Edit Profile",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{}, // no standard buttons
                null
        );
    }

    /**
     * A custom panel for the dialog's content.
     * Null layout + gradient background + fade-in + hover effect on Save button.
     */
    private static class ProfilePanel extends JPanel {
        private float alpha = 0f; // for fade-in effect
        private Timer fadeTimer;

        // Non-editable fields
        private JTextField usernameField;
        private JTextField nameField;
        private JTextField regdField;
        private JTextField rollField;

        // Editable fields
        private JTextField semesterField;
        private JTextArea coursesArea;

        private JButton saveButton;

        public ProfilePanel(String username, String name, String regdNo, String rollNo,
                            String semester, String courses) {
            setLayout(null);
            setPreferredSize(new Dimension(600, 300)); // Adjust as needed
            setOpaque(false);

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

            // 1) Circular placeholder for profile pic
            // We'll paint it in paintComponent() or create a sub-panel for it.
            // Let's just create a small sub-panel for the circle
            JPanel picPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.LIGHT_GRAY);
                    int d = Math.min(getWidth(), getHeight()) - 10;
                    int x = (getWidth() - d) / 2;
                    int y = (getHeight() - d) / 2;
                    g2.fillOval(x, y, d, d);
                }
            };
            picPanel.setBounds(20, 20, 100, 100);
            picPanel.setOpaque(false);
            add(picPanel);

            // 2) Username label (non-editable)
            usernameField = new JTextField(username);
            usernameField.setBounds(20, 130, 100, 25);
            usernameField.setFont(new Font("SansSerif", Font.BOLD, 12));
            usernameField.setHorizontalAlignment(JTextField.CENTER);
            usernameField.setEditable(false);
            add(usernameField);

            // 3) Container for text labels and text fields on the right
            // We'll just place them directly on this panel for simplicity
            // Non-editable textfields
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            nameLabel.setBounds(150, 30, 80, 20);
            add(nameLabel);

            nameField = new JTextField(name);
            nameField.setBounds(230, 30, 120, 25);
            nameField.setEditable(false);
            add(nameField);

            JLabel regdLabel = new JLabel("Email.:");
            regdLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            regdLabel.setBounds(370, 30, 80, 20);
            add(regdLabel);

            regdField = new JTextField(regdNo);
            regdField.setBounds(450, 30, 120, 25);
            regdField.setEditable(false);
            add(regdField);

            JLabel rollLabel = new JLabel("Designation.:");
            rollLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            rollLabel.setBounds(150, 70, 80, 20);
            add(rollLabel);

            rollField = new JTextField(rollNo);
            rollField.setBounds(230, 70, 120, 25);
            rollField.setEditable(false);
            add(rollField);

            // Editable textfields
            JLabel semLabel = new JLabel("Areas of Expertise:");
            semLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            semLabel.setBounds(370, 70, 80, 20);
            add(semLabel);

            semesterField = new JTextField(semester);
            semesterField.setBounds(450, 70, 120, 25);
            add(semesterField);

            JLabel coursesLabel = new JLabel("Courses:");
            coursesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            coursesLabel.setBounds(150, 110, 80, 20);
            add(coursesLabel);

            coursesArea = new JTextArea(courses);
            coursesArea.setLineWrap(true);
            coursesArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(coursesArea);
            scrollPane.setBounds(230, 110, 340, 60);
            // Hide the scroll bar UI to make it minimal
            scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {}
                @Override
                protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
                @Override
                protected JButton createIncreaseButton(int orientation) { return zeroButton(); }
                private JButton zeroButton() {
                    JButton btn = new JButton();
                    btn.setPreferredSize(new Dimension(0,0));
                    return btn;
                }
                @Override
                protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
                @Override
                protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {}
            });
            add(scrollPane);

            // 4) Save button with hover effect
            saveButton = new JButton("SAVE");
            saveButton.setBounds(360, 190, 80, 30);
            styleButton(saveButton);
            addHoverEffect(saveButton);
            add(saveButton);

            // You could add an ActionListener for "saveButton" to handle saving logic
            saveButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Data Saved!");
            });
        }

        // We override paintComponent to draw a gradient background + fade alpha
        @Override
        protected void paintComponent(Graphics g) {
            // fade in
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // gradient
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(44, 62, 80),
                    getWidth(), getHeight(), new Color(189, 195, 199)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();

            super.paintComponent(g);
        }

        // Basic button style
        private void styleButton(JButton btn) {
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setBackground(new Color(0,123,255));
            btn.setForeground(Color.WHITE);
            btn.setBorder(new LineBorder(new Color(0,123,255), 2));
        }

        // Hover effect to lighten the button color
        private void addHoverEffect(JButton btn) {
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(200,200,200));
                    btn.setForeground(Color.BLACK);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(0,123,255));
                    btn.setForeground(Color.WHITE);
                }
            });
        }
    }
}