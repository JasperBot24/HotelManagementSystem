import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AdminDashboard extends JFrame {
	//Panels
	private JPanel mainPanel, sidebar, panel1, pane3, pane4, pane5;
	//Side Bar BUttons
	private JButton hom, ei, ci, el;
	//Tabs
    private JTabbedPane tabbedPane;
    //First Tab Contents
    private JLabel welcomeLabel;
    private JButton btn;
    ////// Include in 3rd
    private JTextField fullName, email, PhoneN, HomeAdd, EmpID, HotelPswd, confirmPassword,salaryField;
    String emails;
    private JLabel fulnm, eml, phneN, tp, HmAdd, EmID, Psd, confirmPsd;
    private JButton adempsub;
    private JComboBox etp, etyp;
    //Third Tab Contents
    DefaultTableModel model;
    private JTable emtable;
    private JScrollPane scrollPane;
    private JLabel lb2l; // labels3,
    private JTextField name3, email3, phone3, Add3, search3;// pass3;
    private JButton Search3, update3, del3;
    //4th Tab Contents
    DefaultTableModel model4;
    private JTable custable;
    private JScrollPane scrollPane4;
    private JLabel lb4l; //labels4,
    private JTextField search4; //name4, email4, phone4, Add4, pass4;
    private JButton Search4, del4; //update4,
    //5th Tab Contents
    DefaultTableModel model5;
    private JTable totable;
    private JScrollPane scrollPane5;
    private JLabel lbsl;
    private JTextField search5;
    private JButton Search5;//, view;
    //////////////////////////////////////////////
    //Global shit s
  	private int selectedRow = -1; // Store selected row for editing
  	private JButton saveEdit, cancelEdit;
    private JDialog editDialog;
    private JComboBox comboBox3, comboBox4, comboBox5;
    //////////////////////////////////////////////
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ////////////////////////////////////////////
    private String adminEmail; // Store the manager's email for database queries
    private String adminName; // Store the manager's name for UI updates
    private JLabel welcomelbl; // Make welcomeLabel an instance variable
    
    
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email is optional, so empty is valid
        }
        // Basic email regex pattern
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }
    
    
    
    ///////////////////////////////////////////
    public AdminDashboard(String adminName, String adminEmail) {
        setTitle("Admin Dashboard");
        setSize(1010, 600); // Width x Height
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        setVisible(true);

        // Exit confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                        AdminDashboard.this,
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

        /////////////////////////////////////////////////// SIDEBAR SECTION
        sidebar = new JPanel();
        sidebar.setBounds(0, 0, 230, 600); // Width increased a bit for sidebar (230)
        sidebar.setBackground(new Color(41, 57, 80)); // Dark blue-gray
        sidebar.setLayout(new GridLayout(5, 1, 15, 15));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        // Button styling
        Color activeColor = new Color(255, 204, 0); // Yellow
        Color hoverColor = new Color(70, 70, 70); // Hover
        Color defaultBg = new Color(45, 45, 45); // Sidebar bg
        Color defaultFg = Color.WHITE;
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JButton[] navButtons = new JButton[4];
        String[] labels = {"Home", "Employee Info", "Customer Info", "Earning Log"};

        for (int i = 0; i < labels.length; i++) {
            JButton btn = new JButton(labels[i]);
            btn.setFocusPainted(false);
            btn.setFont(buttonFont);
            btn.setForeground(defaultFg);
            btn.setBackground(defaultBg);
            btn.setBorderPainted(false);
            btn.setOpaque(true);

            int index = i;
            btn.addActionListener(e -> {
                tabbedPane.setSelectedIndex(index);
                for (JButton b : navButtons) {
                    b.setBackground(defaultBg);
                    b.setForeground(defaultFg);
                }
                btn.setBackground(activeColor);
                btn.setForeground(Color.BLACK);
            });

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!btn.getBackground().equals(activeColor)) {
                        btn.setBackground(hoverColor);
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!btn.getBackground().equals(activeColor)) {
                        btn.setBackground(defaultBg);
                    }
                }
            });

            navButtons[i] = btn;
            sidebar.add(btn);
        }

        navButtons[0].setBackground(activeColor);
        navButtons[0].setForeground(Color.BLACK);

        ////////////////////////////////////////////////////////// MAIN SECTION
        mainPanel = new JPanel();
        mainPanel.setBounds(230, 0, 770, 600); // Sidebar width (230) + Main width (770) = 1000
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        getContentPane().add(sidebar);
        getContentPane().add(mainPanel);

        ////////////////////////////////////////////////////////// TAB SECTION
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(-2, -25, 800, 700); // Fill mainPanel fully
        mainPanel.add(tabbedPane);
        ////////////////////////////////////////////////////////////////////////////////////////// 1st Tab - Home (Welcome Screen)
        panel1 = new JPanel() {
            private BufferedImage backgroundImage;

            {
                try {
                    // Try loading from resources first
                    backgroundImage = ImageIO.read(getClass().getResource("/backgroundhome1.jpg"));
                    
                    // If not found in resources, try file system
                    if (backgroundImage == null) {
                        backgroundImage = ImageIO.read(new File("backgroundhome1.jpg"));
                    }
                    
                    if (backgroundImage == null) {
                        System.err.println("Could not load background image");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    backgroundImage = null;
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback background if image fails to load
                    g.setColor(Color.GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel1.setLayout(null); 
        tabbedPane.addTab("Home", null, panel1, "Welcome Screen");
        
        /////////////////////////////////////////////////////////// Welcome Label
        welcomeLabel = new JLabel(
        	    "<html><center><font color='#fdfefe' size='5'>Hey Admin, Welcome to</font><br><font color='#fdfefe' size='6' ><b>Our Hotel Management System</b></font><br><font color='#fdfefe' size='5'>Admin Dashboard</font></center></html>",
        	    SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBounds(138, 166, 500, 100);
        panel1.add(welcomeLabel);
        ///////////////////////////////////////////////////////// Log Out Button 
        btn = new JButton("Log out");
        btn.setBackground(Color.RED);
        btn.setForeground(Color.WHITE);
        btn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to Log out?",
                        "Confirm Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    dispose(); 
                    new LoginGUI().setVisible(true);
                }
        	}
        });
        btn.setBounds(650, 490, 100, 35);
        panel1.add(btn);
        ////////////////////////////////////////////////////////////////////////////////////////// 3rd Tab - Employee Info
        pane3 = new JPanel();
        pane3.setLayout(null);
        tabbedPane.addTab("Employee Info", null, pane3, "Employee Information");
        lb2l = new JLabel("EMPLOYEE INFORMATION");
        lb2l.setBounds(325, 5, 150, 35);
        pane3.add(lb2l);
        //////////////////////////////////////////////////////////TABLE SECTION
        model = new DefaultTableModel(new String[]{"Employee ID", "Name", "Type", "Email", "Phone Number", "Address", "Monthly Salary"}, 0);
        emtable = new JTable(model) {
        	@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevents cell editing
            }
        	
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) {
                    ((JComponent)c).setToolTipText(getValueAt(row, column).toString());
                }
                return c;
            }
        };
        
        
        emtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Crucial for horizontal scrolling
        scrollPane = new JScrollPane(emtable);
        scrollPane.setBounds(10, 35, 755, 434);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pane3.add(scrollPane);
        
     // Set column widths (adjust as needed)
        emtable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        emtable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        emtable.getColumnModel().getColumn(2).setPreferredWidth(100); // Type
        emtable.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        emtable.getColumnModel().getColumn(4).setPreferredWidth(120); // Phone
        emtable.getColumnModel().getColumn(5).setPreferredWidth(200); // Address
        emtable.getColumnModel().getColumn(6).setPreferredWidth(100); // Salary
        
        try (Connection conn = Databaseconnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT employee_id, employee_fullname, employee_role, employee_email, "
                		+ "employee_phoneNumber, employee_address, default_salary FROM employees");
                ResultSet rs = pstmt.executeQuery()) {  
               while (rs.next()) {
                   int id = rs.getInt("employee_id");
                   String fullName = rs.getString("employee_fullname");
                   String email = rs.getString("employee_email");
                   String type = rs.getString("employee_role");
                   String phone = rs.getString("employee_phoneNumber");
                   String address = rs.getString("employee_address");
                   String dsalary = rs.getString("default_salary");

                   model.addRow(new Object[]{id, fullName, type, email, phone, address, dsalary});
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
		//////////////////////////////////////////////////////////SEARCH SECTION
		search3 = new JTextField();
		search3.setColumns(10);
		search3.setBounds(10, 487, 119, 28);
		pane3.add(search3);
		Search3 = new JButton("SEARCH");
		Search3.setBackground(Color.GRAY);
		Search3.setForeground(Color.WHITE);
		Search3.setBounds(216, 487, 89, 28);
		pane3.add(Search3);
		
        comboBox3 = new JComboBox();
        comboBox3.setModel(new DefaultComboBoxModel(new String[] {"Id", "Name", "Type"}));
        comboBox3.setBounds(139, 491, 67, 20);
        pane3.add(comboBox3);
		
		Search3.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String searchText = search3.getText().trim(); // Get search text
		        String searchCategory = comboBox3.getSelectedItem().toString(); // Get selected category

		        if (!searchText.isEmpty()) {
		            filterEmployee(searchText, searchCategory);
		        } else {
		        	resetEmtable();
		        }
		    }
		});
        //////////////////////////////////////////////////////////BUTTON SECTION
		adempsub = new JButton("New Employee");
		adempsub.setBackground(new Color(0, 153, 51));
		adempsub.setForeground(Color.WHITE);
		adempsub.setBounds(356, 487, 139, 28);
        pane3.add(adempsub);
        adempsub.addActionListener(e -> openEditDialog2());
		
        update3 = new JButton("Edit");
        update3.setBackground(new Color(255, 153, 0));
        update3.setForeground(Color.WHITE);
        update3.setBounds(520, 487, 105, 28);
        pane3.add(update3);
        update3.addActionListener(e -> openEditDialog());
        
        del3 = new JButton("Delete");
        del3.setBackground(new Color(204, 0, 0));
        del3.setForeground(Color.WHITE);
        del3.setBounds(650, 487, 105, 28);
        pane3.add(del3);
        del3.addActionListener(e -> deleteEmployee());
        setVisible(true);
        
        ////////////////////////////////////////////////////////////////////////////////////////// 4th Tab - Customer Info
        pane4 = new JPanel();
        pane4.setLayout(null);
        tabbedPane.addTab("Customer Info", null, pane4, "Customer Information");
        lb4l = new JLabel("CUSTOMER INFORMATION");
        lb4l.setBounds(325, 5, 150, 35);
        pane4.add(lb4l);
		////////////////////////////////////////////////////////////TABLE SECTION
        model4 = new DefaultTableModel(new String[]{"Customer ID", "Name", "Email", "Phone Number", "Address"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevents cell editing
            }
        };
        
        custable = new JTable(model4);
        
        
        custable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane4 = new JScrollPane(custable);
        scrollPane4.setBounds(10, 35, 755, 434);
        scrollPane4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pane4.add(scrollPane4);

     // Set column widths
        custable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        custable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        custable.getColumnModel().getColumn(2).setPreferredWidth(150); // Email
        custable.getColumnModel().getColumn(3).setPreferredWidth(150); // Phone
        custable.getColumnModel().getColumn(4).setPreferredWidth(250); // Address

        try (Connection conn = Databaseconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT customer_id, customer_fullname, customer_email, customer_phoneNumber, customer_address FROM customers");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("customer_id");
                String fullName = rs.getString("customer_fullname");
                String email = rs.getString("customer_email");
                String phone = rs.getString("customer_phoneNumber");
                String address = rs.getString("customer_address");

                model4.addRow(new Object[]{id, fullName, email, phone, address});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		//////////////////////////////////////////////////////////SEARCH SECTION
		search4 = new JTextField();
		search4.setColumns(10);
		search4.setBounds(10, 487, 119, 28);
		pane4.add(search4);
		
		Search4 = new JButton("SEARCH");
		Search4.setBackground(Color.GRAY);
		Search4.setForeground(Color.WHITE);
		Search4.setBounds(216, 487, 89, 28);
		pane4.add(Search4);
		
		comboBox4 = new JComboBox();
        comboBox4.setModel(new DefaultComboBoxModel(new String[] {"Id", "Name", "Type"}));
        comboBox4.setBounds(139, 491, 67, 20);
        pane4.add(comboBox4);
		
		Search4.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String searchText = search4.getText().trim(); // Get search text
		        String searchCategory = comboBox4.getSelectedItem().toString(); // Get selected category

		        if (!searchText.isEmpty()) {
		            filterCustomers(searchText, searchCategory);
		        } else {
		        	resetCustable();
		        }
		    }
		});
        //////////////////////////////////////////////////////////BUTTON SECTION
        del4 = new JButton("Delete");
        del4.setBackground(new Color(204, 0, 0));
        del4.setForeground(Color.WHITE);
        del4.setBounds(625, 487, 128, 28);
        pane4.add(del4);
        del4.addActionListener(e -> deleteCustomer());
        setVisible(true);
        
        
        ////////////////////////////////////////////////////////////////////////////////////////// 5th Tab - Total Earnings
