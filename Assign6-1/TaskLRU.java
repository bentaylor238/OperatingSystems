import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Benjamin Taylor
 */
public class TaskLRU implements Runnable {
    private int[] sequence;
    private int[] pageFaults;
    private int maxMemoryFrames;

    // Save the given needed values as member variables
    public TaskLRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.pageFaults = pageFaults;
        this.maxMemoryFrames = maxMemoryFrames;
    }

    // Create a modified linkedhashmap that accepts values and orders them according to the lru algorithm. If the number
    // of pages in the map exceeds the number of frames, it will delete it automatically. Also, if the number is in the
    // map then get it to update it in the lru sequence.
    @Override
    public void run() {
        int numberPageFaults = 0;
        MyLinkedHashMap<Integer, Integer> pageTable = new MyLinkedHashMap<>(maxMemoryFrames, .75f, true, maxMemoryFrames);
        for (int i = 0; i < sequence.length; i++) {
            if (!pageTable.containsKey(sequence[i])) {
                numberPageFaults++;
                pageTable.put(sequence[i], sequence[i]);
            } else {
                pageTable.get(sequence[i]);
            }
        }
        pageFaults[maxMemoryFrames] = numberPageFaults;
    }
}

/**
 * Code from Baeldung, with a few modifications for th maxEntries variable so that it will delete values once the map
 * contains more than the given number of memory frames
 *
 * @param <K> for our purposes we just use the actual number in the sequence
 * @param <V> for our purposes we just use the actual number in the sequence
 */
class MyLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private int maxEntries;

    public MyLinkedHashMap(
            int initialCapacity, float loadFactor, boolean accessOrder, int maxEntries) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxEntries;
    }

}
