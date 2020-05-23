import java.util.HashMap;

/**
 * Maintains a hashmap through aggregation so the resource can be locked and only one thread will be able to access
 *@author Benjamin Taylor
 */
public class ResultTable {
    private HashMap<Integer, Integer> results = new HashMap<>();

    public synchronized void put(int digit, int number) {
        results.put(digit, number);
    }

    public synchronized int size() {
        return results.size();
    }

    public synchronized int get(int digit) {
        return results.get(digit);
    }
}
