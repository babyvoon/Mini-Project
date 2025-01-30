import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static Map<String, String> userDatabase = new HashMap<>();
    private static Map<String, Map<String, Integer>> warehouseStock = new HashMap<>();
    
    public static void main(String[] args) {
        // Mock users
        userDatabase.put("user", "user123");
        userDatabase.put("manager", "manager123");

        warehouseStock.put("Warehouse A", new HashMap<>());
        warehouseStock.put("Warehouse B", new HashMap<>());
        warehouseStock.put("Warehouse C", new HashMap<>());

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
        stockFrame.setSize(500, 300);
        stockFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        stockFrame.setLayout(new FlowLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        stockFrame.add(welcomeLabel);

        JButton warehouseAButton = new JButton("View Warehouse A");
        JButton warehouseBButton = new JButton("View Warehouse B");
        JButton warehouseCButton = new JButton("View Warehouse C");
        JButton logoutButton = new JButton("Log Out");

        warehouseAButton.addActionListener(e -> openWarehouseView("Warehouse A", username));
        warehouseBButton.addActionListener(e -> openWarehouseView("Warehouse B", username));
        warehouseCButton.addActionListener(e -> openWarehouseView("Warehouse C", username));
        logoutButton.addActionListener(e -> {
            stockFrame.dispose();
            main(null);
        });

        stockFrame.add(warehouseAButton);
        stockFrame.add(warehouseBButton);
        stockFrame.add(warehouseCButton);
        stockFrame.add(logoutButton);

        stockFrame.setVisible(true);
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
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                    warehouseStock.get(warehouse).put(product, warehouseStock.get(warehouse).getOrDefault(product, 0) + quantity);
                    updateLogArea(logArea, warehouse);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(warehouseFrame, "Please enter a valid number");
                }
            });

            removeButton.addActionListener(e -> {
                String product = productField.getText();
                warehouseStock.get(warehouse).remove(product);
                updateLogArea(logArea, warehouse);
            });

            editButton.addActionListener(e -> {
                String product = productField.getText();
                if (warehouseStock.get(warehouse).containsKey(product)) {
                    String newQuantityStr = JOptionPane.showInputDialog(warehouseFrame, "Enter new quantity for " + product + ":");
                    try {
                        int newQuantity = Integer.parseInt(newQuantityStr);
                        warehouseStock.get(warehouse).put(product, newQuantity);
                        updateLogArea(logArea, warehouse);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(warehouseFrame, "Invalid quantity");
                    }
                } else {
                    JOptionPane.showMessageDialog(warehouseFrame, "Product not found");
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
        for (Map.Entry<String, Integer> entry : warehouseStock.get(warehouse).entrySet()) {
            logText.append(entry.getKey()).append(" - ").append(entry.getValue()).append(" units\n");
        }
        logArea.setText(logText.toString());
    }
}