import java.util.HashMap;

/**
 * Maintains a hashmap through aggregation so the resource can be locked and only one thread will be able to access
 *@author Benjamin Taylor
 */
public class ResultTable {
    private HashMap<Integer, Integer> results = new HashMap<>();

    /**
     * Places a key value pair into the table with the digit place as the key and the actual value for the nth digit of
     * pi as the value. It then updates the progress of computation to the user.
     *
     * @param digit the nth digit computed for pi
     * @param number the value of the the nth digit of pi
     */
    public synchronized void put(int digit, int number) {
        results.put(digit, number);

        int size = size();
        if (size % 10 == 0 && size != 0) {
            System.out.print(".");
        }
        if (size % 200 == 0 && size != 0) {
            System.out.println();
        }
    }

    public synchronized int size() {
        return results.size();
    }

    public synchronized int get(int digit) {
        return results.get(digit);
    }
}
