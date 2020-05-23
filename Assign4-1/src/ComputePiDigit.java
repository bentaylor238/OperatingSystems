import java.util.Optional;

/**
 * This is the class giving the threads what to do, it also contains teh shared resources
 *
 * @author Benjamin Taylor
 */
public class ComputePiDigit implements Runnable {
    private String threadName; // used for debugging
    private TaskQueue taskQueue;
    private ResultTable results;
    private Bpp bpp;

    /**
     * Pass in shared resources and save as private variables, the name was for debugging purposes, creates a single bpp
     * object for each thread
     * @param taskQueue shared TaskQueue
     * @param results shared results Table
     * @param name thread name which was used for debugging
     */
    public ComputePiDigit(TaskQueue taskQueue, ResultTable results, String name) {
        this.results = results;
        this.taskQueue = taskQueue;
        this.threadName = name;
        this.bpp = new Bpp();
    }

    // While the taskQueue does not return an empty Optional continue to grab tasks to compute the digit and place it in the result table
    public void run() {
        boolean done = false;
        while (!done) {
            Optional<Integer> digit = taskQueue.dequeue();
            if (digit.isPresent()) {
                int result = bpp.getDecimal(digit.get());
                results.put(digit.get(), result);
            }
            else {
                done = true;
            }
        }
    }
}
