import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CustomerDashboard extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton btnHome, btnRoomDetails, btnCheckIn, btnCheckHistory;

    private static final Color ACTIVE_COLOR = new Color(212, 180, 15); // Yellow for active button
    private static final Color HOVER_COLOR = new Color(255, 223, 0); // Brighter yellow for hover
    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Dark background color
    private static final Color TEXT_COLOR = Color.WHITE; // Default text color
    private static final Color HOVER_TEXT_COLOR = Color.BLACK; // Text color on hover

    private String customerEmail;

    public CustomerDashboard(String customer_fullname, String customer_email) {
        this.customerEmail = customer_email; // Initialize the customerEmail field
        

        setTitle("Customer Dashboard");
        setSize(970, 540);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Changed to DO_NOTHING_ON_CLOSE
        setLocationRelativeTo(null);
        setResizable(false);

        // Add window listener to handle closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        CustomerDashboard.this,
                        "Are you sure you want to exit the application?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                
                if (option == JOptionPane.YES_OPTION) {
                    dispose(); // Close the window if user confirms
                }
            }
        });

        // Main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Set the background color of the main content area
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Navigation panel
        JPanel navPanel = new JPanel(new GridLayout(5, 1, 40, 40));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        navPanel.setBackground(BACKGROUND_COLOR);

        // Initialize buttons
        btnHome = createNavButton("HOME");
        btnRoomDetails = createNavButton("ROOM DETAILS");
        btnCheckIn = createNavButton("CHECK-IN RESERVATIONS");
        btnCheckHistory = createNavButton("CHECK-IN HISTORY");

        // Add buttons to the navigation panel
        navPanel.add(btnHome);
        navPanel.add(btnRoomDetails);
        navPanel.add(btnCheckIn);
        navPanel.add(btnCheckHistory);

        // Home Panel with Background & Welcome Message
        HomeCustomerPanel homePanel = new HomeCustomerPanel(customer_fullname, customer_email);

        // Other panels
        CheckInPanel checkInPanel = new CheckInPanel(customerEmail);
     // With:
        String customerId = getCustomerIdFromEmail(customerEmail);
        CheckHistoryPanel checkHistoryPanel = new CheckHistoryPanel(customerId);
        RoomDetailsPanel roomDetailsPanel = new RoomDetailsPanel();

        // Add panels to the main panel
        mainPanel.add(homePanel, "HOME");
        mainPanel.add(roomDetailsPanel, "ROOM DETAILS");
        mainPanel.add(checkInPanel, "CHECK-IN");
        mainPanel.add(checkHistoryPanel, "CHECK-IN HISTORY");

        // Button actions
        btnHome.addActionListener(e -> switchPanel("HOME"));
        btnRoomDetails.addActionListener(e -> switchPanel("ROOM DETAILS"));
        btnCheckIn.addActionListener(e -> switchPanel("CHECK-IN"));
        btnCheckHistory.addActionListener(e -> switchPanel("CHECK-IN HISTORY"));

        // Layout setup
        setLayout(new BorderLayout());
        add(navPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Initially highlight the "Home" button as the default active button
        btnHome.setBackground(ACTIVE_COLOR);
        btnHome.setForeground(HOVER_TEXT_COLOR); // Set text color for active button
    }

    private String getCustomerIdFromEmail(String email) {
        String customerId = "";
        System.out.println("Searching for customer with email: " + email); // Debug
        
        try (Connection conn = Databaseconnection.getConnection()) {
            System.out.println("Database connection established"); // Debug
            
            String query = "SELECT customer_id FROM customers WHERE customer_email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    customerId = rs.getString("customer_id");
                    System.out.println("Found customer ID: " + customerId); // Debug
                } else {
                    System.out.println("No customer found with email: " + email); // Debug
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage()); // Debug
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to fetch customer ID: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        return customerId;
    }

	private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50)); // Increased width to fit longer text
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Adjusted font size
        button.setBackground(BACKGROUND_COLOR); // Default background color
        button.setForeground(TEXT_COLOR); // Default text color
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add rounded corners and hover effects
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(ACTIVE_COLOR, 2, true)); // Rounded border

        // Hover effect using MouseListener
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground() != ACTIVE_COLOR) {
                    button.setBackground(HOVER_COLOR); // Highlight color on hover
                    button.setForeground(HOVER_TEXT_COLOR); // Change text color on hover
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getBackground() != ACTIVE_COLOR) {
                    button.setBackground(BACKGROUND_COLOR); // Reset to default background
                    button.setForeground(TEXT_COLOR); // Reset text color
                }
            }
        });

        return button;
    }

    private void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        // Reset button styles to default background
        resetButtonStyles();

        // Set the active button color
        if (panelName.equals("HOME")) {
            btnHome.setBackground(ACTIVE_COLOR); // Highlight home button
            btnHome.setForeground(HOVER_TEXT_COLOR); // Set text color for active button
        } else if (panelName.equals("ROOM DETAILS")) {
            btnRoomDetails.setBackground(ACTIVE_COLOR); // Highlight room details button
            btnRoomDetails.setForeground(HOVER_TEXT_COLOR); // Set text color for active button
        } else if (panelName.equals("CHECK-IN")) {
            btnCheckIn.setBackground(ACTIVE_COLOR); // Highlight check-in button
            btnCheckIn.setForeground(HOVER_TEXT_COLOR); // Set text color for active button
        } else if (panelName.equals("CHECK-IN HISTORY")) {
            btnCheckHistory.setBackground(ACTIVE_COLOR); // Highlight check-in history button
            btnCheckHistory.setForeground(HOVER_TEXT_COLOR); // Set text color for active button
        }
    }

    private void resetButtonStyles() {
        btnHome.setBackground(BACKGROUND_COLOR);
        btnHome.setForeground(TEXT_COLOR);
        btnRoomDetails.setBackground(BACKGROUND_COLOR);
        btnRoomDetails.setForeground(TEXT_COLOR);
        btnCheckIn.setBackground(BACKGROUND_COLOR);
        btnCheckIn.setForeground(TEXT_COLOR);
        btnCheckHistory.setBackground(BACKGROUND_COLOR);
        btnCheckHistory.setForeground(TEXT_COLOR);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard("John Doe", "john.doe@example.com").setVisible(true));
    }

    // Home Panel with Blurred Background and Responsive Buttons
    class HomeCustomerPanel extends JPanel {
        private BufferedImage backgroundImage;
        private BufferedImage blurredImage;
        private JButton btnEditInfo, btnLogout;
        private JLabel lblWelcome;
        private String customer_fullname, customer_email;

        private static final Color HOVER_COLOR = new Color(255, 223, 0); // Brighter yellow for hover
        private static final Color ACTIVE_COLOR = new Color(212, 180, 15); // Yellow for active button
        private static final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Dark background color
        private static final Color TEXT_COLOR = Color.WHITE; // Default text color
        private static final Color HOVER_TEXT_COLOR = Color.BLACK; // Text color on hover

        public HomeCustomerPanel(String customer_fullname, String customer_email) {
            this.customer_fullname = customer_fullname;
            this.customer_email = customer_email;
            setLayout(null); // Absolute positioning

            // Load and blur background image
            try {
                backgroundImage = ImageIO.read(getClass().getResource("/backgroundhome1.jpg"));
                blurredImage = blurImage(backgroundImage, 15); // Apply blur effect
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Welcome Message
            lblWelcome = new JLabel("Welcome, " + customer_fullname + "!");
            lblWelcome.setFont(new Font("Arial", Font.BOLD, 26));
            lblWelcome.setForeground(Color.WHITE);
            add(lblWelcome);

            // Edit Info Button
            btnEditInfo = createStyledButton("Edit Info");
            btnEditInfo.addActionListener(e -> openEditInfoFrame());
            add(btnEditInfo);

            // Logout Button
            btnLogout = createStyledButton("Logout");
            btnLogout.addActionListener(e -> logout());
            add(btnLogout);

            // Adjust component positions dynamically
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    positionComponents();
                }
            });
        }

        // Helper method to create styled buttons with hover effects
        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(BACKGROUND_COLOR); // Default background color
            button.setForeground(TEXT_COLOR); // Default text color
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(ACTIVE_COLOR, 2, true)); // Rounded border
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Hover effect using MouseListener
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(HOVER_COLOR); // Highlight color on hover
                    button.setForeground(HOVER_TEXT_COLOR); // Change text color on hover
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(BACKGROUND_COLOR); // Reset to default background
                    button.setForeground(TEXT_COLOR); // Reset text color
                }
            });

            return button;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (blurredImage != null) {
                g.drawImage(blurredImage, 0, 0, getWidth(), getHeight(), this);
            }
        }

        private void positionComponents() {
            int width = getWidth();
            int height = getHeight();

            lblWelcome.setBounds(20, 20, 400, 30);
            btnEditInfo.setBounds(width - 150, 20, 130, 40);
            btnLogout.setBounds(width - 150, height - 70, 130, 40);
        }

        // Gaussian Blur Filter
        private BufferedImage blurImage(BufferedImage image, int radius) {
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage blurred = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = blurred.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.dispose();
            return blurred;
        }

        // Open Edit Info Frame
        private void openEditInfoFrame() {
            EditInfoFrame editInfoFrame = new EditInfoFrame(customer_email, this); // Pass 'this' reference
            editInfoFrame.setVisible(true);
        }

        // Logout method to handle logout functionality
        private void logout() {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                // Close the current dashboard window
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                topFrame.dispose();

                // Open the LoginGUI window
                SwingUtilities.invokeLater(() -> {
                    LoginGUI loginGUI = new LoginGUI();
                    loginGUI.setVisible(true); // Assuming LoginGUI is a JFrame
                });
            }
        }

        // Method to update customer info in the UI
        public void updateCustomerInfo(String fullName, String email) {
            this.customer_fullname = fullName;
            this.customer_email = email;
            lblWelcome.setText("Welcome, " + customer_fullname + "!"); // Update the welcome message
        }
    }

    // EditInfoFrame class
 // EditInfoFrame class
    class EditInfoFrame extends JFrame {
        private JTextField txtFullName, txtEmail, txtPhone, txtAddress;
        private JPasswordField pwdNewPassword, pwdConfirmPassword;
        private JLabel lblPasswordError;
        private JButton btnSave, btnCancel;
        private String email; // Use email as the unique identifier
        private HomeCustomerPanel homeCustomerPanel; // Reference to HomeCustomerPanel
        private CheckInPanel checkInPanel; // Reference to CheckInPanel

        private static final Color HOVER_COLOR = new Color(255, 223, 0); // Brighter yellow for hover
        private static final Color ACTIVE_COLOR = new Color(212, 180, 15); // Yellow for active button
        private static final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Dark background color
        private static final Color TEXT_COLOR = Color.WHITE; // Default text color
        private static final Color HOVER_TEXT_COLOR = Color.BLACK; // Text color on hover
        private static final Color CANCEL_COLOR = new Color(220, 53, 69); // Red color for cancel button

        public EditInfoFrame(String email, HomeCustomerPanel homeCustomerPanel) {
            this.email = email; // Initialize email
            this.homeCustomerPanel = homeCustomerPanel; // Initialize homeCustomerPanel
            setTitle("Edit Information");
            setSize(450, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setResizable(false);  // Disable resizing
            setLayout(new BorderLayout());

            // Panel for form fields
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridLayout(8, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            formPanel.setBackground(TEXT_COLOR); // Dark background color

            // Initialize fields
            txtFullName = new JTextField();
            txtEmail = new JTextField();
            txtPhone = new JTextField();
            txtAddress = new JTextField();

            // Password fields
            pwdNewPassword = new JPasswordField();
            pwdConfirmPassword = new JPasswordField();

            // Error label (for matching passwords)
            lblPasswordError = new JLabel("Passwords do not match", JLabel.CENTER);
            lblPasswordError.setForeground(Color.RED);
            lblPasswordError.setVisible(false);

            // Add components to the form panel
            formPanel.add(new JLabel("Full Name:"));
            formPanel.add(txtFullName);
            formPanel.add(new JLabel("Email:"));
            formPanel.add(txtEmail);
            formPanel.add(new JLabel("Phone Number:"));
            formPanel.add(txtPhone);
            formPanel.add(new JLabel("Address:"));
            formPanel.add(txtAddress);

            // Password fields
            formPanel.add(new JLabel("New Password:"));
            formPanel.add(pwdNewPassword);
            formPanel.add(new JLabel("Confirm Password:"));
            formPanel.add(pwdConfirmPassword);
            formPanel.add(lblPasswordError);  // Error message label

            // Button panel
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            buttonPanel.setBackground(TEXT_COLOR);

            // Save Button
            btnSave = createStyledButton("Save");
            btnSave.addActionListener(e -> saveChanges());

            // Cancel Button
            btnCancel = createStyledButton("Cancel");
            btnCancel.addActionListener(e -> {
                if (hasUnsavedChanges()) {
                    int choice = JOptionPane.showConfirmDialog(
                        this,
                        "You have unsaved changes. Are you sure you want to cancel?",
                        "Confirm Cancel",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            });

            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);

            // Add formPanel and buttonPanel to the frame
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            // Set window background color
            getContentPane().setBackground(BACKGROUND_COLOR); // Dark background color

            // Load customer data
            loadCustomerData();
            
            // Add window listener to check for unsaved changes when closing
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (hasUnsavedChanges()) {
                        int choice = JOptionPane.showConfirmDialog(
                            EditInfoFrame.this,
                            "You have unsaved changes. Are you sure you want to close?",
                            "Confirm Close",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                        );
                        
                        if (choice != JOptionPane.YES_OPTION) {
                            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                            return;
                        }
                    }
                    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                }
            });
        }

        // Check if there are any unsaved changes
        private boolean hasUnsavedChanges() {
            String fullName = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String newPassword = new String(pwdNewPassword.getPassword()).trim();
            String confirmPassword = new String(pwdConfirmPassword.getPassword()).trim();

            // Get original data for comparison
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = Databaseconnection.getConnection();
                String query = "SELECT customer_fullname, customer_email, customer_phoneNumber, customer_address, customer_password FROM customers WHERE customer_email = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, this.email);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Compare each field
                    if (!fullName.equals(rs.getString("customer_fullname"))) {
                        return true;
                    }
                    if (!email.equals(rs.getString("customer_email"))) {
                        return true;
                    }
                    if (!phone.equals(rs.getString("customer_phoneNumber"))) {
                        return true;
                    }
                    if (!address.equals(rs.getString("customer_address"))) {
                        return true;
                    }
                    if (!newPassword.isEmpty()) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        // Helper method to create styled buttons with hover effects
     // Helper method to create styled buttons with hover effects
        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            
            // Set different colors for cancel button
            if (text.equals("Cancel")) {
                button.setBackground(CANCEL_COLOR); // Red for cancel button
                button.setBorder(BorderFactory.createLineBorder(CANCEL_COLOR, 2, true));
            } else {
                button.setBackground(ACTIVE_COLOR); // Default background color
                button.setBorder(BorderFactory.createLineBorder(ACTIVE_COLOR, 2, true));
            }
            
            button.setForeground(TEXT_COLOR); // Default text color
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Hover effect using MouseListener
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (text.equals("Cancel")) {
                        button.setBackground(CANCEL_COLOR.brighter()); // Slightly brighter red on hover
                    } else {
                        button.setBackground(HOVER_COLOR); // Highlight color on hover
                    }
                    button.setForeground(HOVER_TEXT_COLOR); // Change text color on hover
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (text.equals("Cancel")) {
                        button.setBackground(CANCEL_COLOR); // Reset to red
                    } else {
                        button.setBackground(ACTIVE_COLOR); // Reset to default background
                    }
                    button.setForeground(TEXT_COLOR); // Reset text color
                }
            });

            return button;
        }

        // Load customer data from the database
        private void loadCustomerData() {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = Databaseconnection.getConnection();
                String query = "SELECT customer_fullname, customer_email, customer_phoneNumber, customer_address FROM customers WHERE customer_email = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, email); // Use email to fetch data
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Debug: Print fetched data
                    System.out.println("Fetched Data:");
                    System.out.println("Full Name: " + rs.getString("customer_fullname"));
                    System.out.println("Email: " + rs.getString("customer_email"));
                    System.out.println("Phone: " + rs.getString("customer_phoneNumber"));
                    System.out.println("Address: " + rs.getString("customer_address"));

                    // Populate the form fields
                    txtFullName.setText(rs.getString("customer_fullname"));
                    txtEmail.setText(rs.getString("customer_email"));
                    txtPhone.setText(rs.getString("customer_phoneNumber"));
                    txtAddress.setText(rs.getString("customer_address"));
                } else {
                    System.out.println("No data found for email: " + email);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load customer data.", "Error", JOptionPane.ERROR_MESSAGE);
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

        // Save changes and show confirmation prompt
        private void saveChanges() {
            // Validate fields
            String fullName = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String newPassword = new String(pwdNewPassword.getPassword()).trim();
            String confirmPassword = new String(pwdConfirmPassword.getPassword()).trim();

            // Check for empty fields
            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate Full Name (no numbers)
            if (fullName.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(this, "Full Name cannot contain numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate Phone Number (Philippine format: 11 digits, starts with 09)
            if (!phone.matches("09\\d{9}")) {
                JOptionPane.showMessageDialog(this, "Invalid Philippine phone number. It must be 11 digits and start with 09.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate Email Address
            if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
                JOptionPane.showMessageDialog(this, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate Password Match (if new password is provided)
            if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                lblPasswordError.setVisible(true);  // Show error if passwords do not match
                return;  // Exit the method to prevent saving
            } else {
                lblPasswordError.setVisible(false);  // Hide error if passwords match
            }

            // Confirmation Prompt
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to save these changes?",
                "Confirm Save",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                // Save the changes to the database
                if (updateCustomerData(fullName, email, phone, address, newPassword)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Changes saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    // Update the HomeCustomerPanel UI
                    homeCustomerPanel.updateCustomerInfo(fullName, email);

                    // Update the CheckInPanel UI
                    CheckInPanel checkInPanel = (CheckInPanel) mainPanel.getComponent(2); // Assuming CheckInPanel is the third panel
                    checkInPanel.updateCustomerInfo(fullName, email, phone, address);

                    // Close the frame
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to save changes. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }

        // Update customer data in the database
        private boolean updateCustomerData(String fullName, String email, String phone, String address, String newPassword) {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = Databaseconnection.getConnection();
                String query;
                if (newPassword.isEmpty()) {
                    query = "UPDATE customers SET customer_fullname = ?, customer_email = ?, customer_phoneNumber = ?, customer_address = ? WHERE customer_email = ?";
                } else {
                    query = "UPDATE customers SET customer_fullname = ?, customer_email = ?, customer_phoneNumber = ?, customer_address = ?, customer_password = ? WHERE customer_email = ?";
                }
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, fullName);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.setString(4, address);
                if (newPassword.isEmpty()) {
                    pstmt.setString(5, this.email); // Use the original email for the WHERE clause
                } else {
                    pstmt.setString(5, newPassword);
                    pstmt.setString(6, this.email); // Use the original email for the WHERE clause
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
    }
}