/**
 * This is the class giving the threads what to do, it also contains teh shared resources
 *
 * @author Benjamin Taylor
 */
public class ComputePiDigit implements Runnable {
    private String threadName; // used for debugging
    private TaskQueue taskQueue;
    private ResultTable results;

    // Pass in shared resources and save as private variables, the name was for debugging purposes
    public ComputePiDigit(TaskQueue taskQueue, ResultTable results, String name) {
        this.results = results;
        this.taskQueue = taskQueue;
        this.threadName = name;
    }

    // While the taskQueue is not empty continue to grab tasks to compute the digit for and place in the result table
    public void run() {
        while (!taskQueue.isEmpty()) {
            int digit = taskQueue.dequeue();
            int result = new Bpp(digit).getDecimal(digit);
            results.put(digit, result);
        }
    }
}
