import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RoomDetailsPanel extends JPanel {
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel placeholderLabel;

    private static final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Dark blue background
    private static final Color HOVER_COLOR = new Color(255, 223, 0); // Bright yellow for hover
    private static final Color ACTIVE_COLOR = new Color(212, 180, 15); // Yellow for active button
    private static final Color TEXT_COLOR = Color.WHITE; // Default text color
    private static final Color HOVER_TEXT_COLOR = Color.BLACK; // Text color on hover

    public RoomDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR); // Dark blue background

        // 🔹 Create Main Panel with Rounded Corners (Similar to Login Form)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR); // Dark blue background
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 90, 150), 2, true));
        mainPanel.setPreferredSize(new Dimension(600, 400));

        // 🔹 Create Styled Search Bar Panel (Centered)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR); // Dark blue background
        searchPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // 🔹 Create Search Field Container
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setPreferredSize(new Dimension(280, 35));
        searchContainer.setBackground(Color.WHITE);
        searchContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 🔹 Load and Resize Search Icon
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("/icons8-search-50.png"));
        searchIcon = resizeIcon(searchIcon, 25, 25);
        JLabel iconLabel = new JLabel(searchIcon);
        iconLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        // 🔹 Create Layered Pane for Search Field and Placeholder
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(200, 30));

        // 🔹 Search Field
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setBounds(0, 0, 200, 30);

        // 🔹 Placeholder Label (Overlaid on Search Field)
        placeholderLabel = new JLabel("Search");
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholderLabel.setForeground(Color.GRAY);
        placeholderLabel.setBounds(5, 0, 200, 30);

        layeredPane.add(placeholderLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(searchField, JLayeredPane.DEFAULT_LAYER);

        searchContainer.add(iconLabel, BorderLayout.WEST);
        searchContainer.add(layeredPane, BorderLayout.CENTER);
        searchPanel.add(searchContainer);

        // 🔹 Table Setup
        String[] columnNames = {"Room No.", "Room Type", "Capacity", "Price (₱)", "Room Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        roomTable = new JTable(tableModel);

        // 🔹 Table Header Styling
        JTableHeader header = roomTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(60, 90, 150)); // Dark blue background for header
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // 🔹 Table Row Styling
        roomTable.setRowHeight(25);
        roomTable.setFont(new Font("Arial", Font.PLAIN, 14));
        roomTable.setBackground(new Color(44, 62, 80)); // Dark blue background for table
        roomTable.setForeground(Color.WHITE);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER); // 🔹 Center-align text
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(44, 62, 80) : new Color(60, 90, 150)); // Alternating dark rows
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        };

        // 🔹 Apply Centered Cell Renderer to All Columns
        for (int i = 0; i < roomTable.getColumnCount(); i++) {
            roomTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // 🔹 Load Rooms from Database
        loadRoomsFromDatabase();

        // 🔹 Search Functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterTable(query);
                placeholderLabel.setVisible(searchField.getText().isEmpty()); // Show placeholder when empty
            }
        });

        // 🔹 Remove Placeholder When Clicking the Search Field
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

        // 🔹 Table in a Scroll Pane
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 🔹 Add Components to Panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the Main Panel to the Frame
        add(mainPanel, BorderLayout.CENTER);
    }

    // 🔹 Resize Icons
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    // 🔹 Add Room Data
    private void addRoom(String roomNo, String type, String capacity, String price, String status) {
        tableModel.addRow(new Object[]{roomNo, type, capacity, price, status});
    }

    // 🔹 Filter Table Based on Search Query
    private void filterTable(String query) {
        DefaultTableModel filteredModel = new DefaultTableModel(new String[]{"Room No.", "Room Type", "Capacity", "Price (₱)", "Room Status"}, 0);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String roomNo = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String roomType = tableModel.getValueAt(i, 1).toString().toLowerCase();
            String status = tableModel.getValueAt(i, 4).toString().toLowerCase();

            if (roomNo.contains(query) || roomType.contains(query) || status.contains(query)) {
                filteredModel.addRow(new Object[]{
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1),
                        tableModel.getValueAt(i, 2),
                        tableModel.getValueAt(i, 3),
                        tableModel.getValueAt(i, 4)
                });
            }
        }
        roomTable.setModel(filteredModel);
    }

    // 🔹 Load Rooms from Database
    private void loadRoomsFromDatabase() {
        tableModel.setRowCount(0); // Clear the table
        try (Connection conn = Databaseconnection.getConnection()) {
            String query = "SELECT room_number, room_type, capacity, price, status FROM rooms";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String roomNo = rs.getString("room_number");
                    String roomType = rs.getString("room_type");
                    String capacity = rs.getString("capacity");
                    String price = rs.getString("price");
                    String status = rs.getString("status");
                    tableModel.addRow(new Object[]{roomNo, roomType, capacity, price, status});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load rooms from database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔹 Main Method to Test the Panel
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Room Details");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500); // Adjusted for a larger view
            frame.setLayout(new BorderLayout());

            RoomDetailsPanel panel = new RoomDetailsPanel();
            frame.add(panel, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}