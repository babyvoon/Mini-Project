import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static Map<String, String> userDatabase = new HashMap<>();
    private static Map<String, Map<String, Integer>> warehouseStock = new HashMap<>();
    private static StockManage stockManage = new StockManage();
    private static JPanel warehousePanel;

    public static void main(String[] args) {
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

        frame.add(userLabel);
        frame.add(userField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(new JLabel());
        frame.add(loginButton);

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
            JButton addWarehouseButton = new JButton("Add Warehouse");
            JButton removeWarehouseButton = new JButton("Remove Warehouse");
    
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
            JButton viewLogButton = new JButton("View Log");
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
            JButton warehouseButton = new JButton("View " + warehouse);
            warehouseButton.addActionListener(e -> openWarehouseView(warehouse, username));
            warehousePanel.add(warehouseButton);
        }

        warehousePanel.revalidate();
        warehousePanel.repaint();
    }

    private static void openWarehouseView(String warehouse, String username) {
        JFrame warehouseFrame = new JFrame(warehouse + " Inventory");
        warehouseFrame.setSize(400, 350);
        warehouseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        warehouseFrame.setLayout(new FlowLayout());

        JTextArea logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        updateLogArea(logArea, warehouse);
        warehouseFrame.add(new JScrollPane(logArea));

        if (username.equals("manager")) {  
            JTextField productField = new JTextField(10);
            JTextField quantityField = new JTextField(5);
            JButton addButton = new JButton("Add Product");
            JButton removeButton = new JButton("Remove Product");
            JButton editButton = new JButton("Edit Product");

            addButton.addActionListener(e -> {
                String product = productField.getText();
                if (product.isEmpty()) {
                    JOptionPane.showMessageDialog(warehouseFrame, "Please enter a product name.");
                    return;
                }
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    stockManage.addProduct(warehouse, product, quantity);
                    updateLogArea(logArea, warehouse);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(warehouseFrame, "Invalid number format.");
                }
            });

            removeButton.addActionListener(e -> {
                String product = productField.getText();
                stockManage.removeProduct(warehouse, product);
                updateLogArea(logArea, warehouse);
            });

            editButton.addActionListener(e -> {
                String product = productField.getText();
                if (stockManage.viewStock(warehouse).containsKey(product)) {
                    String newQuantityStr = JOptionPane.showInputDialog(warehouseFrame, "Enter new quantity for " + product + ":");
                    try {
                        int newQuantity = Integer.parseInt(newQuantityStr);
                        stockManage.setProductQuantity(warehouse, product, newQuantity);
                        updateLogArea(logArea, warehouse);
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

    private static void updateLogArea(JTextArea logArea, String warehouse) {
        StringBuilder logText = new StringBuilder(warehouse + " Inventory:\n");
        for (Map.Entry<String, Integer> entry : stockManage.viewStock(warehouse).entrySet()) {
            logText.append(entry.getKey()).append(" : ").append(entry.getValue()).append(" units\n");
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
    
    
    
}
