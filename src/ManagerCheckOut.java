import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.regex.Pattern;

public class ManagerCheckOut extends JFrame {
    private DefaultTableModel tableModel;
    private JTable customerTable;
    private JTextField searchField, customeridField, nameField, phoneField, emailField, addressField;
    private JTextField roomTypeField, priceField, capacityField, roomNumberBox;
    private JDateChooser dateChooser;
    private JButton checkOutButton;
    //private JComboBox<String> roomTypeBox;
    private Map<String, String> roomPrices;
    private Timer timer;
    private String managerName; // Store manager's name
    private String managerEmail; // Store manager's email
 // Add this near your other instance variables
    private JButton extendStayButton;

    public ManagerCheckOut(String managerName, String managerEmail) {
        this.managerName = managerName; // Initialize manager's name
        this.managerEmail = managerEmail; // Initialize manager's email

        setTitle("Manager Dashboard");
        setSize(1230, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        timer = new Timer(1000, e -> SwingUtilities.invokeLater(this::updateCustomerTable));
        timer.start();

        // Exit confirmation when closing the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        ManagerCheckOut.this,
                        "Do you want to exit the program?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Room Price Mapping (Same as RoomCheck)
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
        sidebar.setLayout(new GridLayout(5, 1, 15, 15));
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
            
            if (label.equals("Check Out")) {
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

        // Search Panel
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Customer Info");
        JPanel searchPanel = new JPanel();
        searchButton.setBackground(Color.GRAY);
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

     // Update the columns to match check history
        String[] columns = {"Customer ID", "Customer Name", "Phone", "Email", "Address", 
                           "Room Number", "Room Type", "Room Capacity", "Price", 
                           "Check-In Date", "Check-Out Date", "Days Stayed", "Total Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.getTableHeader().setReorderingAllowed(false);
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Enables horizontal scrolling

        // Set preferred column widths (adjusted for all 10 columns)
        int[] columnWidths = {100, 150, 100, 120, 120, 100, 80, 120, 120, 150, 150, 120, 120};
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

        // Check-Out Info Panel
        JPanel checkOutPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        checkOutPanel.setBorder(BorderFactory.createTitledBorder("Check Out Info"));

        customeridField = new JTextField();
        customeridField.setEditable(false); // Make it read-only

        nameField = new JTextField();
        nameField.setEditable(false); // Make it read-only

        phoneField = new JTextField();
        phoneField.setEditable(false); // Make it read-only

        emailField = new JTextField();
        emailField.setEditable(false); // Make it read-only

        addressField = new JTextField();
        addressField.setEditable(false); // Make it read-only

        roomNumberBox = new JTextField();
        roomNumberBox.setEditable(false); // Make it read-only
        //roomTypeBox = new JComboBox<>(roomPrices.keySet().toArray(new String[0]));\
        
        roomTypeField = new JTextField();
        roomTypeField.setEditable(false); // Make it read-only
        
        capacityField = new JTextField();
        capacityField.setEditable(false); // Make it read-only

        priceField = new JTextField();
        priceField.setEditable(false); // Prevents manual editing

        dateChooser = new JDateChooser();
        dateChooser.setEnabled(false); // Make it completely uneditable
        dateChooser.getDateEditor().setEnabled(false); // Disable the text field editor

        // Change text color inside the text field
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setDisabledTextColor(Color.BLACK);


        
        

        // Room Type Selection Auto-Update Price
      /* roomTypeBox.addActionListener(e -> {
            String selectedRoom = (String) roomTypeBox.getSelectedItem();
            priceField.setText(roomPrices.getOrDefault(selectedRoom, ""));
        });*/

        // Add a selection listener to stop timer updates when selecting a customer
     // Update the table selection listener to properly fill all fields including checkout date
     // Update the table selection listener to properly handle walk-in customer IDs
     // Update the table selection listener to properly handle walk-in customer IDs
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() >= 0) {
                int selectedRow = customerTable.getSelectedRow();
                
                // Get all values directly from the table model
                String customerId = tableModel.getValueAt(selectedRow, 0).toString();
                String name = tableModel.getValueAt(selectedRow, 1).toString();
                String phone = tableModel.getValueAt(selectedRow, 2).toString();
                String email = tableModel.getValueAt(selectedRow, 3).toString();
                String address = tableModel.getValueAt(selectedRow, 4).toString();
                String roomNumber = tableModel.getValueAt(selectedRow, 5).toString();
                String roomType = tableModel.getValueAt(selectedRow, 6).toString();
                String capacity = tableModel.getValueAt(selectedRow, 7).toString();
                String price = tableModel.getValueAt(selectedRow, 8).toString();
                String checkOutDate = tableModel.getValueAt(selectedRow, 10).toString();

                // Fill all text fields
                customeridField.setText(customerId);
                nameField.setText(name);
                phoneField.setText(phone);
                emailField.setText(email);
                addressField.setText(address);
                roomNumberBox.setText(roomNumber);
                roomTypeField.setText(roomType);
                capacityField.setText(capacity);
                priceField.setText(price);

                // Set checkout date
                try {
                    if (!checkOutDate.equals("Not checked out")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dateChooser.setDate(dateFormat.parse(checkOutDate));
                    } else {
                        dateChooser.setDate(new Date());
                    }
                } catch (Exception ex) {
                    dateChooser.setDate(new Date());
                }

                // Stop the timer while editing
                timer.stop();
            }
        });

        // Add a focus listener to update the minimum date dynamically
        dateChooser.addPropertyChangeListener("ancestor", evt -> dateChooser.setMinSelectableDate(new Date()));
        checkOutButton = new JButton("Check Out");
        checkOutButton.setBackground(new Color(204, 0, 0));
        checkOutButton.setForeground(Color.WHITE);
     // In initializeUI(), add this after creating checkOutButton
        extendStayButton = new JButton("Extend Stay");
        extendStayButton.setBackground(new Color(0, 153, 51));
        extendStayButton.setForeground(Color.WHITE);
       

        checkOutPanel.add(new JLabel("Customer ID:"));
        checkOutPanel.add(customeridField);
        checkOutPanel.add(new JLabel("Customer Name:"));
        checkOutPanel.add(nameField);
        checkOutPanel.add(new JLabel("Phone:"));
        checkOutPanel.add(phoneField);
        checkOutPanel.add(new JLabel("Email:"));
        checkOutPanel.add(emailField);
        checkOutPanel.add(new JLabel("Address:"));
        checkOutPanel.add(addressField);
        checkOutPanel.add(new JLabel("Room Number:"));
        checkOutPanel.add(roomNumberBox);
        checkOutPanel.add(new JLabel("Room Type:"));
        checkOutPanel.add(roomTypeField);
        checkOutPanel.add(new JLabel("Room Capacity:"));
        checkOutPanel.add(capacityField);
        checkOutPanel.add(new JLabel("Price:"));
        checkOutPanel.add(priceField);
        checkOutPanel.add(new JLabel("Check Out Date:"));
        checkOutPanel.add(dateChooser);
        checkOutPanel.add(new JLabel());
        checkOutPanel.add(checkOutButton);
        checkOutPanel.add(extendStayButton); // Add this to your checkOutPanel

        mainPanel.add(checkOutPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Button Actions
        searchButton.addActionListener(e -> searchCustomer());
        checkOutButton.addActionListener(e -> processCheckOut());
     // Add this action listener (put it near your other action listeners)
        extendStayButton.addActionListener(e -> extendStay());

        // Apply Document Filters for fields
        applyDocumentFilters();
     // Add this in the constructor after UI setup
        Timer autoCheckoutTimer = new Timer(1000 * 60 * 5, e -> checkAutoCheckouts()); // Check every 5 minutes
        autoCheckoutTimer.start();
    }
    
    private void checkAutoCheckouts() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = Databaseconnection.getConnection();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();
            String currentDateStr = dateFormat.format(currentDate);
            
            // Find all guests who should be auto-checked-out
            String query = "SELECT * FROM checkouts WHERE status = 'Checked-in' AND check_out_date <= ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentDateStr);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                // Get all necessary data
                String customerId = rs.getString("customer_id");
                String roomNumber = rs.getString("room_number");
                String checkInDateStr = rs.getString("check_in_date");
                
                // Calculate days stayed and total amount based on CURRENT DATE
                Date checkInDate = dateFormat.parse(checkInDateStr);
                long diff = currentDate.getTime() - checkInDate.getTime();
                int daysStayed = (int) (diff / (1000 * 60 * 60 * 24));
                daysStayed = daysStayed == 0 ? 1 : daysStayed;
                
                // Get room price
                String priceQuery = "SELECT price FROM rooms WHERE room_number = ?";
                PreparedStatement priceStmt = conn.prepareStatement(priceQuery);
                priceStmt.setString(1, roomNumber);
                ResultSet priceRs = priceStmt.executeQuery();
                
                if (priceRs.next()) {
                    double price = priceRs.getDouble("price");
                    double totalAmount = price * daysStayed;
                    
                    // Move to history
                    String insertHistory = "INSERT INTO check_history " +
                        "(customer_id, customer_name, phone, email, address, " +
                        "room_number, room_type, capacity, price, " +
                        "check_in_date, check_out_date, days_stayed, total_amount) " +
                        "SELECT c.customer_id, c.customer_fullname, c.customer_phoneNumber, " +
                        "c.customer_email, c.customer_address, ch.room_number, " +
                        "r.room_type, r.capacity, r.price, ch.check_in_date, " +
                        "?, ?, ? " +  // Using current date for checkout
                        "FROM checkouts ch " +
                        "JOIN customers c ON ch.customer_id = c.customer_id " +
                        "JOIN rooms r ON ch.room_number = r.room_number " +
                        "WHERE ch.customer_id = ? AND ch.room_number = ?";
                    
                    PreparedStatement historyStmt = conn.prepareStatement(insertHistory);
                    historyStmt.setString(1, currentDateStr);
                    historyStmt.setInt(2, daysStayed);
                    historyStmt.setDouble(3, totalAmount);
                    historyStmt.setString(4, customerId);
                    historyStmt.setString(5, roomNumber);
                    historyStmt.executeUpdate();
                    
                    // Update statuses
                    String updateCheckout = "UPDATE checkouts SET check_out_date = ?, total_amount = ?, status = 'Checked-out' " +
                        "WHERE customer_id = ? AND room_number = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateCheckout);
                    updateStmt.setString(1, currentDateStr);
                    updateStmt.setDouble(2, totalAmount);
                    updateStmt.setString(3, customerId);
                    updateStmt.setString(4, roomNumber);
                    updateStmt.executeUpdate();
                    
                    String updateRoom = "UPDATE rooms SET status = 'Available' " +
                        "WHERE room_number = ?";
                    PreparedStatement roomStmt = conn.prepareStatement(updateRoom);
                    roomStmt.setString(1, roomNumber);
                    roomStmt.executeUpdate();
                    
                    // Refresh the table
                    SwingUtilities.invokeLater(this::updateCustomerTable);
                }
            }
        } catch (Exception e) {
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
    }
    
    
    
 // Add this new method to handle stay extension
    private void extendStay() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a customer to extend stay!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get current data
        String customerId = tableModel.getValueAt(selectedRow, 0).toString();
        String roomNumber = tableModel.getValueAt(selectedRow, 5).toString();
        double pricePerDay = Double.parseDouble(tableModel.getValueAt(selectedRow, 8).toString());
        String currentCheckoutStr = tableModel.getValueAt(selectedRow, 10).toString();
        String checkInDateStr = tableModel.getValueAt(selectedRow, 9).toString();

        // Create extension dialog
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Current info
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Current Check-out:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(currentCheckoutStr), gbc);
        
        // New date
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("New Check-out Date:"), gbc);
        gbc.gridx = 1;
        JDateChooser newCheckoutChooser = new JDateChooser();
        newCheckoutChooser.setPreferredSize(new Dimension(150, 25));
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = currentCheckoutStr.equals("Not checked out") ? new Date() : dateFormat.parse(currentCheckoutStr);
            newCheckoutChooser.setDate(currentDate);
        } catch (Exception e) {
            newCheckoutChooser.setDate(new Date());
        }
        
        newCheckoutChooser.setMinSelectableDate(new Date());
        panel.add(newCheckoutChooser, gbc);

        // Price calculation preview
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Additional Cost:"), gbc);
        gbc.gridx = 1;
        JLabel costLabel = new JLabel("Calculating...");
        panel.add(costLabel, gbc);
        
        // Add listener for date changes
        newCheckoutChooser.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date checkInDate = dateFormat.parse(checkInDateStr);
                    Date newCheckoutDate = newCheckoutChooser.getDate();
                    
                    if (newCheckoutDate == null) {
                        costLabel.setText("Please select a valid date");
                        return;
                    }
                    
                    // Ensure checkout date is after check-in date
                    if (newCheckoutDate.before(checkInDate)) {
                        costLabel.setText("Invalid date (before check-in)");
                        return;
                    }
                    
                    long diff = newCheckoutDate.getTime() - checkInDate.getTime();
                    int daysStayed = (int) (diff / (1000 * 60 * 60 * 24));
                    daysStayed = Math.max(daysStayed, 1); // Minimum 1 day
                    double totalAmount = pricePerDay * daysStayed;
                    
                    costLabel.setText(String.format("₱%.2f (%d days)", totalAmount, daysStayed));
                } catch (Exception ex) {
                    costLabel.setText("Error calculating");
                    ex.printStackTrace();
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(
            this, 
            panel, 
            "Extend Stay for " + tableModel.getValueAt(selectedRow, 1).toString(), 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION && newCheckoutChooser.getDate() != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = newCheckoutChooser.getDate();
                String formattedDate = dateFormat.format(newDate);
                
                // Calculate days stayed and total amount
                Date checkInDate = dateFormat.parse(checkInDateStr);
                long diff = newDate.getTime() - checkInDate.getTime();
                int daysStayed = (int) (diff / (1000 * 60 * 60 * 24));
                daysStayed = Math.max(daysStayed, 1); // Minimum 1 day
                double totalAmount = pricePerDay * daysStayed;
                
                // Update database
                boolean success = updateCheckoutDateInDatabase(
                    customerId, 
                    roomNumber, 
                    newDate,
                    totalAmount
                );
                
                if (success) {
                    // Update UI immediately
                    tableModel.setValueAt(formattedDate, selectedRow, 10);
                    tableModel.setValueAt(daysStayed, selectedRow, 11);
                    tableModel.setValueAt(totalAmount, selectedRow, 12);
                    dateChooser.setDate(newDate);
                    
                    JOptionPane.showMessageDialog(
                        this,
                        "Stay extended successfully!\n" +
                        "New Check-out: " + formattedDate + "\n" +
                        "Total Amount: ₱" + String.format("%.2f", totalAmount),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // Refresh the entire table to ensure consistency
                    SwingUtilities.invokeLater(() -> {
                        updateCustomerTable();
                        // Restore selection after refresh
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (tableModel.getValueAt(i, 0).toString().equals(customerId) && 
                                tableModel.getValueAt(i, 5).toString().equals(roomNumber)) {
                                customerTable.setRowSelectionInterval(i, i);
                                break;
                            }
                        }
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error extending stay: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        }
    }

    private boolean updateCheckoutDateInDatabase(String customerId, String roomNumber, Date newCheckoutDate, double totalAmount) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = Databaseconnection.getConnection();
            // Start transaction
            conn.setAutoCommit(false);
            
            // 1. Update checkouts table
            String query = "UPDATE checkouts SET " +
                          "check_out_date = ?, " +
                          "total_amount = ? " +
                          "WHERE customer_id = ? AND room_number = ? AND status = 'Checked-in'";
            
            pstmt = conn.prepareStatement(query);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(1, dateFormat.format(newCheckoutDate));
            pstmt.setDouble(2, totalAmount);
            pstmt.setString(3, customerId);
            pstmt.setString(4, roomNumber);
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                conn.rollback();
                return false;
            }
            
            // 2. Update reservations table if this was a reservation
            query = "UPDATE reservations SET check_out_date = ? " +
                    "WHERE customer_id = ? AND room_number = ? AND (status = 'Approved' OR status = 'Checked-in')";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, dateFormat.format(newCheckoutDate));
            pstmt.setString(2, customerId);
            pstmt.setString(3, roomNumber);
            pstmt.executeUpdate();
            
            // 3. Update rooms table to keep status as Occupied
            query = "UPDATE rooms SET status = 'Occupied' WHERE room_number = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomNumber);
            pstmt.executeUpdate();
            
            // Commit transaction
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
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
    
    
    private void applyDocumentFilters() {
        // Customer ID - Only digits allowed
        /*((AbstractDocument) customeridField.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });*/

        // Phone Field - Allow only digits and limit length to 10 digits
        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        // Room Number - Only digits allowed
        ((AbstractDocument) roomNumberBox.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        // Room Capacity - Only digits allowed
        ((AbstractDocument) capacityField.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        // Customer Name - Only letters and spaces allowed
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[a-zA-Z\\s]+")) { // Allow only letters and spaces
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[a-zA-Z\\s]+")) { // Allow only letters and spaces
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
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
                populateCheckOutFields(i);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Customer not found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void populateCheckOutFields(int rowIndex) {
        // Get all values directly from table
        String customerId = tableModel.getValueAt(rowIndex, 0).toString();
        String name = tableModel.getValueAt(rowIndex, 1).toString();
        String phone = tableModel.getValueAt(rowIndex, 2).toString();
        String email = tableModel.getValueAt(rowIndex, 3).toString();
        String address = tableModel.getValueAt(rowIndex, 4).toString();
        String roomNumber = tableModel.getValueAt(rowIndex, 5).toString();
        String roomType = tableModel.getValueAt(rowIndex, 6).toString();
        String capacity = tableModel.getValueAt(rowIndex, 7).toString();
        String price = tableModel.getValueAt(rowIndex, 8).toString();
        String checkOutDate = tableModel.getValueAt(rowIndex, 10).toString();

        // Fill all fields
        customeridField.setText(customerId);
        nameField.setText(name);
        phoneField.setText(phone);
        emailField.setText(email);
        addressField.setText(address);
        roomNumberBox.setText(roomNumber);
        roomTypeField.setText(roomType);
        capacityField.setText(capacity);
        priceField.setText(price);

        // Set checkout date (which should always exist)
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateChooser.setDate(dateFormat.parse(checkOutDate));
        } catch (Exception ex) {
            // Fallback to current date if parsing fails
            dateChooser.setDate(new Date());
            JOptionPane.showMessageDialog(this, 
                "Error parsing checkout date. Using current date instead.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateCustomerTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Databaseconnection.getConnection();
            if (conn == null) {
                System.err.println("Failed to connect to the database.");
                return;
            }

            // Remember selection
            int selectedRow = customerTable.getSelectedRow();
            String selectedCustomerId = selectedRow >= 0 ? 
                tableModel.getValueAt(selectedRow, 0).toString() : null;
            
            // Clear table before updating
            tableModel.setRowCount(0);

            // Modified query assuming checkout_date is never null
            String query = "SELECT " +
                    "ch.customer_id, " +
                    "COALESCE(c.customer_fullname, 'Walk-in Customer') AS customer_name, " +
                    "COALESCE(c.customer_phoneNumber, '') AS phone, " +
                    "COALESCE(c.customer_email, '') AS email, " +
                    "COALESCE(c.customer_address, '') AS address, " +
                    "ch.room_number, r.room_type, r.capacity, r.price, " +
                    "DATE_FORMAT(ch.check_in_date, '%Y-%m-%d %H:%i:%s') AS check_in_date, " +
                    "DATE_FORMAT(ch.check_out_date, '%Y-%m-%d %H:%i:%s') AS check_out_date, " +
                    "DATEDIFF(ch.check_out_date, ch.check_in_date) AS days_stayed, " +
                    "r.price * DATEDIFF(ch.check_out_date, ch.check_in_date) AS total_amount " +
                    "FROM checkouts ch " +
                    "LEFT JOIN customers c ON ch.customer_id = c.customer_id " +
                    "JOIN rooms r ON ch.room_number = r.room_number " +
                    "WHERE ch.status = 'Checked-in' AND ch.check_out_date IS NOT NULL";
                
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                String customerId = rs.getString("customer_id");
                String customerName = rs.getString("customer_name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                String capacity = rs.getString("capacity");
                String price = rs.getString("price");
                String checkInDate = rs.getString("check_in_date");
                String checkOutDate = rs.getString("check_out_date");
                Integer daysStayed = rs.getInt("days_stayed");
                Double totalAmount = rs.getDouble("total_amount");

                tableModel.addRow(new Object[]{
                    customerId,
                    customerName,
                    phone,
                    email,
                    address,
                    roomNumber,
                    roomType,
                    capacity,
                    price,
                    checkInDate,
                    checkOutDate,
                    daysStayed,
                    totalAmount
                });
            }

            // Restore selection if it was valid
            if (selectedCustomerId != null) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).toString().equals(selectedCustomerId)) {
                        customerTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load customer data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

 // Modify your processCheckOut method to move data to history
    private void processCheckOut() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Customer to check out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get all data from table with null checks
        String customerId = getSafeStringValue(selectedRow, 0);
        String customerName = getSafeStringValue(selectedRow, 1);
        String phone = getSafeStringValue(selectedRow, 2);
        String email = getSafeStringValue(selectedRow, 3);
        String address = getSafeStringValue(selectedRow, 4);
        String roomNumber = getSafeStringValue(selectedRow, 5);
        String roomType = getSafeStringValue(selectedRow, 6);
        String capacity = getSafeStringValue(selectedRow, 7);
        String price = getSafeStringValue(selectedRow, 8);
        String checkInDateStr = getSafeStringValue(selectedRow, 9);
        String originalCheckOutStr = getSafeStringValue(selectedRow, 10);
        
        // Always use current date for checkout when checking out manually
        Date checkOutDate = new Date();
        dateChooser.setDate(checkOutDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date checkInDate = dateFormat.parse(checkInDateStr);
            
            if (checkOutDate.before(checkInDate)) {
                JOptionPane.showMessageDialog(this, 
                    "Check-out date cannot be before check-in date!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculate duration and total
            long diffInMillis = checkOutDate.getTime() - checkInDate.getTime();
            int daysStayed = (int) Math.ceil(diffInMillis / (1000.0 * 60 * 60 * 24));
            daysStayed = Math.max(daysStayed, 1);
            double pricePerDay = Double.parseDouble(price);
            double totalAmount = pricePerDay * daysStayed;

            String checkOutDateStr = dateFormat.format(checkOutDate);

            // Create confirmation message
            String confirmationMessage = String.format(
                "<html><b>Confirm Check-Out Details:</b><br><br>" +
                "Customer: %s<br>" +
                "Room: %s (%s)<br>" +
                "Check-In: %s<br>" +
                "Check-Out: %s<br>" +
                "Days Stayed: %d<br>" +
                "Daily Rate: ₱%.2f<br>" +
                "<b>Total Amount: ₱%.2f</b></html>",
                customerName, roomNumber, roomType,
                checkInDateStr, checkOutDateStr,
                daysStayed, pricePerDay, totalAmount
            );

            int confirm = JOptionPane.showConfirmDialog(
                this, 
                confirmationMessage,
                "Confirm Check Out", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                Connection conn = Databaseconnection.getConnection();
                conn.setAutoCommit(false);

                try {
                    // 1. Insert into check_history (for both manager and customer)
                    String historySql = "INSERT INTO check_history " +
                        "(customer_id, customer_name, phone, email, address, " +
                        "room_number, room_type, capacity, price, " +
                        "check_in_date, check_out_date, days_stayed, total_amount) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    
                    PreparedStatement pstmt = conn.prepareStatement(historySql);
                    pstmt.setString(1, customerId);
                    pstmt.setString(2, customerName);
                    pstmt.setString(3, phone);
                    pstmt.setString(4, email);
                    pstmt.setString(5, address);
                    pstmt.setString(6, roomNumber);
                    pstmt.setString(7, roomType);
                    pstmt.setString(8, capacity);
                    pstmt.setString(9, price);
                    pstmt.setString(10, checkInDateStr);
                    pstmt.setString(11, checkOutDateStr);
                    pstmt.setInt(12, daysStayed);
                    pstmt.setDouble(13, totalAmount);
                    pstmt.executeUpdate();

                    // 2. Update checkouts table
                    pstmt = conn.prepareStatement(
                        "UPDATE checkouts SET check_out_date = ?, total_amount = ?, status = 'Checked-out' " +
                        "WHERE customer_id = ? AND room_number = ?");
                    pstmt.setString(1, checkOutDateStr);
                    pstmt.setDouble(2, totalAmount);
                    pstmt.setString(3, customerId);
                    pstmt.setString(4, roomNumber);
                    pstmt.executeUpdate();

                    // 3. Update room status to 'Not Available' for cleaning
                    pstmt = conn.prepareStatement(
                        "UPDATE rooms SET status = 'Not Available' WHERE room_number = ?");
                    pstmt.setString(1, roomNumber);
                    pstmt.executeUpdate();

                    conn.commit();

                    // Refresh UI
                    tableModel.removeRow(selectedRow);
                    
                    // Show receipt
                    String receipt = String.format(
                        "<html><center><h3>Check-Out Receipt</h3>" +
                        "<table border='0' width='300'>" +
                        "<tr><td align='left'>Customer:</td><td align='right'>%s</td></tr>" +
                        "<tr><td align='left'>Room:</td><td align='right'>%s (%s)</td></tr>" +
                        "<tr><td align='left'>Check-In:</td><td align='right'>%s</td></tr>" +
                        "<tr><td align='left'>Check-Out:</td><td align='right'>%s</td></tr>" +
                        "<tr><td align='left'>Days Stayed:</td><td align='right'>%d</td></tr>" +
                        "<tr><td align='left'>Daily Rate:</td><td align='right'>₱%.2f</td></tr>" +
                        "<tr><td colspan='2'><hr></td></tr>" +
                        "<tr><td align='left'><b>Total Amount:</b></td><td align='right'><b>₱%.2f</b></td></tr>" +
                        "</table></center></html>",
                        customerName, roomNumber, roomType,
                        checkInDateStr, checkOutDateStr,
                        daysStayed, pricePerDay, totalAmount
                    );
                    
                    JOptionPane.showMessageDialog(
                        this,
                        receipt,
                        "Check-Out Complete",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // Notify about room status
                    JOptionPane.showMessageDialog(
                        this,
                        "Room " + roomNumber + " has been marked as 'Not Available' for cleaning.",
                        "Room Status Updated",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // Offer to print receipt
                    int printOption = JOptionPane.showConfirmDialog(
                        this,
                        "Would you like to print the receipt?",
                        "Print Receipt",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (printOption == JOptionPane.YES_OPTION) {
                        printReceipt(customerName, roomNumber, roomType, 
                                   checkInDateStr, checkOutDateStr, 
                                   daysStayed, pricePerDay, totalAmount);
                    }
                    
                    clearFields();
                    
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                    if (conn != null) conn.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "Error during check-out: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String getSafeStringValue(int row, int col) {
        Object value = tableModel.getValueAt(row, col);
        return value != null ? value.toString() : "";
    }

    // Add this new method for printing
    private void printReceipt(String customerName, String roomNumber, String roomType,
                             String checkInDate, String checkOutDate, int daysStayed,
                             double pricePerDay, double totalAmount) {
        try {
            // Create a text version of the receipt
            String receiptText = String.format(
                "CHECK-OUT RECEIPT\n\n" +
                "Customer: %s\n" +
                "Room: %s (%s)\n" +
                "Check-In: %s\n" +
                "Check-Out: %s\n" +
                "Days Stayed: %d\n" +
                "Daily Rate: ₱%.2f\n" +
                "----------------------------\n" +
                "Total Amount: ₱%.2f\n\n" +
                "Thank you for staying with us!",
                customerName, roomNumber, roomType,
                checkInDate, checkOutDate,
                daysStayed, pricePerDay, totalAmount
            );

            // Print using Java's printing API
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Check-Out Receipt");

            // Define printable content
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Draw receipt content
                String[] lines = receiptText.split("\n");
                int y = 100;
                for (String line : lines) {
                    g2d.drawString(line, 50, y);
                    y += 20;
                }

                return Printable.PAGE_EXISTS;
            });

            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, 
                "Error printing receipt: " + e.getMessage(), 
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        customeridField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        roomNumberBox.setText("");
        roomTypeField.setText("");
        capacityField.setText("");
        priceField.setText("");
        dateChooser.setDate(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerCheckOut("Manager Name", "manager@example.com").setVisible(true));
    }
}