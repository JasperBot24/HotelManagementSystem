import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerCheckHistory extends JFrame {
    private DefaultTableModel tableModel;
    private JTable historyTable;
    private JTextField searchField;
    private String managerName; // Store manager's name
    private String managerEmail; // Store manager's email

    public ManagerCheckHistory(String managerName, String managerEmail) {
        this.managerName = managerName; // Initialize manager's name
        this.managerEmail = managerEmail; // Initialize manager's email

        setTitle("Manager Dashboard");
        setSize(1230, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

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
            
            if (label.equals("Check History")) {
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

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(Color.GRAY);
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table Panel
     // Use the same column names
        String[] columns = {"Customer ID", "Customer Name", "Phone", "Email", "Address", 
                           "Room Number", "Room Type", "Room Capacity", "Price", 
                           "Check-In Date", "Check-Out Date", "Days Stayed", "Total Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Enables horizontal scrolling

        // Set column widths
        int[] columnWidths = {100, 150, 100, 130, 150, 100, 80, 80, 100, 150, 150, 120, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            historyTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Enable scrolling
        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Search Functionality
        searchButton.addActionListener(e -> searchHistory());

        // Window Close Confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Do you want to exit the program?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        // Call this in the constructor after initializing components
        loadCheckHistory();
    }

    private void searchHistory() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a keyword to search!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                if (value != null && value.toString().toLowerCase().contains(searchText)) {
                    historyTable.setRowSelectionInterval(i, i);
                    historyTable.scrollRectToVisible(historyTable.getCellRect(i, 0, true)); // Scroll to row
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "No records found!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
 // Add this method to ManagerCheckHistory
    private void loadCheckHistory() {
        try {
            Connection conn = Databaseconnection.getConnection();
            String sql = "SELECT * FROM check_history ORDER BY check_out_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0); // Clear existing data

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("capacity"),
                    String.format("₱%.2f", rs.getDouble("price")),
                    rs.getString("check_in_date"),
                    rs.getString("check_out_date"),
                    rs.getInt("days_stayed"),
                    String.format("₱%.2f", rs.getDouble("total_amount"))
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load history", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerCheckHistory("Manager Name", "manager@example.com").setVisible(true));
    }
}