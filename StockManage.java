import java.io.*;
import java.util.HashMap;
import java.util.Map;

class StockManage {
    private Map<String, Map<String, Integer>> warehouseStock = new HashMap<>();
    private StockAction stockAction = new StockAction();
    private static final String FILE_NAME = "stock_data.txt";

    public StockManage() {
        loadWarehouseData();
    }

    public Map<String, Map<String, Integer>> loadWarehouseData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return warehouseStock;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String warehouse = parts[0];
                    String product = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    warehouseStock.putIfAbsent(warehouse, new HashMap<>());
                    warehouseStock.get(warehouse).put(product, quantity);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading stock data: " + e.getMessage());
        }
        return warehouseStock;
    }

    public void saveWarehouseData(Map<String, Map<String, Integer>> warehouseStock) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String warehouse : warehouseStock.keySet()) {
                for (Map.Entry<String, Integer> entry : warehouseStock.get(warehouse).entrySet()) {
                    writer.write(warehouse + "," + entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving stock data: " + e.getMessage());
        }
    }

    public void addProduct(String warehouse, String product, int quantity) {
        warehouseStock.putIfAbsent(warehouse, new HashMap<>());
        warehouseStock.get(warehouse).put(product, warehouseStock.get(warehouse).getOrDefault(product, 0) + quantity);

        stockAction.logAction("Added", warehouse + " - " + product + " (" + quantity + ")");
        saveWarehouseData(warehouseStock);
    }

    public void removeProduct(String warehouse, String product) {
        if (warehouseStock.containsKey(warehouse) && warehouseStock.get(warehouse).containsKey(product)) {
            warehouseStock.get(warehouse).remove(product);
            stockAction.logAction("Removed", warehouse + " - " + product);
            saveWarehouseData(warehouseStock);
        }
    }

    public void setProductQuantity(String warehouse, String product, int newQuantity) {
        if (warehouseStock.containsKey(warehouse) && warehouseStock.get(warehouse).containsKey(product)) {
            warehouseStock.get(warehouse).put(product, newQuantity);
            stockAction.logAction("Edited", warehouse + " - " + product + " (" + newQuantity + ")");
            saveWarehouseData(warehouseStock);
        }
    }

    public Map<String, Integer> viewStock(String warehouse) {
        return warehouseStock.getOrDefault(warehouse, new HashMap<>());
    }

    public StockAction getStockAction() {
        return stockAction;
    }
}
