import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class StockAction {
    private List<String> log = new ArrayList<>();
    private static final String LOG_FILE = "stock_log.txt";

    public void logAction(String action, String product) {
        String entry = action + " -> Warehouse: " + product;
        log.add(entry);
        System.out.println(entry);
        logToFile(entry);
    }

    public List<String> showLog() {
        return log;
    }

    private void logToFile(String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }
}
