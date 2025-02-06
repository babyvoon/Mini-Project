import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static Map<String, String> userDatabase = new HashMap<>();
    private static final String USER_FILE = "user_data.txt";
    private static Map<String, Map<String, Integer>> warehouseStock = new HashMap<>();
    private static StockManage stockManage = new StockManage();
    private static JPanel warehousePanel;

    public static void main(String[] args) {
        loadUserData();  // โหลดบัญชีจากไฟล์
        userDatabase.put("user", "user123");       
        userDatabase.put("manager", "manager123"); 

        warehouseStock = stockManage.loadWarehouseData();

        JFrame frame = new JFrame("Login Page");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful");
                frame.dispose();
                openStockManagement(username);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password");
            }
        });
        
        registerButton.addActionListener(e -> {
            JFrame registerFrame = new JFrame("Register");
            registerFrame.setSize(300, 200);
            registerFrame.setLayout(new GridLayout(3, 2));

            JLabel newUserLabel = new JLabel("New Username:");
            JTextField newUserField = new JTextField();
            JLabel newPassLabel = new JLabel("New Password:");
            JPasswordField newPassField = new JPasswordField();
            JButton confirmRegister = new JButton("Register");

            confirmRegister.addActionListener(ev -> {
                String newUsername = newUserField.getText();
                String newPassword = new String(newPassField.getPassword());
            
                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(registerFrame, "Username and password cannot be empty.");
                    return;
                }
            
                if (userDatabase.containsKey(newUsername)) {
                    JOptionPane.showMessageDialog(registerFrame, "Username already exists.");
                } else {
                    userDatabase.put(newUsername, newPassword);
                    saveUserData();  // บันทึกข้อมูลลงไฟล์
                    JOptionPane.showMessageDialog(registerFrame, "Registration successful!");
                    registerFrame.dispose();
                }
            });

            registerFrame.add(newUserLabel);
            registerFrame.add(newUserField);
            registerFrame.add(newPassLabel);
            registerFrame.add(newPassField);
            registerFrame.add(new JLabel());
            registerFrame.add(confirmRegister);

            registerFrame.setVisible(true);
            });

            frame.add(userLabel);
            frame.add(userField);
            frame.add(passLabel);
            frame.add(passField);
            frame.add(registerButton);  // เพิ่มปุ่ม Register
            frame.add(loginButton);  // ปุ่ม Login

            frame.setVisible(true);
        }

    private static void openStockManagement(String username) {
        JFrame stockFrame = new JFrame("Stock Management System");
        stockFrame.setSize(600, 400);
        stockFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        stockFrame.setLayout(new BorderLayout());
    
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        stockFrame.add(welcomeLabel, BorderLayout.NORTH);
    
        warehousePanel = new JPanel();
        warehousePanel.setLayout(new FlowLayout());
        updateWarehouseButtons(stockFrame, username);
    
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
    
        if (username.equals("manager")) {  
            JButton addWarehouseButton = new JButton("✏️ Add Warehouse");
            JButton removeWarehouseButton = new JButton("❌ Remove Warehouse");
    
            addWarehouseButton.addActionListener(e -> {
                String newWarehouse = JOptionPane.showInputDialog(stockFrame, "Enter new warehouse name:");
                if (newWarehouse != null && !newWarehouse.trim().isEmpty() && !warehouseStock.containsKey(newWarehouse)) {
                    warehouseStock.put(newWarehouse, new HashMap<>());
                    stockManage.saveWarehouseData(warehouseStock);
                    updateWarehouseButtons(stockFrame, username);
                    JOptionPane.showMessageDialog(stockFrame, "Warehouse added successfully.");
                } else {
                    JOptionPane.showMessageDialog(stockFrame, "Invalid or duplicate warehouse name.");
                }
            });
    
            removeWarehouseButton.addActionListener(e -> {
                String warehouseToRemove = JOptionPane.showInputDialog(stockFrame, "Enter warehouse name to remove:");
                if (warehouseToRemove != null && warehouseStock.containsKey(warehouseToRemove)) {
                    warehouseStock.remove(warehouseToRemove);
                    stockManage.saveWarehouseData(warehouseStock);
                    updateWarehouseButtons(stockFrame, username);
                    JOptionPane.showMessageDialog(stockFrame, "Warehouse removed successfully.");
                } else {
                    JOptionPane.showMessageDialog(stockFrame, "Warehouse not found.");
                }
            });
    
            controlPanel.add(addWarehouseButton);
            controlPanel.add(removeWarehouseButton);
    
            // เพิ่มปุ่ม "View Log" เฉพาะ Manager
            JButton viewLogButton = new JButton("👁️ View Log");
            viewLogButton.addActionListener(e -> viewLog(username));
            controlPanel.add(viewLogButton);
        }
    
        JButton logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(e -> {
            stockFrame.dispose();
            main(null);
        });
    
        controlPanel.add(logoutButton);
        stockFrame.add(warehousePanel, BorderLayout.CENTER);
        stockFrame.add(controlPanel, BorderLayout.SOUTH);
    
        stockFrame.setVisible(true);
    }

    private static void updateWarehouseButtons(JFrame stockFrame, String username) {
        warehousePanel.removeAll();

        for (String warehouse : warehouseStock.keySet()) {
            JButton warehouseButton = new JButton("View: " + warehouse);
            warehouseButton.addActionListener(e -> openWarehouseView(warehouse, username));
            warehousePanel.add(warehouseButton);
        }

        warehousePanel.revalidate();
        warehousePanel.repaint();
    }

    private static void openWarehouseView(String warehouse, String username) {
        JFrame warehouseFrame = new JFrame(warehouse + "📦 Inventory");
        warehouseFrame.setSize(380, 350);
        warehouseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        warehouseFrame.setLayout(new FlowLayout());

        // 📌 ส่วนของ Search Bar
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        gbc.gridx = 0; gbc.gridy = 0; searchPanel.add(new JLabel("Search: "), gbc);
        gbc.gridx = 1; gbc.gridy = 0; searchPanel.add(searchField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; searchPanel.add(searchButton, gbc);

        JTextArea logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        updateLogArea(logArea, warehouse, "");
        warehouseFrame.add(new JScrollPane(logArea));

        // แสดงสินค้าทั้งหมดตั้งแต่เริ่มต้น
        updateLogArea(logArea, warehouse, "");

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            updateLogArea(logArea, warehouse, query);
        });

        
        warehouseFrame.add(new JLabel("Search Product: "));
        warehouseFrame.add(searchField);
        warehouseFrame.add(searchButton);
        

        JPanel buttonPanel = new JPanel(new FlowLayout());
        if (username.equals("manager")) {
            JTextField productField = new JTextField(10);
            JTextField quantityField = new JTextField(5);
            JButton addButton = new JButton("➕ Add");
            JButton removeButton = new JButton("❌ Remove");
            JButton editButton = new JButton("✏️ Edit");

            addButton.addActionListener(e -> {
                String product = productField.getText();
                if (product.isEmpty()) {
                    JOptionPane.showMessageDialog(warehouseFrame, "Please enter a product name.");
                    return;
                }
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    stockManage.addProduct(warehouse, product, quantity);
                    updateLogArea(logArea, warehouse, product);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(warehouseFrame, "Invalid number format.");
                }
            });

            removeButton.addActionListener(e -> {
                String product = productField.getText();
                stockManage.removeProduct(warehouse, product);
                updateLogArea(logArea, warehouse, product);
            });

            editButton.addActionListener(e -> {
                String product = productField.getText();
                if (stockManage.viewStock(warehouse).containsKey(product)) {
                    String newQuantityStr = JOptionPane.showInputDialog(warehouseFrame, "Enter new quantity for " + product + ":");
                    try {
                        int newQuantity = Integer.parseInt(newQuantityStr);
                        stockManage.setProductQuantity(warehouse, product, newQuantity);
                        updateLogArea(logArea, warehouse, newQuantityStr);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(warehouseFrame, "Invalid quantity input.");
                    }
                } else {
                    JOptionPane.showMessageDialog(warehouseFrame, "Product not found.");
                }
            });

            warehouseFrame.add(new JLabel("Product: "));
            warehouseFrame.add(productField);
            warehouseFrame.add(new JLabel("Quantity: "));
            warehouseFrame.add(quantityField);
            warehouseFrame.add(addButton);
            warehouseFrame.add(removeButton);
            warehouseFrame.add(editButton);
        }

        warehouseFrame.setVisible(true);
    }

    private static void updateLogArea(JTextArea logArea, String warehouse, String query) {
        StringBuilder logText = new StringBuilder("📦 " + warehouse + " Inventory:\n");
        for (Map.Entry<String, Integer> entry : stockManage.viewStock(warehouse).entrySet()) {
            if (query.isEmpty() || entry.getKey().toLowerCase().contains(query.toLowerCase())) {
                logText.append("🔹 ").append(entry.getKey()).append(" : ").append(entry.getValue()).append(" units\n");
            }
        }
        logArea.setText(logText.toString());
    }

    private static void viewLog(String username) {
        if (!username.equals("manager")) {
            JOptionPane.showMessageDialog(null, "Access Denied: You do not have permission to view the logs.");
            return;
        }
    
        JFrame logFrame = new JFrame("Stock Action Log");
        logFrame.setSize(400, 300);
        logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JTextArea logArea = new JTextArea(15, 30);
        logArea.setEditable(false);
    
        StringBuilder logText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("stock_log.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logText.append(line).append("\n");
            }
        } catch (IOException e) {
            logText.append("No logs found.");
        }
    
        logArea.setText(logText.toString());
        logFrame.add(new JScrollPane(logArea));
        logFrame.setVisible(true);
    }

        private static void loadUserData() {
            File file = new File(USER_FILE);
            if (!file.exists()) return;
        
            try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        userDatabase.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading user data: " + e.getMessage());
            }
        }
      
        private static void saveUserData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    
}