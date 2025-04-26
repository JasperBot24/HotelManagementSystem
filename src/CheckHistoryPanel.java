import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.sql.*;

public class CheckHistoryPanel extends JPanel {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel placeholderLabel;
    private String customerId; // Store customer ID

    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80);
    private static final Color HOVER_COLOR = new Color(255, 223, 0);
    private static final Color ACTIVE_COLOR = new Color(212, 180, 15);
    private static final Color TEXT_COLOR = Color.GREEN;
    private static final Color HOVER_TEXT_COLOR = Color.BLACK;
    
    public CheckHistoryPanel(String customerId) {
        this.customerId = customerId;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 90, 150), 2, true));
        mainPanel.setPreferredSize(new Dimension(600, 400));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Search Field Container
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setPreferredSize(new Dimension(280, 35));
        searchContainer.setBackground(Color.WHITE);
        searchContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Search Icon
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/icons8-search-50.png"));
        searchIcon = resizeIcon(searchIcon, 25, 25);
        JLabel iconLabel = new JLabel(searchIcon);
        iconLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        // Search Field with Placeholder
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(200, 30));

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setBounds(0, 0, 200, 30);

        placeholderLabel = new JLabel("Search");
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholderLabel.setForeground(Color.GRAY);
        placeholderLabel.setBounds(5, 0, 200, 30);

        layeredPane.add(placeholderLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(searchField, JLayeredPane.DEFAULT_LAYER);

        searchContainer.add(iconLabel, BorderLayout.WEST);
        searchContainer.add(layeredPane, BorderLayout.CENTER);
        searchPanel.add(searchContainer);

        // Table Setup
        String[] columnNames = {"Room No.", "Room Type", "Check-in Date", "Check-out Date", "Days Stayed", "Total Price (₱)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);

        // Table Styling
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(60, 90, 150));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        historyTable.setRowHeight(25);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        historyTable.setBackground(new Color(44, 62, 80));
        historyTable.setForeground(Color.WHITE);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(44, 62, 80) : new Color(60, 90, 150));
                } else {}
                c.setForeground(Color.WHITE);
                return c;
            }
        };

        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Load real data from database
        loadHistoryFromDatabase();

        // Search Functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterTable(query);
                placeholderLabel.setVisible(searchField.getText().isEmpty());
            }
        });

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                placeholderLabel.setVisible(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    placeholderLabel.setVisible(true);
                }
            }
        });

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadHistoryFromDatabase() {
        tableModel.setRowCount(0); // Clear existing data
        System.out.println("Loading history for customer ID: " + customerId); // Debug
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = Databaseconnection.getConnection();
            System.out.println("Database connection established"); // Debug
            
            String query = "SELECT room_number, room_type, check_in_date, check_out_date, " +
                         "days_stayed, total_amount FROM check_history " +
                         "WHERE customer_id = ? ORDER BY check_out_date DESC";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, customerId);
            rs = pstmt.executeQuery();
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String roomNo = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                String checkIn = rs.getString("check_in_date");
                String checkOut = rs.getString("check_out_date");
                int daysStayed = rs.getInt("days_stayed");
                double totalAmount = rs.getDouble("total_amount");
                
                System.out.println("Found history record: " + roomNo + ", " + roomType); // Debug
                
                // Format dates if needed (remove time portion)
                checkIn = checkIn != null ? checkIn.split(" ")[0] : "N/A";
                checkOut = checkOut != null ? checkOut.split(" ")[0] : "N/A";
                
                tableModel.addRow(new Object[]{
                    roomNo, 
                    roomType, 
                    checkIn, 
                    checkOut, 
                    daysStayed, 
                    String.format("₱%.2f", totalAmount)
                });
            }
            
            if (!hasData) {
                System.out.println("No history records found for customer"); // Debug
                JOptionPane.showMessageDialog(this, 
                    "No check-in history found for your account",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage()); // Debug
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to load check history: " + e.getMessage(),
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
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    private void filterTable(String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        historyTable.setRowSorter(sorter);

        if (query.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + query);
                sorter.setRowFilter(rowFilter);
            } catch (Exception e) {
                sorter.setRowFilter(null);
            }
        }
    }

    public static void main(String[] args) {
        // For testing with a sample customer ID
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Check History");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int option = JOptionPane.showConfirmDialog(
                            frame,
                            "Are you sure you want to exit?",
                            "Exit Confirmation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (option == JOptionPane.YES_OPTION) {
                        frame.dispose();
                    }
                }
            });
            
            frame.setSize(600, 500);
            frame.setLayout(new BorderLayout());

            // Use a test customer ID
            CheckHistoryPanel panel = new CheckHistoryPanel("CUST001");
            frame.add(panel, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}