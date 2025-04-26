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
import java.util.regex.Pattern;

public class RegistrationForm extends JPanel {

    public RegistrationForm(JPanel mainPanel, JLabel titleLabel) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); // Padding for a clean UI
        setBackground(new Color(245, 245, 220)); // Soft beige

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and Fields
        String[] labels = {"Full Name:", "Email Address:", "Phone Number:", "Address:", "Password:", "Confirm Password:"};
        JTextField[] textFields = new JTextField[labels.length];
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.LINE_END;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            add(label, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            if (labels[i].contains("Password")) {
                // Create a password field
                JPasswordField passwordFieldToUse = (i == 4) ? passwordField : confirmPasswordField;
                passwordFieldToUse.setPreferredSize(new Dimension(230, 28));
                passwordFieldToUse.setFont(new Font("Arial", Font.PLAIN, 14));

                // Load the eye icon for the toggle button
                ImageIcon eyeIcon = null;
                try {
                    // Use a relative path to load the icon
                    eyeIcon = new ImageIcon(getClass().getResource("/icons8-eye50.png"));
                } catch (Exception e) {
                    System.err.println("Error loading eye icon: " + e.getMessage());
                }

                // Create a toggle button with the eye icon (if loaded)
                JToggleButton toggleButton = new JToggleButton();
                if (eyeIcon != null) {
                    ImageIcon eyeIconScaled = new ImageIcon(eyeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    toggleButton.setIcon(eyeIconScaled);
                } else {
                    toggleButton.setText("👁"); // Fallback to text if icon fails to load
                    System.out.println("Eye icon not found. Using fallback text.");
                }
                toggleButton.setPreferredSize(new Dimension(40, 28));
                toggleButton.setFocusPainted(false);
                toggleButton.setBorder(BorderFactory.createEmptyBorder());
                toggleButton.setBackground(new Color(44, 62, 80)); // Default background color
                toggleButton.setForeground(Color.WHITE); // Default text color

                // Add action listener to toggle button
                toggleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (toggleButton.isSelected()) {
                            passwordFieldToUse.setEchoChar((char) 0); // Show password
                            toggleButton.setBackground(new Color(212, 180, 15)); // Yellow when selected
                        } else {
                            passwordFieldToUse.setEchoChar('•'); // Hide password
                            toggleButton.setBackground(new Color(44, 62, 80)); // Dark blue when unselected
                        }
                    }
                });

                // Create a panel to hold the password field and toggle button
                JPanel passwordPanel = new JPanel(new BorderLayout());
                passwordPanel.add(passwordFieldToUse, BorderLayout.CENTER);
                passwordPanel.add(toggleButton, BorderLayout.EAST);
                add(passwordPanel, gbc);

                textFields[i] = passwordFieldToUse;
            } else {
                textFields[i] = new JTextField(20);
                textFields[i].setPreferredSize(new Dimension(230, 28));
                textFields[i].setFont(new Font("Arial", Font.PLAIN, 14));
                add(textFields[i], gbc);
            }
        }
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 220));

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(44, 62, 80));
        backButton.setForeground(Color.WHITE);

        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(212, 180, 15));
        registerButton.setForeground(Color.BLACK);

        // Back Button Logic: Switch to Main Menu
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                titleLabel.setText("HOTEL MANAGEMENT SYSTEM"); // Reset title
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "mainMenu");
            }
        });
        
        
        
        // Register Button Logic
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Validate fields
                String fullName = textFields[0].getText().trim();
                String email = textFields[1].getText().trim();
                String phoneNumber = textFields[2].getText().trim();
                String address = textFields[3].getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

                // Check for empty fields
                if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate Full Name (no numbers)
                if (fullName.matches(".*\\d.*")) {
                    JOptionPane.showMessageDialog(null, "Full Name cannot contain numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate Phone Number (Philippine format: 11 digits, starts with 09)
                if (!phoneNumber.matches("09\\d{9}")) {
                    JOptionPane.showMessageDialog(null, "Invalid Philippine phone number. It must be 11 digits and start with 09.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate Email Address
                if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
                    JOptionPane.showMessageDialog(null, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate Password Match
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Confirmation Prompt
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to register?", "Confirm Registration", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Generate Customer ID
                    String customerId = generateCustomerId();

                    // Insert into Database
                    if (insertCustomer(customerId, fullName, email, phoneNumber, address, password)) {
                        JOptionPane.showMessageDialog(null, "Registration Successful! Your Customer ID is: " + customerId, "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Reset all text fields and password fields
                        for (JTextField field : textFields) {
                            field.setText("");
                        }
                        passwordField.setText("");
                        confirmPasswordField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration Failed! Email is exist already. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        
     // Add KeyListeners to all fields for better navigation
        for (int i = 0; i < textFields.length; i++) {
            final int index = i;
            textFields[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (index < textFields.length - 1) {
                            // Move to next field
                            textFields[index + 1].requestFocus();
                        } else {
                            // If last field, trigger register action
                            registerButton.doClick();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        // Handle tab navigation
                        if (e.isShiftDown()) {
                            // Shift+Tab - move to previous field
                            if (index > 0) {
                                textFields[index - 1].requestFocus();
                            }
                        } else {
                            // Tab - move to next field
                            if (index < textFields.length - 1) {
                                textFields[index + 1].requestFocus();
                            }
                        }
                        e.consume(); // Prevent default tab behavior
                    }
                }
            });
        }
        

        // Button Panel
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        // Add button panel to the form
        gbc.gridy = labels.length;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }

    private String generateCustomerId() {
        String customerId = "001"; // Default starting ID
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            // Only consider numeric IDs (excluding walk-in IDs that start with 'W')
            String query = "SELECT MAX(CAST(customer_id AS UNSIGNED)) FROM customers WHERE customer_id NOT LIKE 'W%'";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String maxId = rs.getString(1);
                if (maxId != null) {
                    int nextId = Integer.parseInt(maxId) + 1;
                    customerId = String.format("%03d", nextId); // Format to 3 digits
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error generating customer ID: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return customerId;
    }


    private boolean insertCustomer(String customerId, String fullName, String email, 
            String phoneNumber, String address, String password) {
Connection conn = null;
PreparedStatement checkStmt = null;
PreparedStatement insertStmt = null;
ResultSet rs = null;

try {
conn = Databaseconnection.getConnection();

// 1. First check if email already exists
String checkQuery = "SELECT customer_id FROM customers WHERE customer_email = ?";
checkStmt = conn.prepareStatement(checkQuery);
checkStmt.setString(1, email);
rs = checkStmt.executeQuery();

if (rs.next()) {
JOptionPane.showMessageDialog(null, 
"This email is already registered. Please use a different email.",
"Registration Error", 
JOptionPane.ERROR_MESSAGE);
return false;
}

// 2. Insert new customer
String insertQuery = "INSERT INTO customers (customer_id, customer_fullname, " +
          "customer_email, customer_phoneNumber, customer_address, customer_password) " +
          "VALUES (?, ?, ?, ?, ?, ?)";
insertStmt = conn.prepareStatement(insertQuery);
insertStmt.setString(1, customerId);
insertStmt.setString(2, fullName);
insertStmt.setString(3, email);
insertStmt.setString(4, phoneNumber);
insertStmt.setString(5, address);
insertStmt.setString(6, password);

int rowsAffected = insertStmt.executeUpdate();
if (rowsAffected > 0) {
System.out.println("Successfully registered customer: " + customerId);
return true;
} else {
System.out.println("No rows affected during registration");
return false;
}
} catch (SQLException e) {
e.printStackTrace();
JOptionPane.showMessageDialog(null, 
"Database error during registration: " + e.getMessage(),
"Registration Error", 
JOptionPane.ERROR_MESSAGE);
return false;
} finally {
try {
if (rs != null) rs.close();
if (checkStmt != null) checkStmt.close();
if (insertStmt != null) insertStmt.close();
if (conn != null) conn.close();
} catch (SQLException e) {
e.printStackTrace();
}
}
}

}