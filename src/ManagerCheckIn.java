import javax.print.attribute.AttributeSet;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class ManagerCheckIn extends JFrame {
    private DefaultTableModel tableModel;
    private JTable customerTable;
    private JTextField searchField, customeridField, nameField, phoneField, emailField, addressField;
    private JTextField priceField, capacityField;
    private JComboBox<String> roomNumberCombo; // Changed to combo box for room numbers
    private JDateChooser dateChooser;
    private JDateChooser checkoutDateChooser;
    private JTextField roomTypeField; // Added for room type
    private Map<String, String> roomPrices;
    private Set<String> bookedRooms;
    private String managerName;
    private String managerEmail;

    public ManagerCheckIn(String managerName, String managerEmail) {
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Manager Dashboard");
        setSize(1230, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Exit confirmation when closing the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                        ManagerCheckIn.this,
                        "Do you want to exit the program?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Room Price Mapping
        roomPrices = new HashMap<>();
        roomPrices.put("Budget Room", "999");
        roomPrices.put("Single Room", "1499");
        roomPrices.put("Double Room", "1999");
        roomPrices.put("Twin Room", "2499");
        roomPrices.put("Queen Room", "3499");
        roomPrices.put("Deluxe Room", "4499");
        roomPrices.put("Suite", "5499");

     // Sidebar panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(5, 1, 0, 5)); // Added 5px vertical gap between buttons
        sidebar.setBackground(new Color(41, 57, 80)); // Dark blue-gray
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
            
            if (label.equals("Check In")) {
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

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Customer Info");
        searchButton.setBackground(Color.GRAY);
        searchButton.setForeground(Color.WHITE);

     // Approve Button (Solid Green)
        JButton btnApprove = new JButton("Approve");
        btnApprove.setBackground(new Color(0, 153, 51)); // Green Color
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFocusPainted(false);
        btnApprove.setFont(new Font("Arial", Font.BOLD, 12));
        btnApprove.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
     // Decline Button (Solid Red)
        JButton btnDecline = new JButton("Decline");
        btnDecline.setBackground(new Color(204, 0, 0)); // Red Color
        btnDecline.setForeground(Color.WHITE);
        btnDecline.setFocusPainted(false);
        btnDecline.setFont(new Font("Arial", Font.BOLD, 12));
        btnDecline.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Panel for search (centered)
        JPanel searchCenterPanel = new JPanel();
        searchCenterPanel.add(searchField);
        searchCenterPanel.add(searchButton);

        // Panel for approve/decline buttons (aligned right)
        JPanel searchRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchRightPanel.add(btnApprove);
        searchRightPanel.add(btnDecline);

        // Main search panel with BorderLayout
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchCenterPanel, BorderLayout.CENTER);
        searchPanel.add(searchRightPanel, BorderLayout.EAST);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Customer Table with Proper Scrolling
        String[] columns = {"Reservations ID", "Customer ID", "Customer Name", "Phone", "Email", "Address", "Room Number", "Room Type", "Room Capacity", "Price", "Reservations Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.getTableHeader().setReorderingAllowed(false);
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set preferred column widths
        int[] columnWidths = {100, 150, 100, 100, 100, 100, 100, 150, 120, 100, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            customerTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Ensure table fills viewport and enables scrolling
        customerTable.setFillsViewportHeight(true);

        // Wrap the table inside a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(customerTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Add scroll pane to main panel
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Check-In Form Panel
        JPanel checkInPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        checkInPanel.setBorder(BorderFactory.createTitledBorder(
        	    BorderFactory.createEtchedBorder(), 
        	    "Check In Info For Walk-In", 
        	    TitledBorder.LEFT, 
        	    TitledBorder.TOP, 
        	    new Font("Arial", Font.BOLD, 14) // Adjust the font size here
        	));


        customeridField = new JTextField();
        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();
        roomNumberCombo = new JComboBox<>(); // Combo box for room numbers
        roomTypeField = new JTextField(); // Room type field
        roomTypeField.setEditable(false);
        capacityField = new JTextField();
        capacityField.setEditable(false);
        priceField = new JTextField();
        priceField.setEditable(false);

        dateChooser = new JDateChooser();
        dateChooser.setMinSelectableDate(new Date());
        checkoutDateChooser = new JDateChooser();
        checkoutDateChooser.setMinSelectableDate(new Date());

        JButton checkInButton = new JButton("Check-In");
        checkInButton.setBackground(new Color(0, 153, 51));
        checkInButton.setForeground(Color.WHITE);

     // Ensure only numbers are entered in specific fields
        KeyAdapter numberOnlyAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        };
        customeridField.addKeyListener(numberOnlyAdapter);
        phoneField.addKeyListener(numberOnlyAdapter);
        
        nameField.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyTyped(KeyEvent e) {
        		char c = e.getKeyChar();
        		if(!Character.isLetter(c) && !Character.isWhitespace(c)) {
        			e.consume();
        		}
        	}
        	
        });
        
        roomNumberCombo.addActionListener(e -> {
            String selectedRoom = (String) roomNumberCombo.getSelectedItem();
            if (selectedRoom != null) {
                fetchRoomDetails(selectedRoom);
            }
        });

        // Add fields to the check-in panel
        checkInPanel.add(new JLabel("Customer ID (Optional):"));
        checkInPanel.add(customeridField);
        checkInPanel.add(new JLabel("Customer Name:"));
        checkInPanel.add(nameField);
        checkInPanel.add(new JLabel("Phone:"));
        checkInPanel.add(phoneField);
        checkInPanel.add(new JLabel("Email (Optional):"));
        checkInPanel.add(emailField);
        checkInPanel.add(new JLabel("Address:"));
        checkInPanel.add(addressField);
        checkInPanel.add(new JLabel("Room Number:"));
        checkInPanel.add(roomNumberCombo);
        checkInPanel.add(new JLabel("Room Type:"));
        checkInPanel.add(roomTypeField);
        checkInPanel.add(new JLabel("Room Capacity:"));
        checkInPanel.add(capacityField);
        checkInPanel.add(new JLabel("Price:"));
        checkInPanel.add(priceField);
        checkInPanel.add(new JLabel("Check-In Date:"));
        checkInPanel.add(dateChooser);
        checkInPanel.add(new JLabel("Check-Out Date:"));
        checkInPanel.add(checkoutDateChooser);
        checkInPanel.add(new JLabel());
        checkInPanel.add(checkInButton);

        // Add check-in panel to the main panel
        mainPanel.add(checkInPanel, BorderLayout.SOUTH);

        // Add sidebar and main panel to the frame
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Event Listeners
        searchButton.addActionListener(e -> searchCustomer());
        checkInButton.addActionListener(e -> addCheckInToTable());

        btnApprove.addActionListener(e -> approveReservation());
        btnDecline.addActionListener(e -> declineReservation());

        // Load pending reservations
        loadPendingReservations();
        loadAvailableRooms();
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
                roomTypeField.setText(rs.getString("room_type"));
                capacityField.setText(rs.getString("capacity"));
                priceField.setText(rs.getString("price"));
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
    
    private void searchCustomer() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Customer ID or Customer Name to search!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nid = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String name = tableModel.getValueAt(i, 1).toString().toLowerCase();

            if (nid.contains(searchText) || name.contains(searchText)) {
                customerTable.setRowSelectionInterval(i, i);
                populateCheckInFields(i);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Customer not found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    class NumericDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;
            if (str.matches("\\d+")) {
                super.insertString(offs, str, (javax.swing.text.AttributeSet) a);
            }
        }
    }
    private void populateCheckInFields(int rowIndex) {
        customeridField.setText(tableModel.getValueAt(rowIndex, 0).toString());
        nameField.setText(tableModel.getValueAt(rowIndex, 1).toString());
        phoneField.setText(tableModel.getValueAt(rowIndex, 2).toString());
        emailField.setText(tableModel.getValueAt(rowIndex, 3).toString());
        addressField.setText(tableModel.getValueAt(rowIndex, 4).toString());
    }

    private void addCheckInToTable() {
        String customerid = customeridField.getText(); // Customer ID (optional)
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText(); // Email (optional)
        String address = addressField.getText();
        String roomNo = (String) roomNumberCombo.getSelectedItem();
        String roomType = roomTypeField.getText();
        String bedCapacity = capacityField.getText();
        String price = priceField.getText();
        Date checkInDate = dateChooser.getDate();
        Date checkoutDate = checkoutDateChooser.getDate();
        
        
        // Check if room is already occupied
        //String roomNo = (String) roomNumberCombo.getSelectedItem();
        
        if (isRoomOccupied(roomNo)) {
            JOptionPane.showMessageDialog(this, 
                "This room is already occupied. Please select another room.", 
                "Room Occupied", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        

        // Validate fields (Customer ID and Email are optional)
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() ||
            roomType.isEmpty() || bedCapacity.isEmpty() || price.isEmpty() || roomNo.isEmpty() ||
            checkInDate == null || checkoutDate == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
     // Add confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to proceed with this check-in?",
            "Confirm Check-In",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return; // User canceled the check-in
        }

        // Validate phone number
        if (!Pattern.matches("^09\\d{9}$", phone)) {
            JOptionPane.showMessageDialog(this, "Phone number must start with 09 and have exactly 11 digits.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email (if provided)
        if (!email.isEmpty() && !Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", email)) {
            JOptionPane.showMessageDialog(this, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate customer name (no numbers allowed)
        if (Pattern.matches(".*\\d.*", name)) {
            JOptionPane.showMessageDialog(this, "Customer name cannot contain numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ensure check-out date is at least 1 full day after check-in date
        long differenceInMillis = checkoutDate.getTime() - checkInDate.getTime();
        long differenceInHours = differenceInMillis / (60 * 60 * 1000); // Convert milliseconds to hours

        if (differenceInHours < 24) {
            JOptionPane.showMessageDialog(this, "Check-out must be at least 1 full day after check-in!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // For walk-ins (no customer ID provided), find or create a walk-in account
            if (customerid.isEmpty()) {
                // Search for existing walk-in account with matching phone number
                String findWalkIn = "SELECT customer_id FROM customers WHERE customer_phoneNumber = ? AND customer_id LIKE 'W%'";
                pstmt = conn.prepareStatement(findWalkIn);
                pstmt.setString(1, phone);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    // Use existing walk-in account
                    customerid = rs.getString("customer_id");
                } else {
                    // Generate a new walk-in ID (W01, W02, etc.)
                    String getMaxWalkIn = "SELECT MAX(CAST(SUBSTRING(customer_id, 2) AS UNSIGNED)) FROM customers WHERE customer_id LIKE 'W%'";
                    pstmt = conn.prepareStatement(getMaxWalkIn);
                    rs = pstmt.executeQuery();
                    int nextWalkInNum = 1;
                    if (rs.next()) {
                        nextWalkInNum = rs.getInt(1) + 1;
                    }
                    customerid = "W" + String.format("%02d", nextWalkInNum);
                    rs.close();
                    pstmt.close();

                    // Create new walk-in account with optional email
                    String insertCustomer = "INSERT INTO customers (customer_id, customer_fullname, customer_phoneNumber, " +
                                          "customer_email, customer_address, customer_password) " +
                                          "VALUES (?, ?, ?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(insertCustomer);
                    pstmt.setString(1, customerid);
                    pstmt.setString(2, name);
                    pstmt.setString(3, phone);
                    
                    // Handle optional email - use NULL if empty
                    if (email == null || email.trim().isEmpty()) {
                        pstmt.setNull(4, java.sql.Types.VARCHAR);
                    } else {
                        pstmt.setString(4, email);
                    }
                    
                    pstmt.setString(5, address);
                    pstmt.setNull(6, java.sql.Types.VARCHAR); // No password for walk-ins
                    pstmt.executeUpdate();
                }
                rs.close();
                pstmt.close();
            }

            // Format dates properly
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkInDateStr = sdf.format(checkInDate);
            String checkoutDateStr = sdf.format(checkoutDate);

            // Create a reservation record (required for checkouts table)
            String insertReservation = "INSERT INTO reservations (customer_id, room_number, check_in_date, check_out_date, status) " +
                                     "VALUES (?, ?, ?, ?, 'Walk-in')";
            pstmt = conn.prepareStatement(insertReservation, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, customerid);
            pstmt.setString(2, roomNo);
            pstmt.setString(3, checkInDateStr);
            pstmt.setString(4, checkoutDateStr);
            pstmt.executeUpdate();
            
            // Get the auto-generated reservation_id
            int reservationId = -1;
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                reservationId = rs.getInt(1);
            }
            rs.close();
            pstmt.close();

            // Insert into checkins table
            String insertCheckin = "INSERT INTO check_ins (reservation_id, customer_id, customer_name, customer_phone, room_number, check_in_date, status) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, 'checked-in')";
            pstmt = conn.prepareStatement(insertCheckin);
            pstmt.setInt(1, reservationId);
            pstmt.setString(2, customerid);
            pstmt.setString(3, name);
            pstmt.setString(4, phone);
            pstmt.setString(5, roomNo);
            pstmt.setString(6, checkInDateStr);
            pstmt.executeUpdate();
            pstmt.close();

            // Insert into checkouts table
            String insertCheckout = "INSERT INTO checkouts (reservation_id, customer_id, room_number, check_in_date, check_out_date, total_amount, status) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, 'Checked-in')";
            pstmt = conn.prepareStatement(insertCheckout);
            pstmt.setInt(1, reservationId);
            pstmt.setString(2, customerid);
            pstmt.setString(3, roomNo);
            pstmt.setString(4, checkInDateStr);
            pstmt.setString(5, checkoutDateStr);
            pstmt.setString(6, price);
            pstmt.executeUpdate();
            pstmt.close();

            // Update room status to 'Occupied' - This is the key part for your requirement
            String updateRoom = "UPDATE rooms SET status = 'Occupied' WHERE room_number = ?";
            pstmt = conn.prepareStatement(updateRoom);
            pstmt.setString(1, roomNo);
            int updatedRows = pstmt.executeUpdate();
            
            if (updatedRows == 0) {
                throw new SQLException("Failed to update room status - room not found");
            }
            pstmt.close();

            conn.commit(); // Commit transaction

            JOptionPane.showMessageDialog(this, "Check-In Successful! Room " + roomNo + " is now occupied.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

            // Refresh available rooms list
            loadAvailableRooms();

            // Navigate to ManagerCheckOut
            this.dispose();
            new ManagerCheckOut(managerName, managerEmail).setVisible(true);

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, 
                "Error during check-in: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } 
    }
    
    
    private boolean isRoomOccupied(String roomNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = Databaseconnection.getConnection();
            String query = "SELECT status FROM rooms WHERE room_number = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomNumber);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String status = rs.getString("status");
                return "Occupied".equalsIgnoreCase(status);
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
    
    
    
    
    
    private void clearFields() {
        customeridField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        roomNumberCombo.setSelectedIndex(0);
        roomTypeField.setText("");
        capacityField.setText("");
        priceField.setText("");
        dateChooser.setDate(null);
        checkoutDateChooser.setDate(null);
    }

    private void loadPendingReservations() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Query to get pending reservations
            String query = "SELECT r.reservation_id, c.customer_id, c.customer_fullname, c.customer_phoneNumber, " +
                           "c.customer_email, c.customer_address, r.room_number, rm.room_type, " +
                           "rm.capacity AS room_capacity, rm.price, r.check_in_date " +
                           "FROM reservations r " +
                           "JOIN customers c ON r.customer_id = c.customer_id " +
                           "JOIN rooms rm ON r.room_number = rm.room_number " +
                           "WHERE r.status = 'Pending'";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            // Clear the existing table data
            tableModel.setRowCount(0);

            // Add pending reservations to the table
            while (rs.next()) {
                String reservationId = rs.getString("reservation_id");
                String customerId = rs.getString("customer_id");
                String customerName = rs.getString("customer_fullname");
                String phone = rs.getString("customer_phoneNumber");
                String email = rs.getString("customer_email");
                String address = rs.getString("customer_address");
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                String roomCapacity = rs.getString("room_capacity");
                String price = rs.getString("price");
                String checkInDate = rs.getString("check_in_date");

                tableModel.addRow(new Object[]{reservationId, customerId, customerName, phone, email, address, roomNumber, roomType, roomCapacity, price, checkInDate});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load pending reservations.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void approveReservation() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to approve.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        
        
        
        // Add confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to approve this reservation?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return; // User clicked No or closed the dialog
        }
        
        // Fetch values from the table model
        String reservationId = tableModel.getValueAt(selectedRow, 0).toString();
        String customerId = tableModel.getValueAt(selectedRow, 1).toString();
        String customerName = tableModel.getValueAt(selectedRow, 2).toString();
        String phone = tableModel.getValueAt(selectedRow, 3).toString();
        String roomNumber = tableModel.getValueAt(selectedRow, 6).toString();
        String checkInDateStr = tableModel.getValueAt(selectedRow, 10).toString();
        String price = tableModel.getValueAt(selectedRow, 9).toString();
        
        
        
        if (isRoomOccupied(roomNumber)) {
            JOptionPane.showMessageDialog(this, 
                "This room is already occupied. Cannot approve reservation.", 
                "Room Occupied", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Start transaction
            conn.setAutoCommit(false);

            try {
                // 1. First get the checkout date from the reservation
                String getReservationQuery = "SELECT check_out_date FROM reservations WHERE reservation_id = ?";
                pstmt = conn.prepareStatement(getReservationQuery);
                pstmt.setInt(1, Integer.parseInt(reservationId));
                rs = pstmt.executeQuery();
                
                String checkoutDateStr = null;
                if (rs.next()) {
                    checkoutDateStr = rs.getString("check_out_date");
                }
                rs.close();
                pstmt.close();

                if (checkoutDateStr == null) {
                    throw new SQLException("No checkout date found for reservation");
                }

                // 2. Update reservation status to 'Approved'
                String updateReservationQuery = "UPDATE reservations SET status = 'Approved' WHERE reservation_id = ?";
                pstmt = conn.prepareStatement(updateReservationQuery);
                pstmt.setInt(1, Integer.parseInt(reservationId));
                pstmt.executeUpdate();
                pstmt.close();

                // 3. Update room status to 'Occupied'
                String updateRoomQuery = "UPDATE rooms SET status = 'Occupied' WHERE room_number = ?";
                pstmt = conn.prepareStatement(updateRoomQuery);
                pstmt.setString(1, roomNumber);
                pstmt.executeUpdate();
                pstmt.close();

                // 4. Insert into checkins table
                String insertCheckin = "INSERT INTO check_ins (reservation_id, customer_id, customer_name, customer_phone, room_number, check_in_date, status) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, 'checked-in')";
                pstmt = conn.prepareStatement(insertCheckin);
                pstmt.setInt(1, Integer.parseInt(reservationId));
                pstmt.setString(2, customerId);
                pstmt.setString(3, customerName);
                pstmt.setString(4, phone);
                pstmt.setString(5, roomNumber);
                pstmt.setString(6, checkInDateStr);
                pstmt.executeUpdate();
                pstmt.close();

                // 5. Insert into checkouts table with the original checkout date
                String insertCheckoutQuery = "INSERT INTO checkouts (reservation_id, customer_id, " +
                                          "room_number, check_in_date, check_out_date, total_amount, status) " +
                                          "VALUES (?, ?, ?, ?, ?, ?, 'Checked-in')";
                pstmt = conn.prepareStatement(insertCheckoutQuery);
                pstmt.setInt(1, Integer.parseInt(reservationId));
                pstmt.setString(2, customerId);
                pstmt.setString(3, roomNumber);
                pstmt.setString(4, checkInDateStr);
                pstmt.setString(5, checkoutDateStr); // Use the original checkout date
                pstmt.setBigDecimal(6, new BigDecimal(price));
                pstmt.executeUpdate();

                // Commit transaction if all operations succeed
                conn.commit();

                JOptionPane.showMessageDialog(this, 
                    "Reservation approved and room marked as occupied!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);

                // Reload pending reservations
                loadPendingReservations();

            } catch (Exception e) {
                // Rollback transaction if any operation fails
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                throw e;
            } finally {
                // Reset auto-commit and close resources
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                    }
                    if (pstmt != null) {
                        pstmt.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid reservation ID format. Please contact support.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error during approval: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void declineReservation() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to decline.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to decline this reservation?",
            "Confirm Decline",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return; // User canceled the decline
        }

        String reservationId = tableModel.getValueAt(selectedRow, 0).toString();
        String roomNumber = tableModel.getValueAt(selectedRow, 6).toString(); // Get the room number from the selected row

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // First check if the room is currently occupied
            String checkRoomStatus = "SELECT status FROM rooms WHERE room_number = ?";
            pstmt = conn.prepareStatement(checkRoomStatus);
            pstmt.setString(1, roomNumber);
            rs = pstmt.executeQuery();
            
            String currentStatus = null;
            if (rs.next()) {
                currentStatus = rs.getString("status");
            }
            rs.close();
            pstmt.close();

            // Update reservation status to 'Declined'
            String updateReservationQuery = "UPDATE reservations SET status = 'Declined' WHERE reservation_id = ?";
            pstmt = conn.prepareStatement(updateReservationQuery);
            pstmt.setString(1, reservationId);
            pstmt.executeUpdate();
            pstmt.close();

            // Only update room status if it's not already occupied
            if (!"Occupied".equalsIgnoreCase(currentStatus)) {
                String updateRoomQuery = "UPDATE rooms SET status = 'Available' WHERE room_number = ?";
                pstmt = conn.prepareStatement(updateRoomQuery);
                pstmt.setString(1, roomNumber);
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, 
                "Reservation declined!" + ("Occupied".equalsIgnoreCase(currentStatus) ? 
                    " Note: Room status was not changed as it is currently occupied." : ""), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);

            // Reload pending reservations and refresh available rooms
            loadPendingReservations();
            loadAvailableRooms(); // Refresh the room list in the combo box

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to decline reservation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerCheckIn("Manager Name", "manager@example.com").setVisible(true));
    }
}