import java.util.HashMap;

import java.util.Map;

class StockManage {
    private Map<String, Integer> stock = new HashMap<>();
    private StockAction stockAction = new StockAction();

    public Map<String, Integer> viewStock() {
        return stock;
    }

    public void addProduct(String product, int quantity) {
        stock.put(product, stock.getOrDefault(product, 0) + quantity);
        stockAction.logAction("Added", product + " (" + quantity + ")");
    }

    public void removeProduct(String product) {
        if (stock.containsKey(product)) {
            stock.remove(product);
            stockAction.logAction("Removed", product);
        } else {
            System.out.println("Product not found in stock");
        }
    }

    public StockAction getStockAction() {
        return stockAction;
    }
}