//////////////////////////////////////////////////////////////////////////////////////////5th Tab - Total Earnings
pane5 = new JPanel();
pane5.setLayout(null);
tabbedPane.addTab("Earning Log", null, pane5, "Earning Log Report");
lbsl = new JLabel("EARNING LOG");
lbsl.setBounds(325, 5, 150, 35);
pane5.add(lbsl);

//Date range filter components
JLabel fromDateLabel = new JLabel("From:");
fromDateLabel.setBounds(10, 40, 40, 20);
pane5.add(fromDateLabel);

JTextField fromDateField = new JTextField();
fromDateField.setBounds(50, 40, 100, 20);
pane5.add(fromDateField);

JLabel toDateLabel = new JLabel("To:");
toDateLabel.setBounds(160, 40, 30, 20);
pane5.add(toDateLabel);

JTextField toDateField = new JTextField();
toDateField.setBounds(190, 40, 100, 20);
pane5.add(toDateField);

//Add filter options
JLabel filterLabel = new JLabel("Filter by:");
filterLabel.setBounds(300, 40, 50, 20);
pane5.add(filterLabel);

JComboBox<String> timeFilterCombo = new JComboBox<>(new String[]{"Custom Range", "Daily", "Weekly", "Monthly", "Yearly", "All"});
timeFilterCombo.setBounds(350, 40, 120, 20);
pane5.add(timeFilterCombo);

