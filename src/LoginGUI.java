import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginGUI extends JFrame {

    public LoginGUI() {
        // Create the main frame
        setTitle("Hotel Management System");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        
     // Exit confirmation when closing the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                        LoginGUI.this,
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
        
        
        
        
        // Set background color for the whole frame
        getContentPane().setBackground(new Color(44, 62, 80));

        // Main panel with CardLayout
        JPanel mainPanel = new JPanel(new CardLayout());

        // Title label
        JLabel titleLabel = new JLabel("HOTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(255, 215, 0));

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(44, 62, 80));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Login menu panel (Main Menu)
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.setBackground(new Color(245, 245, 220));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Load and add an icon
        ImageIcon icon = new ImageIcon("C:\\Users\\JOEL BOTICARIO\\eclipse-workspace\\HOTELMANAGEMENT\\src\\vector-hotel-icon-symbol-sign-removebg-preview.png");
        Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(iconLabel, gbc);

        // Create buttons
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(44, 62, 80));
        loginButton.setForeground(Color.WHITE);

        JButton registerGuestButton = new JButton("Register as Guest");
        registerGuestButton.setPreferredSize(new Dimension(160, 40));
        registerGuestButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerGuestButton.setBackground(new Color(212, 180, 15));
        registerGuestButton.setForeground(Color.BLACK);

        // Switch to Registration Form
        registerGuestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                titleLabel.setText("REGISTER FORM");
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "registrationPanel");
            }
        });

        // Switch to Login Form
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                titleLabel.setText("LOGIN FORM");
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "loginFormPanel");
            }
        });

        // Add buttons
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        loginPanel.add(registerGuestButton, gbc);

        // Create the Registration and Login panels
        JPanel registrationPanel = new RegistrationForm(mainPanel, titleLabel);
        JPanel loginFormPanel = new LoginForm(mainPanel, titleLabel);

        // Add panels to the main panel
        mainPanel.add(loginPanel, "mainMenu");
        mainPanel.add(registrationPanel, "registrationPanel");
        mainPanel.add(loginFormPanel, "loginFormPanel");

        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setVisible(true);
        });
    }
}