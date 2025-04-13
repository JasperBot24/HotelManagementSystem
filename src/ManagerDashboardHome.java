import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerDashboardHome extends JFrame {
    private String managerEmail; // Store the manager's email for database queries
    private String managerName; // Store the manager's name for UI updates
    private JLabel welcomeLabel; // Make welcomeLabel an instance variable

    public ManagerDashboardHome(String managerName, String managerEmail) {
        this.managerName = managerName; // Initialize the manager's name
        this.managerEmail = managerEmail; // Initialize the manager's email

        setTitle("Manager Dashboard");
        setSize(1230, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevents direct closing
        setLayout(new BorderLayout());
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                        ManagerDashboardHome.this,
                        "Do you want to exit the program?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0); // Close the application
                }
            }
        });

     // Sidebar panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(5, 1, 0, 5)); // Added 5px vertical gap between buttons
        sidebar.setBackground(new Color(45, 45, 45)); // Dark background
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

     // Color constants
        final Color DEFAULT_BG = new Color(45, 45, 45);
        final Color HOVER_BG = new Color(70, 70, 70);   // Hover effect
        final Color ACTIVE_BG = new Color(255, 204, 0);  // Yellow for active
        final Color TEXT_COLOR = Color.WHITE; // White text for all buttons

        // Sidebar Buttons
        String[] buttonLabels = {"Home", "Room Check", "Check In", "Check Out", "Check History"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(TEXT_COLOR); // Set white text for all buttons
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (label.equals("Home")) {
                // Active state styling
                button.setBackground(ACTIVE_BG);
            } else {
                // Default state styling
                button.setBackground(DEFAULT_BG);
                
                // Hover effects
                button.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent evt) {
                        button.setBackground(HOVER_BG);
                    }
                    public void mouseExited(MouseEvent evt) {
                        button.setBackground(DEFAULT_BG);
                    }
                });

                // Button Actions (unchanged functionality)
                button.addActionListener(e -> {
                    dispose();
                    switch (label) {
                        case "Home":
                            new ManagerDashboardHome(managerName, managerEmail).setVisible(true);
                            break;
                        case "Room Check":
                            new ManagerRoomCheck(managerName, managerEmail).setVisible(true);
                            break;
                        case "Check In":
                            new ManagerCheckIn(managerName, managerEmail).setVisible(true);
                            break;
                        case "Check Out":
                            new ManagerCheckOut(managerName, managerEmail).setVisible(true);
                            break;
                        case "Check History":
                            new ManagerCheckHistory(managerName, managerEmail).setVisible(true);
                            break;
                    }
                });
            }
            
            sidebar.add(button);
        }

        // Main Content Panel with Background Image
        JPanel mainPanel = new HomePanel();
        mainPanel.setLayout(null); // Use absolute positioning for precise placement

        // Welcome Label
        welcomeLabel = new JLabel("Hey " + managerName + ", Welcome to our Hotel Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE); // Set text color to white for better visibility
        welcomeLabel.setBounds(50, 50, 800, 40); // Position and size of the label
        mainPanel.add(welcomeLabel);

        // Manager Info Button
        JButton managerInfoButton = new JButton("Manager Info");
        managerInfoButton.setFont(new Font("Arial", Font.BOLD, 14));
        managerInfoButton.setBackground(new Color(70, 130, 180)); // Blue color
        managerInfoButton.setForeground(Color.WHITE);
        managerInfoButton.setFocusPainted(false);
        managerInfoButton.setBorderPainted(false);
        managerInfoButton.setBounds(900, 20, 150, 40); // Position and size of the button
        managerInfoButton.addActionListener(e -> showManagerInfoFrame());
        mainPanel.add(managerInfoButton);

        // Log Out Button
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setBounds(900, 70, 150, 40); // Position and size of the button
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?",
                    "Confirm Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                dispose(); // Close the current window
                new LoginGUI().setVisible(true); // Open the LoginGUI
            }
        });
        mainPanel.add(logoutButton);

        // Add Components to Frame
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Show Manager Info in a new JFrame
    private void showManagerInfoFrame() {
        JFrame managerInfoFrame = new JFrame("Manager Information");
        managerInfoFrame.setSize(450, 400); // Increased height for additional fields
        managerInfoFrame.setLocationRelativeTo(this);
        managerInfoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        managerInfoFrame.setLayout(new BorderLayout());
        managerInfoFrame.setResizable(false); // Disable resizing

        // Main Panel for Form
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10)); // Increased rows for new fields
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        // Add fields
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField();
        formPanel.add(nameLabel);
        formPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email: ");
        JTextField emailField = new JTextField();
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        JLabel phoneLabel = new JLabel("Phone: ");
        JTextField phoneField = new JTextField();
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);

        JLabel addressLabel = new JLabel("Address: ");
        JTextField addressField = new JTextField();
        formPanel.add(addressLabel);
        formPanel.add(addressField);

        JLabel newPasswordLabel = new JLabel("New Password: ");
        JPasswordField newPasswordField = new JPasswordField();
        formPanel.add(newPasswordLabel);
        formPanel.add(newPasswordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
        JPasswordField confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);

        // Error Label for Password Mismatch
        JLabel passwordErrorLabel = new JLabel("Passwords do not match");
        passwordErrorLabel.setForeground(Color.RED);
        passwordErrorLabel.setVisible(false);
        formPanel.add(new JLabel()); // Empty label for spacing
        formPanel.add(passwordErrorLabel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> {
            // Get values from fields
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            // Validate Full Name (no numbers)
            if (name.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(managerInfoFrame, "Full Name cannot contain numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate Phone Number (starts with 09 and 11 digits)
            if (!phone.matches("09\\d{9}")) {
                JOptionPane.showMessageDialog(managerInfoFrame, "Phone number must start with 09 and be 11 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate password match
            if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                passwordErrorLabel.setVisible(true); // Show error if passwords do not match
                return;
            } else {
                passwordErrorLabel.setVisible(false); // Hide error if passwords match
            }

            // Update manager's information in the database
            if (updateManagerInfo(name, email, phone, address, newPassword)) {
                JOptionPane.showMessageDialog(managerInfoFrame, "Information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Update UI
                managerName = name; // Update the manager's name
                managerEmail = email; // Update the manager's email
                welcomeLabel.setText("Hey " + managerName + ", Welcome to our Hotel Management System");

                // Close the manager info frame
                managerInfoFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(managerInfoFrame, "Failed to update information.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel Button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(e -> managerInfoFrame.dispose());

        // Add buttons to the button panel
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to the frame
        managerInfoFrame.add(formPanel, BorderLayout.CENTER);
        managerInfoFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display manager's current info
        fetchAndDisplayManagerInfo(nameField, emailField, phoneField, addressField);

        managerInfoFrame.setVisible(true);
    }

    // Fetch manager's info from the database and populate the form fields
    private void fetchAndDisplayManagerInfo(JTextField nameField, JTextField emailField, JTextField phoneField, JTextField addressField) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            String query = "SELECT employee_fullname, employee_email, employee_phoneNumber, employee_address FROM employees WHERE employee_email = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, managerEmail); // Use the manager's email to fetch data
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Populate the form fields with the fetched data
                nameField.setText(rs.getString("employee_fullname"));
                emailField.setText(rs.getString("employee_email"));
                phoneField.setText(rs.getString("employee_phoneNumber"));
                addressField.setText(rs.getString("employee_address"));
            } else {
                JOptionPane.showMessageDialog(this, "No data found for the manager.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch manager data.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Update manager's information in the database
    private boolean updateManagerInfo(String name, String email, String phone, String address, String newPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Databaseconnection.getConnection();
            String query;
            if (newPassword.isEmpty()) {
                query = "UPDATE employees SET employee_fullname = ?, employee_email = ?, employee_phoneNumber = ?, employee_address = ? WHERE employee_email = ?";
            } else {
                query = "UPDATE employees SET employee_fullname = ?, employee_email = ?, employee_phoneNumber = ?, employee_address = ?, employee_password = ? WHERE employee_email = ?";
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            if (newPassword.isEmpty()) {
                pstmt.setString(5, managerEmail); // Use the original email for the WHERE clause
            } else {
                pstmt.setString(5, newPassword);
                pstmt.setString(6, managerEmail); // Use the original email for the WHERE clause
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Home Panel with Background Image
    class HomePanel extends JPanel {
        private BufferedImage backgroundImage;

        public HomePanel() {
            setLayout(null); // Use absolute positioning
            try {
                // Load the background image
                backgroundImage = ImageIO.read(getClass().getResource("/backgroundhome1.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the background image scaled to fit the panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerDashboardHome("Manager Name", "manager@example.com").setVisible(true));
    }
}