JButton applyFilterBtn = new JButton("Apply Filter");
applyFilterBtn.setBackground(new Color(255, 153, 0));
applyFilterBtn.setForeground(Color.WHITE);
applyFilterBtn.setBounds(480, 40, 120, 20);
pane5.add(applyFilterBtn);

//Summary label for selected rows
JLabel summaryLabel = new JLabel("Selected: 0 transactions | Total: ₱0.00");
summaryLabel.setBounds(10, 70, 400, 20);
pane5.add(summaryLabel);

//TABLE SECTION
model5 = new DefaultTableModel(new String[]{
"Room No.", "Room Type", "Customer", "Check In", 
"Check Out", "Price", "Days", "Total"
}, 0) {
	
	@Override
    public boolean isCellEditable(int row, int column) {
        return false; // Prevents cell editing
    }
	
@Override
public Class<?> getColumnClass(int columnIndex) {
// Make numeric columns right-aligned
return columnIndex >= 5 ? Double.class : String.class;
}
};

totable = new JTable(model5);
totable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
totable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
@Override
public Component getTableCellRendererComponent(JTable table, Object value,
boolean isSelected, boolean hasFocus, int row, int column) {
Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
if (value instanceof Double) {
setText(String.format("₱%,.2f", value));
setHorizontalAlignment(SwingConstants.RIGHT);
}
return c;
}
});

//Add selection listener to calculate selected rows total
totable.getSelectionModel().addListSelectionListener(e -> {
if (!e.getValueIsAdjusting()) {
int selectedCount = totable.getSelectedRowCount();
double total = 0.0;

int[] selectedRows = totable.getSelectedRows();
for (int row : selectedRows) {
Object value = model5.getValueAt(row, 7); // Total column
if (value instanceof Double) {
total += (Double) value;
} else if (value instanceof String) {
try {
total += Double.parseDouble(((String) value).replace("₱", "").replace(",", ""));
} catch (NumberFormatException ex) {
// Ignore if not a valid number
}
}
}

summaryLabel.setText(String.format("Selected: %d transactions | Total: ₱%,.2f", 
selectedCount, total));
}
});

scrollPane5 = new JScrollPane(totable);
scrollPane5.setBounds(10, 100, 755, 370);
scrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
pane5.add(scrollPane5);

//Set column widths
totable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Room No
totable.getColumnModel().getColumn(1).setPreferredWidth(100); // Room Type
totable.getColumnModel().getColumn(2).setPreferredWidth(150); // Customer
totable.getColumnModel().getColumn(3).setPreferredWidth(120); // Check In
totable.getColumnModel().getColumn(4).setPreferredWidth(120); // Check Out
totable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Price
totable.getColumnModel().getColumn(6).setPreferredWidth(50);  // Days
totable.getColumnModel().getColumn(7).setPreferredWidth(100); // Total

//Export button
JButton exportBtn = new JButton("Export to CSV");
exportBtn.setBounds(610, 40, 150, 20);
exportBtn.setBackground(new Color(0, 153, 51));
exportBtn.setForeground(Color.WHITE);
pane5.add(exportBtn);
exportBtn.addActionListener(e -> exportToCSV());

//Load initial data
loadEarningData(null);

//Apply filter button action
applyFilterBtn.addActionListener(e -> {
 String filterType = (String) timeFilterCombo.getSelectedItem();
 
 if ("Custom Range".equals(filterType)) {
     // Validate that both dates are entered
     if (fromDateField.getText().isEmpty() || toDateField.getText().isEmpty()) {
         JOptionPane.showMessageDialog(pane5, "Please enter both start and end dates", 
             "Error", JOptionPane.ERROR_MESSAGE);
         return;
     }
     
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     sdf.setLenient(false); // Strict date parsing
     
     try {
         Date fromDate = sdf.parse(fromDateField.getText());
         Date toDate = sdf.parse(toDateField.getText());
         
         // Validate that from date is before or equal to to date
         if (fromDate.after(toDate)) {
             JOptionPane.showMessageDialog(pane5, 
                 "Start date must be before or equal to end date", 
                 "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
             return;
         }
         
         // Validate dates are not in the future
         Date today = new Date();
         if (fromDate.after(today) || toDate.after(today)) {
             JOptionPane.showMessageDialog(pane5, 
                 "Dates cannot be in the future", 
                 "Invalid Date", JOptionPane.ERROR_MESSAGE);
             return;
         }
         
         // Validate reasonable date range (e.g., not more than 5 years)
         long diffInMillies = Math.abs(toDate.getTime() - fromDate.getTime());
         long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
         
         if (diffInDays > (5 * 365)) { // 5 years
             int response = JOptionPane.showConfirmDialog(pane5, 
                 "The date range is very large (more than 5 years). Continue anyway?",
                 "Large Date Range", JOptionPane.YES_NO_OPTION);
             
             if (response != JOptionPane.YES_OPTION) {
                 return;
             }
         }
         
         // If all validations pass, load data
         loadEarningData(filterType, fromDateField.getText(), toDateField.getText());
         
     } catch (ParseException ex) {
         JOptionPane.showMessageDialog(pane5, 
             "Please enter dates in YYYY-MM-DD format (e.g., 2023-01-15)", 
             "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
         return;
     }
 } else {
     loadEarningData(filterType);
 }
});

//Add date format validation on focus loss
fromDateField.addFocusListener(new FocusAdapter() {
 @Override
 public void focusLost(FocusEvent e) {
     validateDateFormat(fromDateField);
 }
});

toDateField.addFocusListener(new FocusAdapter() {
 @Override
 public void focusLost(FocusEvent e) {
     validateDateFormat(toDateField);
 }
});

//Search section
search5 = new JTextField();
search5.setColumns(10);
search5.setBounds(10, 487, 119, 28);
pane5.add(search5);

Search5 = new JButton("SEARCH");
Search5.setBackground(Color.GRAY);
Search5.setForeground(Color.WHITE);
Search5.setBounds(216, 487, 89, 28);
pane5.add(Search5);

comboBox5 = new JComboBox();
comboBox5.setModel(new DefaultComboBoxModel(new String[] {"Room No.", "Type", "Customer"}));
comboBox5.setBounds(139, 491, 67, 20);
pane5.add(comboBox5);

Search5.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e) {
String searchText = search5.getText().trim();
String searchCategory = comboBox5.getSelectedItem().toString();
if (!searchText.isEmpty()) {
filterRoom(searchText, searchCategory);
} else {
resetRoom();
}
}
});

