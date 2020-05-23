import java.util.ArrayList;

/**
 * @author Benjamin Taylor A02021288
 *
 * This class runs our computations for calculating 1000 digits of pi after the decimal
 */
public class Assign4 {
    public static void main(String[] args) {
        // Create an array list, place 1000 digits in it, and then shuffle them
        ArrayList<Integer> thousand = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            thousand.add(i+1);
        }
        java.util.Collections.shuffle(thousand);

        // Initialize a new taskqueue to be a shared resource for threads, and then place the randomized 1000 digits in it
        TaskQueue taskQueue = new TaskQueue();
        for (int i = 0; i < 1000; i++) {
            taskQueue.enqueue(thousand.get(i));
        }

        //debugging purposes
//        System.out.printf("Number of cpus: %d\n", Runtime.getRuntime().availableProcessors());

        // Find the number of processors and initialize a result table to be a shared resource for threads
        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        ResultTable results = new ResultTable();
        try {
            // Create an array of threads the size of the number of processors and begin tracking the time
            Thread[] threads = new Thread[numberOfProcessors];
            long timeStart = System.currentTimeMillis();
            // Initialize each thread and pass it a runnable object to know what to do and also pass in the shared resources, then start the thread
            for (int i = 0; i < numberOfProcessors; i++) {
                threads[i] = new Thread(new ComputePiDigit(taskQueue, results, Integer.toString(i)));
                threads[i].start();
            }
            // Wait for the threads to finish and track when the processes complete
            for (Thread t : threads) {
                t.join();
            }
            long timeEnd = System.currentTimeMillis();
            //Prints out pi and the time it took
            System.out.flush();
            System.out.print("\n3.");
            for (int i = 0; i < 1000; i++) {
                System.out.print(results.get(i+1));
            }
            System.out.printf("\nPi computation took: %.2f sec\n", (timeEnd - timeStart) / 1000.0);
        }
        catch (Exception ex) {
            System.out.println("Something bad happened :(");
        }


    }
}
