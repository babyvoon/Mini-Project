import java.util.ArrayList;
import java.util.List;

class StockAction {
    private List<String> log = new ArrayList<>();

    public void logAction(String action, String product) {
        String entry = action + ": " + product;
        log.add(entry);
        System.out.println(entry);
    }

    public List<String> showLog() {
        return log;
    }
}