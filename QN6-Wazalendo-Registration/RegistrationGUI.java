import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationGUI extends JFrame {
    private JTextField memberIdField;
    private JTextField fullNameField;
    private JTextField ninField;
    private JTextField phoneField;
    private JTextField depositField;
    private JButton registerButton;
    private JButton clearButton;
    private JButton exitButton;
    
    public RegistrationGUI() {
       
        try {
            DatabaseConnection.createMembersTableIfNotExists();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
       
        setupGUI();
    }
    
    private void setupGUI() {
        setTitle("Wazalendo SACCO - Member Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Member ID:"), gbc);
        gbc.gridx = 1;
        memberIdField = new JTextField(20);
        add(memberIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("National ID (NIN):"), gbc);
        gbc.gridx = 1;
        ninField = new JTextField(20);
        add(ninField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Initial Deposit (UGX):"), gbc);
        gbc.gridx = 1;
        depositField = new JTextField(20);
        add(depositField, gbc);
        
        
        JPanel buttonPanel = new JPanel();
        registerButton = new JButton("Register");
        clearButton = new JButton("Clear");
        exitButton = new JButton("Exit");
        
        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        
       
        registerButton.addActionListener(e -> handleRegistration());
        clearButton.addActionListener(e -> clearFields());
        exitButton.addActionListener(e -> System.exit(0));
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void clearFields() {
        memberIdField.setText("");
        fullNameField.setText("");
        ninField.setText("");
        phoneField.setText("");
        depositField.setText("");
    }
    
    private void handleRegistration() {
       
        String memberId = memberIdField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String nin = ninField.getText().trim();
        String phone = phoneField.getText().trim();
        String depositStr = depositField.getText().trim();
        
       
        StringBuilder errors = new StringBuilder();
        
        if (memberId.isEmpty()) {
            errors.append("❌ Member ID is required\n");
        }
        if (fullName.isEmpty()) {
            errors.append("❌ Full Name is required\n");
        }
        if (nin.isEmpty()) {
            errors.append("❌ NIN is required\n");
        } else if (nin.length() != 14) {
            errors.append("❌ NIN must be exactly 14 characters\n");
        }
        if (phone.isEmpty()) {
            errors.append("❌ Phone Number is required\n");
        } else if (!phone.matches("\\d{10}")) {
            errors.append("❌ Phone Number must be exactly 10 digits\n");
        }
        if (depositStr.isEmpty()) {
            errors.append("❌ Initial Deposit is required\n");
        } else {
            try {
                double deposit = Double.parseDouble(depositStr);
                if (deposit <= 0) {
                    errors.append("❌ Initial Deposit must be a positive amount\n");
                }
            } catch (NumberFormatException e) {
                errors.append("❌ Initial Deposit must be a valid number\n");
            }
        }
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                errors.toString(), 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        try {
            double deposit = Double.parseDouble(depositStr);
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Members (member_id, full_name, nin, phone, initial_deposit) " +
                         "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, nin);
            pstmt.setString(4, phone);
            pstmt.setDouble(5, deposit);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Member registered successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
            
        } catch (SQLException e) {
            
            if (e.getMessage().contains("UNIQUE")) {
                JOptionPane.showMessageDialog(this, 
                    "❌ This NIN or Member ID already exists in the database.", 
                    "Duplicate Entry", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationGUI());
    }
}