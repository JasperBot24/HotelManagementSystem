import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LoginForm extends JPanel {
    private int failedAttempts = 0;
    private long lastFailedAttemptTime = 0;
    private static final int MAX_ATTEMPTS = 3;
    private static final int COOLDOWN_MINUTES = 1;
    private String generatedOTP = "";
    private Timer cooldownTimer;
    private JButton loginButton;
    private JButton forgotPasswordButton;

    public LoginForm(JPanel mainPanel, JLabel titleLabel) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setBackground(new Color(245, 245, 220));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and Fields
        String[] labels = {"Email Address:", "Password:"};
        JTextField[] textFields = new JTextField[labels.length];
        JPasswordField passwordField = new JPasswordField(20);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.LINE_END;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            add(label, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            if (labels[i].contains("Password")) {
                passwordField.setPreferredSize(new Dimension(230, 28));
                passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

                ImageIcon eyeIcon = null;
                try {
                    eyeIcon = new ImageIcon(getClass().getResource("/icons8-eye50.png"));
                } catch (Exception e) {
                    System.err.println("Error loading eye icon: " + e.getMessage());
                }

                JToggleButton toggleButton = new JToggleButton();
                if (eyeIcon != null) {
                    ImageIcon eyeIconScaled = new ImageIcon(eyeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    toggleButton.setIcon(eyeIconScaled);
                } else {
                    toggleButton.setText("👁");
                }
                toggleButton.setPreferredSize(new Dimension(40, 28));
                toggleButton.setFocusPainted(false);
                toggleButton.setBorder(BorderFactory.createEmptyBorder());
                toggleButton.setBackground(new Color(44, 62, 80));
                toggleButton.setForeground(Color.WHITE);

                toggleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (toggleButton.isSelected()) {
                            passwordField.setEchoChar((char) 0);
                            toggleButton.setBackground(new Color(212, 180, 15));
                        } else {
                            passwordField.setEchoChar('•');
                            toggleButton.setBackground(new Color(44, 62, 80));
                        }
                    }
                });

                JPanel passwordPanel = new JPanel(new BorderLayout());
                passwordPanel.add(passwordField, BorderLayout.CENTER);
                passwordPanel.add(toggleButton, BorderLayout.EAST);
                add(passwordPanel, gbc);

                textFields[i] = passwordField;
            } else {
                textFields[i] = new JTextField(20);
                textFields[i].setPreferredSize(new Dimension(230, 28));
                textFields[i].setFont(new Font("Arial", Font.PLAIN, 14));
                add(textFields[i], gbc);
            }
        }

        // Forgot Password Button
        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordButton.setBorder(BorderFactory.createEmptyBorder());
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setVisible(true);

        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(forgotPasswordButton, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 220));

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(44, 62, 80));
        backButton.setForeground(Color.WHITE);

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(212, 180, 15));
        loginButton.setForeground(Color.BLACK);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                titleLabel.setText("HOTEL MANAGEMENT SYSTEM");
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "mainMenu");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = textFields[0].getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // Check if in cooldown
                if (isInCooldown()) {
                    long remainingSeconds = (lastFailedAttemptTime + COOLDOWN_MINUTES * 60 * 1000 - System.currentTimeMillis()) / 1000;
                    JOptionPane.showMessageDialog(null, 
                        "Too many failed attempts. Please try again in " + remainingSeconds + " seconds.", 
                        "Account Locked", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validate email format
                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] userDetails = validateCredentials(email, password);
                if (userDetails != null) {
                    // Successful login - reset attempts
                    failedAttempts = 0;
                    forgotPasswordButton.setVisible(false);
                    
                    String fullName = userDetails[0];
                    String role = userDetails[1];

                    JOptionPane.showMessageDialog(null, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(LoginForm.this);
                    topFrame.dispose();

                    if (role.equals("customer")) {
                        CustomerDashboard dashboard = new CustomerDashboard(fullName, email);
                        dashboard.setVisible(true);
                    } else if (role.equals("manager")) {
                        ManagerDashboardHome dashboard = new ManagerDashboardHome(fullName, email);
                        dashboard.setVisible(true);
                    } else if (role.equals("admin")) {
                        AdminDashboard dashboard = new AdminDashboard(fullName, email);
                        dashboard.setVisible(true);
                    } else if (role.equals("employee")) {
                        JOptionPane.showMessageDialog(null, 
                            "<html><b>Access Denied:</b><br>You don't have permission to access this dashboard.<br><br>" +
                            "Please contact your administrator for assistance.</html>", 
                            "Access Restricted", JOptionPane.WARNING_MESSAGE);
                        new LoginGUI().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid role.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    failedAttempts++;
                    lastFailedAttemptTime = System.currentTimeMillis();
                    
                    if (failedAttempts >= MAX_ATTEMPTS) {
                        startCooldown();
                        JOptionPane.showMessageDialog(null, 
                            "Too many failed attempts. Please try again after " + COOLDOWN_MINUTES + " minute.", 
                            "Account Locked", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, 
                            "Invalid email or password. Attempts remaining: " + (MAX_ATTEMPTS - failedAttempts), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        forgotPasswordButton.setVisible(true);
                    }
                }
            }
        });

     // Forgot Password Action - Customer Only with Dummy Number
        forgotPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = textFields[0].getText().trim();
                
                // Validate email format
                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(null, 
                        "Please enter a valid email address first.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check user type first (customer/employee/admin)
                String userType = getUserTypeByEmail(email);
                
                // Handle non-customer cases
                if (userType == null) {
                    JOptionPane.showMessageDialog(null, 
                        "This email is not registered.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!userType.equals("customer")) {
                    JOptionPane.showMessageDialog(null, 
                        "<html><b>Password reset unavailable:</b><br>" +
                        "Employees and Administrators should contact<br>" +
                        "their System Administrator for password assistance.</html>", 
                        "Access Restricted", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // For customers only - get dummy phone number
                String phoneNumber = getPhoneNumberByEmail(email);
                
                // This check is redundant since we already verified customer status,
                // but kept for safety
                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(null, 
                        "No phone number found for this account.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Generate and display OTP (demo only)
                generatedOTP = generateOTP();
                
                JOptionPane.showMessageDialog(null, 
                    "OTP would be sent to: " + phoneNumber + 
                    "\n\nDemo OTP Number: " + generatedOTP + 
                    "\n\n(In real system, OTP would be sent to ****" + 
                    phoneNumber.substring(phoneNumber.length() - 4) + ")", 
                    "OTP Verification", JOptionPane.INFORMATION_MESSAGE);
                
                // Show password reset dialog
                showPasswordResetDialog(email);
            }
        });

        textFields[0].addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }

    private boolean isInCooldown() {
        return failedAttempts >= MAX_ATTEMPTS && 
               (System.currentTimeMillis() - lastFailedAttemptTime) < COOLDOWN_MINUTES * 60 * 1000;
    }

    private void startCooldown() {
        loginButton.setEnabled(false);
        if (cooldownTimer != null) {
            cooldownTimer.cancel();
        }
        
        cooldownTimer = new Timer();
        cooldownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    failedAttempts = 0;
                    loginButton.setEnabled(true);
                    forgotPasswordButton.setVisible(false);
                });
            }
        }, COOLDOWN_MINUTES * 60 * 1000);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean emailExists(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = Databaseconnection.getConnection();
            
            // Check in customers table
            String customerQuery = "SELECT 1 FROM customers WHERE customer_email = ?";
            pstmt = conn.prepareStatement(customerQuery);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) return true;
            
            // Check in employees table
            String employeeQuery = "SELECT 1 FROM employees WHERE employee_email = ?";
            pstmt = conn.prepareStatement(employeeQuery);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    private String getPhoneNumberByEmail(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = Databaseconnection.getConnection();
            
            // Query to get customer's phone number from database
            String query = "SELECT customer_phoneNumber FROM customers WHERE customer_email = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String phoneNumber = rs.getString("customer_phoneNumber");
                
                // Basic validation - ensure phone number exists and is not empty
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    // Clean the phone number by removing any non-digit characters
                    phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
                    
                    // Return the cleaned phone number if it has digits
                    if (!phoneNumber.isEmpty()) {
                        return phoneNumber;
                    }
                }
            }
            return null; // Return null if no valid phone number found
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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

    private String getUserTypeByEmail(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = Databaseconnection.getConnection();
            
            // Check in customers table first
            String customerQuery = "SELECT 'customer' AS user_type FROM customers WHERE customer_email = ?";
            pstmt = conn.prepareStatement(customerQuery);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) return "customer";
            
            // Check in employees table
            String employeeQuery = "SELECT employee_role FROM employees WHERE employee_email = ?";
            pstmt = conn.prepareStatement(employeeQuery);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("employee_role").toLowerCase();
                return role.equals("admin") || role.equals("manager") ? "admin" : "employee";
            }
            
            return null; // Email not found
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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

	private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private void showPasswordResetDialog(String email) {
        int otpAttempts = 0;
        final int MAX_OTP_ATTEMPTS = 3;
        JPanel panel = new JPanel(new GridLayout(0, 1));
        
        JTextField otpField = new JTextField(6);
        
        // New Password Field with Eye Icon
        JPasswordField newPasswordField = new JPasswordField(20);
        JPanel newPasswordPanel = createPasswordFieldWithEyeIcon(newPasswordField);
        
        // Confirm Password Field with Eye Icon
        JPasswordField confirmPasswordField = new JPasswordField(20);
        JPanel confirmPasswordPanel = createPasswordFieldWithEyeIcon(confirmPasswordField);
        
        panel.add(new JLabel("Enter OTP:"));
        panel.add(otpField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordPanel);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordPanel);
        
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Reset Password", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            // Handle dialog closure (X button, cancel, or OK)
            if (result != JOptionPane.OK_OPTION) {
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Are you sure you want to cancel password reset?", 
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    generatedOTP = ""; // Clear OTP
                    return;
                } else {
                    continue; // Show dialog again
                }
            }
            
            // User clicked OK - validate inputs
            String enteredOTP = otpField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            
            // Validate OTP
            if (!enteredOTP.equals(generatedOTP)) {
                otpAttempts++;
                int remainingAttempts = MAX_OTP_ATTEMPTS - otpAttempts;
                
                if (remainingAttempts > 0) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid OTP. Attempts remaining: " + remainingAttempts, 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    otpField.setText(""); // Clear OTP field
                    otpField.requestFocus();
                    continue;
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Maximum OTP attempts reached. Please request a new OTP.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    generatedOTP = ""; // Clear OTP
                    return;
                }
            }
            
            // Validate password match
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, 
                    "Passwords do not match. Please re-enter your password.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                newPasswordField.requestFocus();
                continue;
            }
            
            // Validate password length
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(null, 
                    "Password must be at least 6 characters.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                newPasswordField.requestFocus();
                continue;
            }
            
            // All validations passed - update password
            if (updatePassword(email, newPassword)) {
                JOptionPane.showMessageDialog(null, 
                    "Password updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                generatedOTP = ""; // Clear OTP after successful reset
                return;
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Failed to update password. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private JPanel createPasswordFieldWithEyeIcon(JPasswordField passwordField) {
        passwordField.setPreferredSize(new Dimension(230, 28));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Create eye icon toggle button
        ImageIcon eyeIcon = null;
        try {
            eyeIcon = new ImageIcon(getClass().getResource("/icons8-eye50.png"));
        } catch (Exception e) {
            System.err.println("Error loading eye icon: " + e.getMessage());
        }
        
        JToggleButton toggleButton = new JToggleButton();
        if (eyeIcon != null) {
            ImageIcon eyeIconScaled = new ImageIcon(eyeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            toggleButton.setIcon(eyeIconScaled);
        } else {
            toggleButton.setText("👁");
        }
        toggleButton.setPreferredSize(new Dimension(40, 28));
        toggleButton.setFocusPainted(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder());
        toggleButton.setBackground(new Color(44, 62, 80));
        toggleButton.setForeground(Color.WHITE);
        
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggleButton.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                    toggleButton.setBackground(new Color(212, 180, 15));
                } else {
                    passwordField.setEchoChar('•');
                    toggleButton.setBackground(new Color(44, 62, 80));
                }
            }
        });
        
        // Create panel to hold password field and toggle button
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(toggleButton, BorderLayout.EAST);
        
        return passwordPanel;
    }

    private boolean updatePassword(String email, String newPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = Databaseconnection.getConnection();
            
            // Try updating in customers table
            String customerUpdate = "UPDATE customers SET customer_password = ? WHERE customer_email = ?";
            pstmt = conn.prepareStatement(customerUpdate);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            int customerRows = pstmt.executeUpdate();
            if (customerRows > 0) return true;
            
            // Try updating in employees table
            String employeeUpdate = "UPDATE employees SET employee_password = ? WHERE employee_email = ?";
            pstmt = conn.prepareStatement(employeeUpdate);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            int employeeRows = pstmt.executeUpdate();
            return employeeRows > 0;
            
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

    private String[] validateCredentials(String email, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] userDetails = null;

        try {
            conn = Databaseconnection.getConnection();

            String customerQuery = "SELECT customer_fullname, 'customer' AS role FROM customers WHERE customer_email = ? AND customer_password = ?";
            pstmt = conn.prepareStatement(customerQuery);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                userDetails = new String[]{rs.getString("customer_fullname"), rs.getString("role")};
            } else {
                String employeeQuery = "SELECT employee_fullname, employee_role FROM employees WHERE employee_email = ? AND employee_password = ?";
                pstmt = conn.prepareStatement(employeeQuery);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("employee_role").toLowerCase();
                    if (role.equals("admin") || role.equals("manager")) {
                        userDetails = new String[]{rs.getString("employee_fullname"), role};
                    } else {
                        userDetails = new String[]{rs.getString("employee_fullname"), "employee"};
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database error occurred. Please contact administrator.", 
                "System Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userDetails;
    }
}