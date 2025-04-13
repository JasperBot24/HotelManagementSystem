import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.*;
import java.sql.*;
import java.util.List;

// NumericFilter class definition
class NumericFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string.matches("\\d+")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text.matches("\\d+")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}

// Custom JTextField with placeholder
class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.drawString(placeholder, 5, 20); // Adjusted y-coordinate to 25 for better alignment
            g2.dispose();
        }
    }
}

public class ManagerRoomCheck extends JFrame {
    private DefaultTableModel tableModel;
    private JTable roomTable;
    private JTextField roomNumberField, capacityField;
    private PlaceholderTextField searchField; // Use custom JTextField with placeholder
    private JComboBox<String> roomTypeBox, priceBox, statusBox;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JDateChooser dateChooser;
    private Map<String, String> roomPrices;
    private Map<String, String> roomCapacities; // Map for room capacities
    private JComboBox<String> searchFilterBox;
    private String managerName;
    private String managerEmail;

    public ManagerRoomCheck(String managerName, String managerEmail) {
        this.managerName = managerName;
        this.managerEmail = managerEmail;

        setTitle("Manager Dashboard");
        setSize(1230, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Add window closing confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                        ManagerRoomCheck.this, "Do you want to exit the program?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

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
            
            if (label.equals("Room Check")) {
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

        // Room Price and Capacity Mapping
        roomPrices = new HashMap<>();
        roomPrices.put("Budget Room", "₱999");
        roomPrices.put("Single Room", "₱1499");
        roomPrices.put("Double Room", "₱1999");
        roomPrices.put("Twin Room", "₱2499");
        roomPrices.put("Queen Room", "₱3499");
        roomPrices.put("Deluxe Room", "₱4499");
        roomPrices.put("Suite", "₱5499");

        roomCapacities = new HashMap<>();
        roomCapacities.put("Budget Room", "2");
        roomCapacities.put("Single Room", "2");
        roomCapacities.put("Double Room", "4");
        roomCapacities.put("Twin Room", "4");
        roomCapacities.put("Queen Room", "6");
        roomCapacities.put("Deluxe Room", "6");
        roomCapacities.put("Suite", "8");

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Manage Rooms", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Room Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        roomNumberField = new JTextField();
        capacityField = new JTextField();
        capacityField.setEditable(false);

        ((AbstractDocument) roomNumberField.getDocument()).setDocumentFilter(new NumericFilter());
        ((AbstractDocument) capacityField.getDocument()).setDocumentFilter(new NumericFilter());

        roomTypeBox = new JComboBox<>(roomPrices.keySet().toArray(new String[0]));
        priceBox = new JComboBox<>(roomPrices.values().toArray(new String[0]));
        priceBox.setEnabled(false); // Prevent manual editing
        priceBox.setForeground(Color.BLACK); // Set text color to black
        priceBox.setBackground(Color.LIGHT_GRAY); // Optional: Light gray background
        ((JTextField) priceBox.getEditor().getEditorComponent()).setDisabledTextColor(Color.BLACK);
       

        statusBox = new JComboBox<>(new String[]{"Available", "Not Available", "Occupied"});
        dateChooser = new JDateChooser();
        dateChooser.setEnabled(true); // Make it completely uneditable
        dateChooser.getDateEditor().setEnabled(false); // Disable the text field editor

        // Change text color inside the text field
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setDisabledTextColor(Color.BLACK);

        // Add a focus listener to update the minimum date dynamically
        dateChooser.addPropertyChangeListener("ancestor", evt -> dateChooser.setMinSelectableDate(new Date()));
        
        // Add Panel
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(0, 153, 51)); // Green color for Add button
        addButton.setForeground(Color.WHITE);
        
     // Edit & Delete Panel
        //JPanel editPanel = new JPanel();
        JButton editButton = new JButton("Edit");
        editButton.setBackground(new Color(255, 153, 0)); // Orange color for Edit button
        editButton.setForeground(Color.WHITE);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(204, 0, 0)); // Red color for Delete button
        deleteButton.setForeground(Color.WHITE);
        
        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomNumberField);
        formPanel.add(new JLabel("Room Type:"));
        formPanel.add(roomTypeBox);
        formPanel.add(new JLabel("Room Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceBox);
        formPanel.add(new JLabel("Room Status:"));
        formPanel.add(statusBox);
        formPanel.add(new JLabel("Date Added:"));
        formPanel.add(dateChooser);
        formPanel.add(new JLabel());
        formPanel.add(addButton);
        formPanel.add(editButton);
        formPanel.add(deleteButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);

     

        // Inside the ManagerRoomCheck constructor
        // Create search panel
        JPanel searchPanel = new JPanel();
        String[] searchOptions = {"All", "Room Number", "Room Type", "Room Capacity", "Room Status"};
        searchFilterBox = new JComboBox<>(searchOptions); // Store globally
        searchField = new PlaceholderTextField("Search"); // Use custom JTextField with placeholder
        searchField.setColumns(20);
        searchField.setPreferredSize(new Dimension(10, 30)); // Set preferred size for better visibility

        
     


        // Add components to search panel
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchFilterBox);
        searchPanel.add(searchField);

        // Add search panel to the top
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = {"Room Number", "Room Type", "Room Capacity", "Price", "Room Status", "Date Added"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(210, 210, 210) : Color.WHITE); // Alternating colors
                } else {
                    c.setBackground(new Color(173, 216, 230)); // Light blue for selected row
                }
                return c;
            }
        };
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getTableHeader().setReorderingAllowed(false);
        rowSorter = new TableRowSorter<>(tableModel);
        roomTable.setRowSorter(rowSorter);


        JScrollPane scrollPane = new JScrollPane(roomTable);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        
        
        //editPanel.add(addButton);
        //editPanel.add(editButton);
        //editPanel.add(deleteButton);
        //mainPanel.add(editPanel, BorderLayout.EAST);

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Event Listeners
        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editRoom());
        deleteButton.addActionListener(e -> deleteRoom());

     // Dynamic search as the manager types
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim().toLowerCase();
                String filterType = (String) searchFilterBox.getSelectedItem();

                // Determine the column index based on the selected filter type
                int columnIndex = switch (filterType) {
                    case "Room Number" -> 0;
                    case "Room Type" -> 1;
                    case "Room Capacity" -> 2;
                    case "Room Status" -> 4;
                    default -> -1;  // "All" selected, search all columns
                };

                // Create a list of filters
                List<RowFilter<Object, Object>> filters = new ArrayList<>();

                // If a specific filter is selected, apply it
                if (columnIndex != -1) {
                    filters.add(RowFilter.regexFilter("(?i)" + searchText, columnIndex));
                } else {
                    filters.add(RowFilter.regexFilter("(?i)" + searchText)); // Search all columns
                }

                // Always search "Available" in Room Status (column 4)
                if (searchText.equals("available")) {
                    filters.add(RowFilter.regexFilter("(?i)^Available$", 4)); // Exact match for "Available"
                }

                // Apply the combined filter
                rowSorter.setRowFilter(RowFilter.andFilter(filters));
            }
        });


        // Auto-fill room capacity based on room type
        roomTypeBox.addActionListener(e -> {
            String selectedType = (String) roomTypeBox.getSelectedItem();
            priceBox.setSelectedItem(roomPrices.get(selectedType));
            capacityField.setText(roomCapacities.get(selectedType)); // Auto-fill capacity
            
        });

        // Auto-fill form fields when a row is selected
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow >= 0) {
                roomNumberField.setText((String) tableModel.getValueAt(selectedRow, 0));
                roomTypeBox.setSelectedItem(tableModel.getValueAt(selectedRow, 1));
                capacityField.setText((String) tableModel.getValueAt(selectedRow, 2));
                priceBox.setSelectedItem(tableModel.getValueAt(selectedRow, 3));
                statusBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
                try {
                    dateChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String) tableModel.getValueAt(selectedRow, 5)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Load rooms from database
        loadRoomsFromDatabase();
    }

    private void addRoom() {
        // Get values from the form fields
        String roomNo = roomNumberField.getText().trim();
        String roomType = (String) roomTypeBox.getSelectedItem();
        String capacity = capacityField.getText().trim();
        String price = ((String) priceBox.getSelectedItem()).replace("₱", ""); // Remove peso sign for database storage
        String status = (String) statusBox.getSelectedItem();
        Date dateAdded = dateChooser.getDate();

        // Validate required fields
        if (roomNo.isEmpty() || roomType == null || dateAdded == null) {
            JOptionPane.showMessageDialog(this, "Please fill out Room Number, Room Type, and Date Added!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate room number
        try (Connection conn = Databaseconnection.getConnection()) {
            String query = "SELECT * FROM rooms WHERE room_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, roomNo);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Room number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Proceed with adding the room
        String dateAddedStr = new SimpleDateFormat("yyyy-MM-dd").format(dateAdded);

        try (Connection conn = Databaseconnection.getConnection()) {
            String query = "INSERT INTO rooms (room_number, room_type, capacity, price, status, date_added) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, roomNo);
                pstmt.setString(2, roomType);
                pstmt.setInt(3, Integer.parseInt(capacity));
                pstmt.setDouble(4, Double.parseDouble(price));
                pstmt.setString(5, status);
                pstmt.setString(6, dateAddedStr);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add room to database!", "Error", JOptionPane.ERROR_MESSAGE);
            
            
            return;
        }

        // Reload rooms from database to reflect changes
        loadRoomsFromDatabase();
        JOptionPane.showMessageDialog(this, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Clear the form fields after adding
        roomNumberField.setText("");
        capacityField.setText("");
        roomTypeBox.setSelectedIndex(0);
        priceBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        dateChooser.setDate(null);
    }

    private void deleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a room to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show confirmation prompt
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this room?",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        // Proceed only if the user selects "Yes"
        if (response == JOptionPane.YES_OPTION) {
            String roomNo = (String) tableModel.getValueAt(selectedRow, 0);

            try (Connection conn = Databaseconnection.getConnection()) {
                String query = "DELETE FROM rooms WHERE room_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, roomNo);
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to delete room from database!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Reload rooms from database to reflect changes
            loadRoomsFromDatabase();
        }
    }

    private void loadRoomsFromDatabase() {
        tableModel.setRowCount(0); // Clear the table
        try (Connection conn = Databaseconnection.getConnection()) {
            String query = "SELECT * FROM rooms";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String roomNo = rs.getString("room_number");
                    String roomType = rs.getString("room_type");
                    String capacity = rs.getString("capacity");
                    String price = "₱" + rs.getString("price"); // Add peso sign
                    String status = rs.getString("status");
                    String dateAdded = rs.getString("date_added");
                    tableModel.addRow(new Object[]{roomNo, roomType, capacity, price, status, dateAdded});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load rooms from database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a room to edit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

     // Check if room is occupied and show warning
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);
        if ("Occupied".equals(currentStatus)) {
            int response = JOptionPane.showConfirmDialog(
                this,
                "This room is currently occupied. Are you sure you want to edit it?",
                "Occupied Room Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (response != JOptionPane.YES_OPTION) {
                return; // User chose not to proceed
            }
        }


     // Show confirmation prompt
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to edit this room?",
                "Edit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        // Proceed only if the user selects "Yes"
        if (response == JOptionPane.YES_OPTION) {
            String roomNo = roomNumberField.getText();
            String roomType = (String) roomTypeBox.getSelectedItem();
            String capacity = capacityField.getText();
            String price = ((String) priceBox.getSelectedItem()).replace("₱", ""); // Remove peso sign for database storage
            String status = (String) statusBox.getSelectedItem();
            String dateAdded = new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate());

            try (Connection conn = Databaseconnection.getConnection()) {
                String query = "UPDATE rooms SET room_type = ?, capacity = ?, price = ?, status = ?, date_added = ? WHERE room_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, roomType);
                    pstmt.setInt(2, Integer.parseInt(capacity));
                    pstmt.setDouble(3, Double.parseDouble(price));
                    pstmt.setString(4, status);
                    pstmt.setString(5, dateAdded);
                    pstmt.setString(6, roomNo);
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update room in database!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Reload rooms from database to reflect changes
            loadRoomsFromDatabase();
            JOptionPane.showMessageDialog(this, "Room updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerRoomCheck("Manager Name", "manager@example.com").setVisible(true));
    }
}