//Add today's date as default in date fields
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
fromDateField.setText(sdf.format(new Date(selectedRow, selectedRow, selectedRow)));
toDateField.setText(sdf.format(new Date(selectedRow, selectedRow, selectedRow)));
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void exitPrompt() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to EXIT?", "Exit Confirmation", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Close application
        }
    }
    
    
 // Add this method to generate employee IDs
    private String generateEmployeeId() {
        try (Connection conn = Databaseconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(employee_id AS UNSIGNED)) FROM employees")) {
            
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return String.valueOf(maxId + 1); // Returns single digit (no leading zeros)
            }
            return "1"; // Default first ID
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }

    // Add this method to validate phone numbers
    private boolean isValidPhone(String phone) {
        return phone.matches("^09\\d{9}$"); // Starts with 09 and 11 digits total
    }

    // Add this method to validate names (no numbers)
    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    // Add this method to validate passwords match
    private boolean passwordsMatch(String pass1, String pass2) {
        return pass1.equals(pass2);
    }
    
    
    
    
    
    // ADD EMPLOYEE SECTION
    @SuppressWarnings("unchecked")
    
    private void openEditDialog2() {
        editDialog = new JDialog(this, "Add Employee", true);
        editDialog.setSize(400, 550); // Adjusted size for better proportions
        editDialog.getContentPane().setLayout(null);
        editDialog.setLocationRelativeTo(this);
        
        // Constants for layout
        final int LABEL_WIDTH = 120;
        final int FIELD_WIDTH = 220;
        final int START_X = 20;
        final int START_Y = 20;
        final int ROW_HEIGHT = 40;
        
        // Row 1: Full Name
        fulnm = new JLabel("Full Name:");
        fulnm.setBounds(START_X, START_Y, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(fulnm);
        
        fullName = new JTextField();
        fullName.setBounds(START_X + LABEL_WIDTH + 10, START_Y, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(fullName);

        // Row 2: Email
        eml = new JLabel("Email:");
        eml.setBounds(START_X, START_Y + ROW_HEIGHT, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(eml);
        
        email = new JTextField();
        email.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(email);

        // Row 3: Phone
        phneN = new JLabel("Phone Number:");
        phneN.setBounds(START_X, START_Y + ROW_HEIGHT*2, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(phneN);
        
        PhoneN = new JTextField();
        PhoneN.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*2, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(PhoneN);
        
        // Row 4: Position Type
        tp = new JLabel("Position:");
        tp.setBounds(START_X, START_Y + ROW_HEIGHT*3, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(tp);

        etp = new JComboBox<>(new String[] {"Stock", "Maintenance", "Manager", "Admin"});
        etp.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*3, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(etp);

        // Row 5: Address
        HmAdd = new JLabel("Address:");
        HmAdd.setBounds(START_X, START_Y + ROW_HEIGHT*4, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(HmAdd);

        HomeAdd = new JTextField();
        HomeAdd.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*4, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(HomeAdd);

        // Row 6: Employee ID
        EmID = new JLabel("Employee ID:");
        EmID.setBounds(START_X, START_Y + ROW_HEIGHT*5, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(EmID);

        EmpID = new JTextField(generateEmployeeId());
        EmpID.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*5, FIELD_WIDTH, 25);
        EmpID.setEditable(false);
        editDialog.getContentPane().add(EmpID);

        // Row 7: Password (with toggle)
        Psd = new JLabel("Password:");
        Psd.setBounds(START_X, START_Y + ROW_HEIGHT*6, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(Psd);

        HotelPswd = new JPasswordField();
        HotelPswd.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*6, FIELD_WIDTH - 40, 25);
        
        // Create toggle button for password visibility
        JToggleButton togglePassword1 = createPasswordToggleButton();
        togglePassword1.addActionListener(e -> {
            if (togglePassword1.isSelected()) {
                ((JPasswordField) HotelPswd).setEchoChar((char) 0); // Show password
            } else {
                ((JPasswordField) HotelPswd).setEchoChar('•'); // Hide password
            }
        });
        
        // Create panel to hold password field and toggle button
        JPanel passwordPanel1 = new JPanel(new BorderLayout());
        passwordPanel1.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*6, FIELD_WIDTH, 25);
        passwordPanel1.add(HotelPswd, BorderLayout.CENTER);
        passwordPanel1.add(togglePassword1, BorderLayout.EAST);
        editDialog.getContentPane().add(passwordPanel1);
        
        // Row 8: Confirm Password (with toggle)
        confirmPsd = new JLabel("Confirm Password:");
        confirmPsd.setBounds(START_X, START_Y + ROW_HEIGHT*7, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(confirmPsd);

        confirmPassword = new JPasswordField();
        confirmPassword.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*7, FIELD_WIDTH - 40, 25);
        
        // Create toggle button for confirm password visibility
        JToggleButton togglePassword2 = createPasswordToggleButton();
        togglePassword2.addActionListener(e -> {
            if (togglePassword2.isSelected()) {
                ((JPasswordField) confirmPassword).setEchoChar((char) 0); // Show password
            } else {
                ((JPasswordField) confirmPassword).setEchoChar('•'); // Hide password
            }
        });
        
        // Create panel to hold confirm password field and toggle button
        JPanel passwordPanel2 = new JPanel(new BorderLayout());
        passwordPanel2.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*7, FIELD_WIDTH, 25);
        passwordPanel2.add(confirmPassword, BorderLayout.CENTER);
        passwordPanel2.add(togglePassword2, BorderLayout.EAST);
        editDialog.getContentPane().add(passwordPanel2);
        
        // Row 9: Salary
        JLabel salLabel = new JLabel("Monthly Salary:");
        salLabel.setBounds(START_X, START_Y + ROW_HEIGHT*8, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(salLabel);

        salaryField = new JTextField();
        salaryField.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*8, FIELD_WIDTH, 25);
        salaryField.setEditable(false);
        editDialog.getContentPane().add(salaryField);

        // Buttons Row
        saveEdit = new JButton("Save");
        saveEdit.setBounds(START_X + 50, START_Y + ROW_HEIGHT*9 + 10, 100, 30);
        editDialog.getContentPane().add(saveEdit);

        cancelEdit = new JButton("Cancel");
        cancelEdit.setBounds(START_X + LABEL_WIDTH + 60, START_Y + ROW_HEIGHT*9 + 10, 100, 30);
        editDialog.getContentPane().add(cancelEdit);

        // Auto-update salary based on position
        etp.addActionListener(e -> {
            String position = (String) etp.getSelectedItem();
            int salary = switch (position.toLowerCase()) {
                case "stock" -> 15000;
                case "maintenance" -> 20000;
                case "manager" -> 35000;
                case "admin" -> 40000;
                default -> 0;
            };
            salaryField.setText(String.valueOf(salary));
        });
        
        // Initialize salary
        salaryField.setText("15000"); // Default for first position
        
        // Event handlers
     // With this:
        saveEdit.addActionListener(e -> {
            // First validate all required fields are filled
            if (fullName.getText().isEmpty() || email.getText().isEmpty() || 
                PhoneN.getText().isEmpty() || HomeAdd.getText().isEmpty() ||
                ((JPasswordField) HotelPswd).getPassword().length == 0 || ((JPasswordField) confirmPassword).getPassword().length == 0) {
                
                JOptionPane.showMessageDialog(editDialog, 
                    "Please fill in all required fields!", 
                    "Incomplete Information", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check if passwords match
            if (!Arrays.equals(((JPasswordField) HotelPswd).getPassword(), ((JPasswordField) confirmPassword).getPassword())) {
                JOptionPane.showMessageDialog(editDialog, 
                    "Passwords do not match!", 
                    "Password Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create confirmation message with employee details
            String confirmationMessage = String.format(
                "<html><b>Please confirm employee details:</b><br><br>" +
                "<b>Name:</b> %s<br>" +
                "<b>Email:</b> %s<br>" +
                "<b>Phone:</b> %s<br>" +
                "<b>Position:</b> %s<br>" +
                "<b>Salary:</b> %s<br><br>" +
                "Are you sure you want to save this employee?",
                fullName.getText(),
                email.getText(),
                PhoneN.getText(),
                etp.getSelectedItem(),
                salaryField.getText()
            );
            
            // Show confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(
                editDialog,
                confirmationMessage,
                "Confirm Employee Details",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                AddEmployee(); // Only proceed with saving if user confirms
            }
        });
        cancelEdit.addActionListener(e -> editDialog.dispose());

        editDialog.setVisible(true);
    }
    
 // Helper method to create a consistent toggle button
    private JToggleButton createPasswordToggleButton() {
        JToggleButton toggleButton = new JToggleButton();
        
        try {
            // Try to load the eye icon (make sure the path matches your resources)
            ImageIcon eyeIcon = new ImageIcon(getClass().getResource("/icons8-eye50.png"));
            if (eyeIcon != null) {
                ImageIcon scaledIcon = new ImageIcon(eyeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                toggleButton.setIcon(scaledIcon);
            } else {
                toggleButton.setText("👁"); // Fallback if icon not found
            }
        } catch (Exception e) {
            System.err.println("Error loading eye icon: " + e.getMessage());
            toggleButton.setText("👁"); // Fallback if error occurs
        }
        
        toggleButton.setPreferredSize(new Dimension(40, 25));
        toggleButton.setFocusPainted(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder());
        toggleButton.setBackground(new Color(44, 62, 80)); // Dark blue background
        toggleButton.setForeground(Color.WHITE);
        
        // Change color when selected
        toggleButton.addItemListener(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setBackground(new Color(212, 180, 15)); // Yellow when selected
            } else {
                toggleButton.setBackground(new Color(44, 62, 80)); // Dark blue when unselected
            }
        });
        
        return toggleButton;
    }
    
    
 // Function to add employee to table
    private void AddEmployee() {
    	
    	 // Validate all fields
        if (fullName.getText().isEmpty() || !isValidName(fullName.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid name (no numbers)", 
                "Invalid Name", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add email validation
        if (!isValidEmail(email.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address or leave it empty", 
                "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidPhone(PhoneN.getText())) {
            JOptionPane.showMessageDialog(this, "Phone must start with 09 and have 11 digits", 
                "Invalid Phone", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String password = new String(((JPasswordField) HotelPswd).getPassword());
        String confirmPass = new String(((JPasswordField) confirmPassword).getPassword());
        if (!passwordsMatch(password, confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", 
                "Password Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        if (fullName.getText().isEmpty() || email.getText().isEmpty() || 
            PhoneN.getText().isEmpty() || HomeAdd.getText().isEmpty() || 
            EmpID.getText().isEmpty() || HotelPswd.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String employeeID = EmpID.getText().trim();
        // Validate Employee ID (Must be an Integer)
        String phoneNumber = PhoneN.getText().trim();
        try {
            Long.parseLong(phoneNumber); // Ensuring phone number is numeric
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Phone Number must be a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Get selected type from comboBox
        String selectedType = etp.getSelectedItem().toString();
        // Assign default salary based on Employee Type
        int defaultSalary;
        switch (selectedType.toLowerCase()) {
            case "stock":
                defaultSalary = 15000;
                break;
            case "maintenance":
                defaultSalary = 20000;
                break;
            case "manager":
                defaultSalary = 35000;
                break;
            case "admin":
                defaultSalary = 40000;
                break;
            default:
                defaultSalary = 0; // Fallback if no match
        }
        // Insert employee to database
        try (Connection conn = Databaseconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO employees "
                     + "(employee_id, employee_fullname, employee_phoneNumber, employee_email, "
                     + "employee_address, employee_password, employee_role, default_salary) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, employeeID); // Keeping as string to allow leading zeros
            pstmt.setString(2, fullName.getText());
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email.getText());
            pstmt.setString(5, HomeAdd.getText());
            pstmt.setString(6, HotelPswd.getText());
            pstmt.setString(7, selectedType);
            pstmt.setInt(8, defaultSalary);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Employee Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Add data to the JTable
                model.addRow(new Object[]{
                    employeeID, fullName.getText(), selectedType, email.getText(), phoneNumber, 
                    HomeAdd.getText(), defaultSalary
                });
                editDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
 // EDIT SECTION FOR EMPLOYEES
    private void openEditDialog() {
        selectedRow = emtable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create Dialog with consistent size
        editDialog = new JDialog(this, "Edit Employee", true);
        editDialog.setSize(450, 500);
        editDialog.getContentPane().setLayout(null);
        editDialog.setLocationRelativeTo(this);
        
        // Layout constants to match add dialog
        final int LABEL_WIDTH = 120;
        final int FIELD_WIDTH = 220;
        final int START_X = 20;
        final int START_Y = 20;
        final int ROW_HEIGHT = 40;
        
        // Row 1: Name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(START_X, START_Y, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(nameLabel);
        
        name3 = new JTextField((String) model.getValueAt(selectedRow, 1));
        name3.setBounds(START_X + LABEL_WIDTH + 10, START_Y, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(name3);

        // Row 2: Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(START_X, START_Y + ROW_HEIGHT, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(emailLabel);
        
        email3 = new JTextField((String) model.getValueAt(selectedRow, 3));
        email3.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(email3);

        // Row 3: Phone
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(START_X, START_Y + ROW_HEIGHT*2, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(phoneLabel);
        
        phone3 = new JTextField((String) model.getValueAt(selectedRow, 4));
        phone3.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*2, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(phone3);
        
        // Row 4: Position
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setBounds(START_X, START_Y + ROW_HEIGHT*3, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(positionLabel);

        etyp = new JComboBox<>(new String[] {"Stock", "Maintenance", "Manager", "Admin"});
        etyp.setSelectedItem(model.getValueAt(selectedRow, 2));
        etyp.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*3, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(etyp);
        
        // Row 5: Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(START_X, START_Y + ROW_HEIGHT*4, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(addressLabel);

        Add3 = new JTextField((String) model.getValueAt(selectedRow, 5));
        Add3.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*4, FIELD_WIDTH, 25);
        editDialog.getContentPane().add(Add3);

        // Row 6: Employee ID (read-only)
        JLabel idLabel = new JLabel("Employee ID:");
        idLabel.setBounds(START_X, START_Y + ROW_HEIGHT*5, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(idLabel);

        JTextField idField = new JTextField(model.getValueAt(selectedRow, 0).toString());
        idField.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*5, FIELD_WIDTH, 25);
        idField.setEditable(false);
        editDialog.getContentPane().add(idField);
        
        // Row 7: Salary
        JLabel salaryLabel = new JLabel("Monthly Salary:");
        salaryLabel.setBounds(START_X, START_Y + ROW_HEIGHT*6, LABEL_WIDTH, 25);
        editDialog.getContentPane().add(salaryLabel);

        salaryField = new JTextField(model.getValueAt(selectedRow, 6).toString());
        salaryField.setBounds(START_X + LABEL_WIDTH + 10, START_Y + ROW_HEIGHT*6, FIELD_WIDTH, 25);
        salaryField.setEditable(false);
        editDialog.getContentPane().add(salaryField);

        // Update salary when position changes
        etyp.addActionListener(e -> {
            String selectedType = (String) etyp.getSelectedItem();
            int newSalary = switch (selectedType.toLowerCase()) {
                case "stock" -> 15000;
                case "maintenance" -> 20000;
                case "manager" -> 35000;
                case "admin" -> 40000;
                default -> 0;
            };
            salaryField.setText(String.valueOf(newSalary));
        });

        // Buttons Row
        saveEdit = new JButton("Save");
        saveEdit.setBounds(START_X + 50, START_Y + ROW_HEIGHT*7 + 10, 100, 30);
        editDialog.getContentPane().add(saveEdit);

        cancelEdit = new JButton("Cancel");
        cancelEdit.setBounds(START_X + LABEL_WIDTH + 60, START_Y + ROW_HEIGHT*7 + 10, 100, 30);
        editDialog.getContentPane().add(cancelEdit);

        
     // Replace this line:
     // saveEdit.addActionListener(e -> updateEmployee());
        //for confirmation
     saveEdit.addActionListener(e -> {
         // First validate all required fields are filled
         if (name3.getText().isEmpty() || email3.getText().isEmpty() || 
             phone3.getText().isEmpty() || Add3.getText().isEmpty()) {
             
             JOptionPane.showMessageDialog(editDialog, 
                 "Please fill in all required fields!", 
                 "Incomplete Information", 
                 JOptionPane.WARNING_MESSAGE);
             return;
         }
         
         // Create confirmation message with employee details
         String confirmationMessage = String.format(
             "<html><b>Please confirm employee changes:</b><br><br>" +
             "<table>" +
             "<tr><td><b>Field</b></td><td><b>Old Value</b></td><td><b>New Value</b></td></tr>" +
             "<tr><td>Name:</td><td>%s</td><td>%s</td></tr>" +
             "<tr><td>Email:</td><td>%s</td><td>%s</td></tr>" +
             "<tr><td>Phone:</td><td>%s</td><td>%s</td></tr>" +
             "<tr><td>Position:</td><td>%s</td><td>%s</td></tr>" +
             "<tr><td>Address:</td><td>%s</td><td>%s</td></tr>" +
             "<tr><td>Salary:</td><td>%s</td><td>%s</td></tr>" +
             "</table><br>" +
             "Are you sure you want to save these changes?",
             model.getValueAt(selectedRow, 1), name3.getText(),
             model.getValueAt(selectedRow, 3), email3.getText(),
             model.getValueAt(selectedRow, 4), phone3.getText(),
             model.getValueAt(selectedRow, 2), etyp.getSelectedItem(),
             model.getValueAt(selectedRow, 5), Add3.getText(),
             model.getValueAt(selectedRow, 6), salaryField.getText()
         );
         
         // Show confirmation dialog
         int confirm = JOptionPane.showConfirmDialog(
             editDialog,
             confirmationMessage,
             "Confirm Employee Changes",
             JOptionPane.YES_NO_OPTION,
             JOptionPane.QUESTION_MESSAGE
         );
         
         if (confirm == JOptionPane.YES_OPTION) {
             updateEmployee(); // Only proceed with updating if user confirms
         }
     });
        cancelEdit.addActionListener(e -> editDialog.dispose());

        editDialog.setVisible(true);
    }

    private void updateEmployee() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate inputs
        if (!isValidName(name3.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid name (no numbers)", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
     // Add email validation
        if (!isValidEmail(email3.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address or leave it empty", 
                "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidPhone(phone3.getText())) {
            JOptionPane.showMessageDialog(this, "Phone must start with 09 and have 11 digits", "Invalid Phone", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String employeeID = model.getValueAt(selectedRow, 0).toString();
        String updatedName = name3.getText();
        String updatedEmail = email3.getText();
        String updatedPhone = phone3.getText();
        String updatedAddress = Add3.getText();
        String updatedRole = etyp.getSelectedItem().toString();
        int updatedSalary = Integer.parseInt(salaryField.getText());

        // Database Update Query
        String updateQuery = "UPDATE employees SET employee_fullname = ?, employee_phoneNumber = ?, "
                + "employee_email = ?, employee_address = ?, employee_role = ?, default_salary = ? "
                + "WHERE employee_id = ?";
        
        try (Connection conn = Databaseconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            
            pstmt.setString(1, updatedName);
            pstmt.setString(2, updatedPhone);
            pstmt.setString(3, updatedEmail);
            pstmt.setString(4, updatedAddress);
            pstmt.setString(5, updatedRole);
            pstmt.setInt(6, updatedSalary);
            pstmt.setString(7, employeeID);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Employee Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Update JTable 
                model.setValueAt(updatedName, selectedRow, 1);
                model.setValueAt(updatedRole, selectedRow, 2);
                model.setValueAt(updatedEmail, selectedRow, 3);
                model.setValueAt(updatedPhone, selectedRow, 4);
                model.setValueAt(updatedAddress, selectedRow, 5);
                model.setValueAt(updatedSalary, selectedRow, 6);
                editDialog.dispose();
                
             // Refresh the table to show changes
                loadEmployeeData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
 // Add this method to refresh employee data
    private void loadEmployeeData() {
        try (Connection conn = Databaseconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT employee_id, employee_fullname, employee_role, employee_email, " +
                 "employee_phoneNumber, employee_address, default_salary FROM employees");
             ResultSet rs = pstmt.executeQuery()) {
            
            model.setRowCount(0); // Clear existing data
            while (rs.next()) {
                String id = rs.getString("employee_id");
                String fullName = rs.getString("employee_fullname");
                String email = rs.getString("employee_email");
                String type = rs.getString("employee_role");
                String phone = rs.getString("employee_phoneNumber");
                String address = rs.getString("employee_address");
                String salary = rs.getString("default_salary");

                model.addRow(new Object[]{id, fullName, type, email, phone, address, salary});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //// for DELETING EMPLOYEES FUNCTION
    private void deleteEmployee() {
        int selectedRow = emtable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String employeeID = model.getValueAt(selectedRow, 0).toString();
        System.out.println("Deleting employee with ID: " + employeeID);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return; // If "No" is selected, to do nothing
        }
        // Delete 
        String deleteQuery = "DELETE FROM employees WHERE employee_id = ?";
        try (Connection conn = Databaseconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, employeeID);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                model.removeRow(selectedRow);// Remove JTable
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee. No rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    ////for dELETING CUSTOMER FUNCTION
    private void deleteCustomer() {
        int selectedRow = custable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String customerID = model4.getValueAt(selectedRow, 0).toString();
        System.out.println("Attempting to delete customer with ID: " + customerID);
        
        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this customer?\nThis will also delete all related reservations.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // First delete related reservations
        try (Connection conn = Databaseconnection.getConnection()) {
            // Disable auto-commit to handle transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete from check_history
                try (PreparedStatement pstmt1 = conn.prepareStatement(
                    "DELETE FROM check_history WHERE customer_id = ?")) {
                    pstmt1.setString(1, customerID);
                    pstmt1.executeUpdate();
                }
                
                // Delete from checkouts
                try (PreparedStatement pstmt2 = conn.prepareStatement(
                    "DELETE FROM checkouts WHERE customer_id = ?")) {
                    pstmt2.setString(1, customerID);
                    pstmt2.executeUpdate();
                }
                
                // Delete from check_ins
                try (PreparedStatement pstmt3 = conn.prepareStatement(
                    "DELETE FROM check_ins WHERE customer_id = ?")) {
                    pstmt3.setString(1, customerID);
                    pstmt3.executeUpdate();
                }
                
                // Delete from reservations
                try (PreparedStatement pstmt4 = conn.prepareStatement(
                    "DELETE FROM reservations WHERE customer_id = ?")) {
                    pstmt4.setString(1, customerID);
                    pstmt4.executeUpdate();
                }
                
                // Finally delete the customer
                try (PreparedStatement pstmt5 = conn.prepareStatement(
                    "DELETE FROM customers WHERE customer_id = ?")) {
                    pstmt5.setString(1, customerID);
                    int rowsDeleted = pstmt5.executeUpdate();
                    
                    if (rowsDeleted > 0) {
                        conn.commit(); // Commit the transaction
                        JOptionPane.showMessageDialog(this, 
                            "Customer and all related data deleted successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        model4.removeRow(selectedRow);
                        
                        // Refresh the table
                        loadCustomerData();
                    } else {
                        conn.rollback(); // Rollback if no rows affected
                        JOptionPane.showMessageDialog(this, 
                            "Failed to delete customer. Customer not found.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Connection error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //load CUSTOMERS DATA
    
    private void loadCustomerData() {
        try (Connection conn = Databaseconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT customer_id, customer_fullname, customer_email, " +
                 "customer_phoneNumber, customer_address FROM customers");
             ResultSet rs = pstmt.executeQuery()) {
            
            model4.setRowCount(0); // Clear existing data
            while (rs.next()) {
                String id = rs.getString("customer_id");
                String fullName = rs.getString("customer_fullname");
                String email = rs.getString("customer_email");
                String phone = rs.getString("customer_phoneNumber");
                String address = rs.getString("customer_address");

                model4.addRow(new Object[]{id, fullName, email, phone, address});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading customer data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    
    
     /////////////////// filter Function For EMPLOYEES
    public void filterEmployee(String searchText, String searchCategory) {
        DefaultTableModel model = (DefaultTableModel) emtable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        emtable.setRowSorter(sorter);
        int columnIndex = 0;
        switch (searchCategory) {
            case "Name":
                columnIndex = 1;
                break;
            case "Type":
                columnIndex = 2;
                break;
            default:
                columnIndex = 0;
                break;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, columnIndex));
    }
    public void resetEmtable() {
        DefaultTableModel model = (DefaultTableModel) emtable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        emtable.setRowSorter(sorter);
        sorter.setRowFilter(null); // Reset filter (show all rows)
    }
	/////////////////// filter Function for CUSTOMERS
	public void filterCustomers(String searchText, String searchCategory) {
		DefaultTableModel model = (DefaultTableModel) custable.getModel();
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		custable.setRowSorter(sorter);
		
		int columnIndex = 0;
		switch (searchCategory) {
			case "Name":
			  columnIndex = 1;
			  break;
			case "Type":
			  columnIndex = 2;
			  break;
			default: // "Id"
			  columnIndex = 0;
			  break;
		}
		sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, columnIndex));
	}
	public void resetCustable() {
		DefaultTableModel model = (DefaultTableModel) custable.getModel();
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		custable.setRowSorter(sorter);
		sorter.setRowFilter(null); // Reset filter (show all rows)
	}
	/////////////////// filter Function for TOTAL LOG
	public void filterRoom(String searchText, String searchCategory) {
		DefaultTableModel model = (DefaultTableModel) totable.getModel();
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		totable.setRowSorter(sorter);
		
		int columnIndex = 0;
		switch (searchCategory) {
			case "Type":
			columnIndex = 1;
			break;
			case "Customer":
			columnIndex = 2;
			break;
			default: // "Room No."
			columnIndex = 0;
			break;
		}
		sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, columnIndex));
	}
	public void resetRoom() {
		DefaultTableModel model = (DefaultTableModel) totable.getModel();
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		totable.setRowSorter(sorter);
		sorter.setRowFilter(null); // Reset filter (show all rows)
	}
	
	private void exportToCSV() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Save as CSV");
	    fileChooser.setSelectedFile(new File("earnings_report_" + new SimpleDateFormat("yyyyMMdd").format(new Date(selectedRow, selectedRow, selectedRow)) + ".csv"));
	    
	    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	        File file = fileChooser.getSelectedFile();
	        if (!file.getName().toLowerCase().endsWith(".csv")) {
	            file = new File(file.getAbsolutePath() + ".csv");
	        }
	        
	        try (PrintWriter pw = new PrintWriter(file)) {
	            // Write headers
	            for (int i = 0; i < model5.getColumnCount(); i++) {
	                pw.print(model5.getColumnName(i));
	                if (i < model5.getColumnCount() - 1) {
	                    pw.print(",");
	                }
	            }
	            pw.println();
	            
	            // Write data
	            for (int row = 0; row < model5.getRowCount(); row++) {
	                for (int col = 0; col < model5.getColumnCount(); col++) {
	                    Object value = model5.getValueAt(row, col);
	                    String output = (value == null) ? "" : value.toString().replaceAll("\"", "\"\"");
	                    if (value instanceof String) {
	                        output = "\"" + output + "\"";
	                    }
	                    pw.print(output);
	                    if (col < model5.getColumnCount() - 1) {
	                        pw.print(",");
	                    }
	                }
	                pw.println();
	            }
	            
	            JOptionPane.showMessageDialog(this, "Data exported successfully to:\n" + file.getAbsolutePath(),
	                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
	        } catch (FileNotFoundException e) {
	            JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage(),
	                "Export Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	private void loadEarningData(String filterType, String... dateRange) {
	    String query = "SELECT room_number, room_type, customer_name, " +
	                  "DATE_FORMAT(check_in_date, '%Y-%m-%d') as check_in_date, " +
	                  "DATE_FORMAT(check_out_date, '%Y-%m-%d') as check_out_date, " +
	                  "price, days_stayed, total_amount FROM check_history";
	    
	    // Add WHERE clause based on filter type
	    if (filterType != null && !filterType.equals("All")) {
	        switch (filterType) {
	            case "Daily":
	                query += " WHERE DATE(check_out_date) = CURDATE()";
	                break;
	            case "Weekly":
	                query += " WHERE YEARWEEK(check_out_date) = YEARWEEK(CURDATE())";
	                break;
	            case "Monthly":
	                query += " WHERE MONTH(check_out_date) = MONTH(CURDATE()) " +
	                         "AND YEAR(check_out_date) = YEAR(CURDATE())";
	                break;
	            case "Yearly":
	                query += " WHERE YEAR(check_out_date) = YEAR(CURDATE())";
	                break;
	            case "Custom Range":
	                if (dateRange.length == 2) {
	                    query += " WHERE DATE(check_out_date) BETWEEN ? AND ?";
	                }
	                break;
	        }
	    }
	    
	    // Add sorting by check_out_date descending
	    query += " ORDER BY check_out_date DESC";
	    
	    try (Connection conn = Databaseconnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        
	        if (filterType != null && filterType.equals("Custom Range") && dateRange.length == 2) {
	            pstmt.setString(1, dateRange[0]);
	            pstmt.setString(2, dateRange[1]);
	        }
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            model5.setRowCount(0); // Clear existing data
	            
	            while (rs.next()) {
	                String rmno = rs.getString("room_number");
	                String rtyp = rs.getString("room_type");
	                String name = rs.getString("customer_name");
	                String chin = rs.getString("check_in_date");
	                String chout = rs.getString("check_out_date");
	                double price = rs.getDouble("price");
	                int days = rs.getInt("days_stayed");
	                double total = rs.getDouble("total_amount");

	                model5.addRow(new Object[]{rmno, rtyp, name, chin, chout, price, days, total});
	            }
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, 
	            "Error loading earning data: " + e.getMessage(), 
	            "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	
	// Add this helper method to your class
	private boolean validateDateFormat(JTextField dateField) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    sdf.setLenient(false);
	    
	    try {
	        if (!dateField.getText().isEmpty()) {
	            sdf.parse(dateField.getText());
	        }
	        dateField.setBackground(Color.WHITE);
	        return true;
	    } catch (ParseException e) {
	        dateField.setBackground(new Color(255, 200, 200)); // Light red
	        return false;
	    }
	}
	
	// Home Panel with Background Image
    class HomePanel extends JPanel {
        private BufferedImage backgroundImage;

        public HomePanel() {
            setLayout(null); // Use absolute positioning
            try {
                // Load the background image
                backgroundImage = ImageIO.read(getClass().getResource("/backgroundhome1.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the background image scaled to fit the panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			new AdminDashboard("Admin Name", "admin@example.com");
		});
	}
}