/**
 * @author Benjamin Taylor
 *
 * This class computes the number of page faults for a given sequence of numbers, using a specified number of memory
 * frames and a known max number in the sequence by applying the MRU algorithm.
 */
public class TaskMRU implements Runnable {
    private int[] sequence;
    private int[] pageFaults;
    private int maxMemoryFrames;
    private boolean[] memoryFrames;

    // Save the data as member variables and create a boolean array with the page values initialized to false b/c they aren't
    // in the task yet
    public TaskMRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.pageFaults = pageFaults;
        this.memoryFrames = new boolean[maxPageReference+1];
        for (int i = 0; i < memoryFrames.length; i++) {
            memoryFrames[i] = false;
        }
    }

    // Add pages to the scheduler and if the scheduler's memory frames are full, then delete the most recently used
    // page by setting it to false and plugging in the new number (make it true).
    @Override
    public void run() {
        int mru = 0;
        int frameCounter = 0;
        int numberPageFaults = 0;
        for (int i = 0; i < sequence.length; i++) {
            if (!memoryFrames[sequence[i]]) {
                numberPageFaults++;
                if (frameCounter == maxMemoryFrames) {
                    memoryFrames[mru] = false;
                    memoryFrames[sequence[i]] = true;
                } else {
                    memoryFrames[sequence[i]] = true;
                    frameCounter++;
                }
            }
            mru = sequence[i];
        }
        pageFaults[maxMemoryFrames] = numberPageFaults;
    }
}
