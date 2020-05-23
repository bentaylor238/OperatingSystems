import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Benjamin Taylor
 *
 * This class implements the FIFO page scheduling algorithm by using an array of boolean and a counter for the memory
 * frames to maintain the maxMemoryFrames.
 */
public class TaskFIFO implements Runnable {
    private int[] sequence;
    private int[] pageFaults;
    private int maxMemoryFrames;
    private boolean[] memoryFrames;

    // Save the given values needed and initialize a boolean array for the memory frames
    public TaskFIFO(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.pageFaults = pageFaults;
        this.memoryFrames = new boolean[maxPageReference+1];
        for (int i = 0; i < this.memoryFrames.length; i++) {
            this.memoryFrames[i] = false;
        }
    }

    // Create a queue and keep track of the number of pages in memory and pagefaults. Then apply the FIFO algorithm
    // by checking if the next number in the sequence is true in the boolean array, and if not make true. If a number
    // is deleted from the queue, then set it to false in the array.
    @Override
    public void run() {
        int numberPageFaults = 0;
        int frameCount = 0;
        Queue<Integer> fifoQ = new LinkedList<>();
        for (int i = 0; i < sequence.length; i++) {
            if (!memoryFrames[sequence[i]]) {
                numberPageFaults++;
                if (frameCount == maxMemoryFrames) {
                    int removed = fifoQ.remove();
                    memoryFrames[removed] = false;
                    fifoQ.add(sequence[i]);
                    memoryFrames[sequence[i]] = true;
                } else {
                    memoryFrames[sequence[i]] = true;
                    fifoQ.add(sequence[i]);
                    frameCount++;
                }
            }
        }
        pageFaults[maxMemoryFrames] = numberPageFaults;
    }
}
