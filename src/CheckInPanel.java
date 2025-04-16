import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CheckInPanel extends JPanel {
    private JComboBox<String> roomNumberCombo;
    private JTextField txtRoomType, txtCapacity, txtPrice;
    private JTextField txtCustomerId, txtFullName, txtEmail, txtPhone, txtAddress;
    private JDateChooser checkInDate;
    private JTextField checkInDateField; // Reference to input field inside JDateChooser
    private final String PLACEHOLDER_TEXT = "YYYY-MM-DD HH:MM:SS"; // Placeholder text
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private JPanel bottomPanel; // Declare bottomPanel as an instance variable
    private JDateChooser checkoutDateChooser; // Add this field


    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Dark blue background
    private static final Color HOVER_COLOR = new Color(255, 223, 0); // Bright yellow for hover
    private static final Color ACTIVE_COLOR = new Color(212, 180, 15); // Yellow for active button
    private static final Color TEXT_COLOR = Color.WHITE; // Default text color
    private static final Color HOVER_TEXT_COLOR = Color.BLACK; // Text color on hover

    private String customerEmail; // Store the logged-in customer's email

    public CheckInPanel(String customerEmail) {
        this.customerEmail = customerEmail; // Initialize with the logged-in email
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(HOVER_TEXT_COLOR, 0)); // Border around the panel
        setBackground(BACKGROUND_COLOR);

        // Initialize UI components
        initializeUI();

        // Load customer data
        loadCustomerData(customerEmail);
        
     // Load available rooms
        loadAvailableRooms();
        
    }

    private void initializeUI() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACTIVE_COLOR, 2),
            "Check-In Details",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 16),
            ACTIVE_COLOR
        ));
        container.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.insets = new Insets(5, 5, 5, 5);
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(5, 5, 5, 5);
        rightGbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        // Customer Details
        JLabel lblCustomerId = new JLabel("Customer ID:");
        lblCustomerId.setFont(labelFont);
        lblCustomerId.setForeground(TEXT_COLOR);
        txtCustomerId = new JTextField(15);
        txtCustomerId.setEditable(false);
        txtCustomerId.setBackground(new Color(60, 90, 150));
        txtCustomerId.setForeground(TEXT_COLOR);

        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setFont(labelFont);
        lblFullName.setForeground(TEXT_COLOR);
        txtFullName = new JTextField(15);
        txtFullName.setEditable(false);
        txtFullName.setBackground(new Color(60, 90, 150));
        txtFullName.setForeground(TEXT_COLOR);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        lblEmail.setForeground(TEXT_COLOR);
        txtEmail = new JTextField(15);
        txtEmail.setEditable(false);
        txtEmail.setBackground(new Color(60, 90, 150));
        txtEmail.setForeground(TEXT_COLOR);

        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(labelFont);
        lblPhone.setForeground(TEXT_COLOR);
        txtPhone = new JTextField(15);
        txtPhone.setEditable(false);
        txtPhone.setBackground(new Color(60, 90, 150));
        txtPhone.setForeground(TEXT_COLOR);

        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(labelFont);
        lblAddress.setForeground(TEXT_COLOR);
        txtAddress = new JTextField(15);
        txtAddress.setEditable(false);
        txtAddress.setBackground(new Color(60, 90, 150));
        txtAddress.setForeground(TEXT_COLOR);

        // Room Details
        JLabel lblRoomNumber = new JLabel("Room No:");
        lblRoomNumber.setFont(labelFont);
        lblRoomNumber.setForeground(TEXT_COLOR);
        roomNumberCombo = new JComboBox<>(new String[]{"101", "102", "103", "104"});
        roomNumberCombo.setBackground(new Color(60, 90, 150));
        roomNumberCombo.setForeground(TEXT_COLOR);
        roomNumberCombo.addActionListener(new RoomSelectionListener());

        JLabel lblRoomType = new JLabel("Room Type:");
        lblRoomType.setFont(labelFont);
        lblRoomType.setForeground(TEXT_COLOR);
        txtRoomType = new JTextField(15);
        txtRoomType.setEditable(false);
        txtRoomType.setBackground(new Color(60, 90, 150));
        txtRoomType.setForeground(TEXT_COLOR);

        JLabel lblCapacity = new JLabel("Capacity:");
        lblCapacity.setFont(labelFont);
        lblCapacity.setForeground(TEXT_COLOR);
        txtCapacity = new JTextField(15);
        txtCapacity.setEditable(false);
        txtCapacity.setBackground(new Color(60, 90, 150));
        txtCapacity.setForeground(TEXT_COLOR);

        JLabel lblPrice = new JLabel("Price/Day:");
        lblPrice.setFont(labelFont);
        lblPrice.setForeground(TEXT_COLOR);
        txtPrice = new JTextField(15);
        txtPrice.setEditable(false);
        txtPrice.setBackground(new Color(60, 90, 150));
        txtPrice.setForeground(TEXT_COLOR);

        
        
     // Inside the initializeUI method
        JLabel lblCheckoutDate = new JLabel("Check-out Date:");
        lblCheckoutDate.setForeground(TEXT_COLOR);
        checkoutDateChooser = new JDateChooser();
        checkoutDateChooser.setDateFormatString("yyyy-MM-dd hh:mm:ss a");
       checkoutDateChooser.setPreferredSize(new Dimension(200, 30));
     // Add the checkout date field to the check-in panel
        rightGbc.gridx = 0; rightGbc.gridy = 4; rightPanel.add(lblCheckoutDate, rightGbc);
        rightGbc.gridx = 1; rightPanel.add(checkoutDateChooser, rightGbc);
       
     // Check-in Date
        JLabel lblCheckInDate = new JLabel("Check-in Date:");
        lblCheckInDate.setForeground(TEXT_COLOR);

        checkInDate = new JDateChooser();
        checkInDate.setDateFormatString("yyyy-MM-dd hh:mm:ss a");
        checkInDate.setPreferredSize(new Dimension(200, 30));
        checkInDateField = ((JTextField) checkInDate.getDateEditor().getUiComponent());
        checkInDateField.setText(PLACEHOLDER_TEXT);
        checkInDateField.setForeground(Color.GRAY);
        // Add FocusListener to manage placeholder behavior
        checkInDateField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (checkInDateField.getText().equals(PLACEHOLDER_TEXT)) {
                    checkInDateField.setText("");
                    checkInDateField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (checkInDateField.getText().isEmpty()) {
                    checkInDateField.setText(PLACEHOLDER_TEXT);
                    checkInDateField.setForeground(Color.GRAY);
                }
            }
        });
        
        checkoutDateChooser.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (checkInDateField.getText().equals(PLACEHOLDER_TEXT)) {
                    checkInDateField.setText("");
                    checkInDateField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (checkInDateField.getText().isEmpty()) {
                    checkInDateField.setText(PLACEHOLDER_TEXT);
                    checkInDateField.setForeground(Color.GRAY);
                }
            }
        });

        // Submit Button
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setBackground(ACTIVE_COLOR);
        btnSubmit.setForeground(HOVER_TEXT_COLOR);
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 16));
        btnSubmit.setPreferredSize(new Dimension(120, 40));

        btnSubmit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSubmit.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSubmit.setBackground(ACTIVE_COLOR);
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitReservation();
            }
        });

        // Add components to left and right panels
        leftGbc.gridx = 0; leftGbc.gridy = 0; leftPanel.add(lblCustomerId, leftGbc);
        leftGbc.gridx = 1; leftPanel.add(txtCustomerId, leftGbc);

        leftGbc.gridx = 0; leftGbc.gridy = 1; leftPanel.add(lblFullName, leftGbc);
        leftGbc.gridx = 1; leftPanel.add(txtFullName, leftGbc);

        leftGbc.gridx = 0; leftGbc.gridy = 2; leftPanel.add(lblEmail, leftGbc);
        leftGbc.gridx = 1; leftPanel.add(txtEmail, leftGbc);

        leftGbc.gridx = 0; leftGbc.gridy = 3; leftPanel.add(lblPhone, leftGbc);
        leftGbc.gridx = 1; leftPanel.add(txtPhone, leftGbc);

        leftGbc.gridx = 0; leftGbc.gridy = 4; leftPanel.add(lblAddress, leftGbc);
        leftGbc.gridx = 1; leftPanel.add(txtAddress, leftGbc);

        rightGbc.gridx = 0; rightGbc.gridy = 0; rightPanel.add(lblRoomNumber, rightGbc);
        rightGbc.gridx = 1; rightPanel.add(roomNumberCombo, rightGbc);

        rightGbc.gridx = 0; rightGbc.gridy = 1; rightPanel.add(lblRoomType, rightGbc);
        rightGbc.gridx = 1; rightPanel.add(txtRoomType, rightGbc);

        rightGbc.gridx = 0; rightGbc.gridy = 2; rightPanel.add(lblCapacity, rightGbc);
        rightGbc.gridx = 1; rightPanel.add(txtCapacity, rightGbc);

        rightGbc.gridx = 0; rightGbc.gridy = 3; rightPanel.add(lblPrice, rightGbc);
        rightGbc.gridx = 1; rightPanel.add(txtPrice, rightGbc);
        

        // Add left and right panels to the container
        gbc.gridx = 0; gbc.gridy = 0; container.add(leftPanel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; container.add(rightPanel, gbc);

        // Add check-in date and submit button
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(lblCheckInDate);
        centerPanel.add(checkInDate);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        container.add(centerPanel, gbc);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(btnSubmit);
        add(container, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        
        
     // Add "View Reservations" button
        JButton btnViewReservations = new JButton("View Reservations");
        btnViewReservations.setBackground(ACTIVE_COLOR);
        btnViewReservations.setForeground(HOVER_TEXT_COLOR);
        btnViewReservations.setFont(new Font("Arial", Font.BOLD, 16));
        btnViewReservations.setPreferredSize(new Dimension(180, 40)); // Set preferred size
        btnViewReservations.setMinimumSize(new Dimension(180, 40)); // Set minimum size
        btnViewReservations.setMaximumSize(new Dimension(180, 40)); // Set maximum size
        
        btnViewReservations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnViewReservations.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnViewReservations.setBackground(ACTIVE_COLOR);
            }
        });

        btnViewReservations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewReservations(); // Call method to view reservations
            }
        });

     // Initialize bottomPanel
        bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND_COLOR);

        // Add the "Submit" button to the bottom panel
        bottomPanel.add(btnSubmit);

        // Add the "View Reservations" button to the bottom panel
        bottomPanel.add(btnViewReservations);

        // Add the bottom panel to the main panel
        add(container, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
    }

    // Method to load customer data
    public void loadCustomerData(String customerEmail) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Query to get customer data
            String query = "SELECT customer_id, customer_fullname, customer_email, customer_phoneNumber, customer_address FROM customers WHERE customer_email = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, customerEmail);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                txtCustomerId.setText(rs.getString("customer_id"));
                txtFullName.setText(rs.getString("customer_fullname"));
                txtEmail.setText(rs.getString("customer_email"));
                txtPhone.setText(rs.getString("customer_phoneNumber"));
                txtAddress.setText(rs.getString("customer_address"));
            } else {
                JOptionPane.showMessageDialog(this, "Customer data not found.", "Error", JOptionPane.ERROR_MESSAGE);
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

    // Method to update customer info in the UI
    public void updateCustomerInfo(String fullName, String email, String phone, String address) {
        txtFullName.setText(fullName);
        txtEmail.setText(email);
        txtPhone.setText(phone);
        txtAddress.setText(address);
    }

    private class RoomSelectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedRoom = (String) roomNumberCombo.getSelectedItem();
            if (selectedRoom != null) {
                fetchRoomDetails(selectedRoom);
            }
        }
    }

    private void fetchRoomDetails(String roomNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Query to get room details
            String query = "SELECT room_type, capacity, price FROM rooms WHERE room_number = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomNumber);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                txtRoomType.setText(rs.getString("room_type"));
                txtCapacity.setText(rs.getString("capacity"));
                txtPrice.setText("₱" + rs.getString("price"));
            } else {
                txtRoomType.setText("");
                txtCapacity.setText("");
                txtPrice.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load room details.", "Error", JOptionPane.ERROR_MESSAGE);
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
    
   
    private void loadAvailableRooms() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Query to get available rooms
            String query = "SELECT room_number FROM rooms WHERE status = 'Available'";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            // Clear the existing items in the combo box
            roomNumberCombo.removeAllItems();

            // Add available rooms to the combo box
            while (rs.next()) {
                roomNumberCombo.addItem(rs.getString("room_number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load available rooms.", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void submitReservation() {
        String roomNumber = (String) roomNumberCombo.getSelectedItem();
        Date selectedDate = checkInDate.getDate();
        Date checkoutDate = checkoutDateChooser.getDate();

        if (roomNumber == null || selectedDate == null || checkoutDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a room, check-in date, and check-out date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get current date (without time component)
        Date currentDate = new Date();
        currentDate = removeTime(currentDate);
        
        // Remove time components from selected dates for accurate comparison
        selectedDate = removeTime(selectedDate);
        checkoutDate = removeTime(checkoutDate);

        // Validate check-in date is not in the past
        if (selectedDate.before(currentDate)) {
            JOptionPane.showMessageDialog(this, 
                "Check-in date cannot be in the past! Please select today or a future date.", 
                "Invalid Date", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate check-out date is not before check-in date
        if (checkoutDate.before(selectedDate)) {
            JOptionPane.showMessageDialog(this, 
                "Check-out date must be after check-in date!", 
                "Invalid Date", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // NEW VALIDATION: Check if check-in and check-out dates are equal
        if (checkoutDate.equals(selectedDate)) {
            JOptionPane.showMessageDialog(this, 
                "Minimum reservation is 1 day! Please select a check-out date at least 1 day after check-in.", 
                "Invalid Reservation Period", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate check-out date is not more than a certain period in the future (optional)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 1);
        if (checkoutDate.after(maxDate.getTime())) {
            JOptionPane.showMessageDialog(this, 
                "Reservations can only be made up to 1 year in advance!", 
                "Invalid Date", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Proceed with reservation if all validations pass
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Insert reservation into the database with status "Pending"
            String query = "INSERT INTO reservations (customer_id, room_number, check_in_date, check_out_date, status) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, txtCustomerId.getText());
            pstmt.setString(2, roomNumber);
            pstmt.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedDate));
            pstmt.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkoutDate));
            pstmt.setString(5, "Pending");
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, 
                "Reservation submitted successfully! Waiting for manager approval.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to submit reservation.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Keep the existing removeTime helper method
    private Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    private void viewReservations() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Query to get reservations for the logged-in customer
            String query = "SELECT room_number, check_in_date, status FROM reservations WHERE customer_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, txtCustomerId.getText()); // Use the customer ID from the UI
            rs = pstmt.executeQuery();

            // Create a table model to display the data
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Make all cells non-editable
                    return false;
                }
            };

            tableModel.addColumn("Room Number");
            tableModel.addColumn("Check-In Date");
            tableModel.addColumn("Status");

            // Add reservation data to the table model
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String checkInDate = rs.getString("check_in_date");
                String status = rs.getString("status");
                tableModel.addRow(new Object[]{roomNumber, checkInDate, status});
            }

            // Create a JTable to display the data
            JTable reservationTable = new JTable(tableModel);
            reservationTable.setRowSelectionAllowed(false); // Disable row selection
            reservationTable.setColumnSelectionAllowed(false); // Disable column selection
            reservationTable.setCellSelectionEnabled(false); // Disable cell selection

            JScrollPane scrollPane = new JScrollPane(reservationTable);

            // Create a dialog to display the table
            JDialog reservationDialog = new JDialog();
            reservationDialog.setTitle("Your Reservations");
            reservationDialog.setLayout(new BorderLayout());
            reservationDialog.add(scrollPane, BorderLayout.CENTER);
            reservationDialog.setSize(500, 300);
            reservationDialog.setLocationRelativeTo(this); // Center the dialog relative to the parent window
            reservationDialog.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load reservations.", "Error", JOptionPane.ERROR_MESSAGE);
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
    
}