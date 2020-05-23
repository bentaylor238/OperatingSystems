import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Benjamin Taylor
 *
 * This class simulates the three page table algorithms (FIFO, LRU, MRU) by creating a sequence of 1000 numbers from
 * 1-250, doing this 1000 times, running the 3 algorithms 100 times each for memoryFrames from 1 to 100 and tallying
 * the number of page faults each time. After the simulation is complete, the algorithms are compared to find the smallest
 * number of page faults on each simulation and frame and then also Belady's Anomaly is found for each algorithm.
 */
public class Assign6 {
    private static final int MAX_PAGE_REFERENCE = 250;
    private static final int REFERENCE_SEQUENCE_LENGTH = 1000;
    private static final int MAX_MEMORY_FRAMES = 100;
    private static final int MAX_NUMBER_SIMULATIONS = 1000;

    // The simulation
    public static void main(String[] args) {
        // Thread pool and number of cpus for number of threads
        int numberOfCpus = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(numberOfCpus);
        // page fault arrays to maintain number of page faults for each simulation
        int[][] fifoPageFaults = new int[MAX_NUMBER_SIMULATIONS][MAX_MEMORY_FRAMES+1];
        int[][] lruPageFaults = new int[MAX_NUMBER_SIMULATIONS][MAX_MEMORY_FRAMES+1];
        int[][] mruPageFaults = new int[MAX_NUMBER_SIMULATIONS][MAX_MEMORY_FRAMES+1];
        //The beginning of the simulation
        long timeStart = System.currentTimeMillis();
        for (int i = 1; i <= MAX_NUMBER_SIMULATIONS; i++) {
            // Create a sequence of random numbers from 1 to 250
            int[] sequence = new int[REFERENCE_SEQUENCE_LENGTH];
            for (int k = 0; k < REFERENCE_SEQUENCE_LENGTH; k++) {
                sequence[k] = (int)(Math.random()*(MAX_PAGE_REFERENCE)) + 1;
            }
            // Simulate the 3 algorithms with memory frames from 1 to 100 on the given random array of 1000 numbers
            for (int k = 1; k <= MAX_MEMORY_FRAMES; k++) {
                Runnable fifo = new TaskFIFO(sequence, k, MAX_PAGE_REFERENCE, fifoPageFaults[i - 1]);
                Runnable lru = new TaskLRU(sequence, k, MAX_PAGE_REFERENCE, lruPageFaults[i-1]);
                Runnable mru = new TaskMRU(sequence, k, MAX_PAGE_REFERENCE, mruPageFaults[i-1]);
                threadPool.execute(fifo);
                threadPool.execute(lru);
                threadPool.execute(mru);
            }
        }
        // Wait for the thread pool to terminate
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        }
        catch (Exception ex) {
            System.out.println("Error in waiting for shutdown");
        }
        // report timing
        long timeEnd = System.currentTimeMillis();
        System.out.printf("Simulation took %d ms\n", timeEnd - timeStart);
        System.out.println();
        // Calculate the minimum number of page faults on each simulation between the three given algorithms and report the totals
        calculateMinPf(fifoPageFaults, lruPageFaults, mruPageFaults);
        // Find if there are any instances of belady's anomalies in the 3 algorithms
        findBeladyAnomalies("FIFO", fifoPageFaults);
        findBeladyAnomalies("LRU", lruPageFaults);
        findBeladyAnomalies("MRU", mruPageFaults);
        // Dean's test code calls
//        validateAlgorithms();
//        testLRU();
//        testMRU();
    }

    // This runs through all the simulations and compares the 3 algorithms to see which had the lowest page faults and
    // given 1 to the winner/winners. Then it reports the totals for each algorithm
    private static void calculateMinPf(int[][] fifoPageFaults, int[][] lruPageFaults, int[][] mruPageFaults) {
        int fifoMinPf = 0;
        int mruMinPf = 0;
        int lruMinPf = 0;
        for (int i = 0; i < MAX_NUMBER_SIMULATIONS; i++) {
            for (int k = 0; k < MAX_MEMORY_FRAMES; k++) {
                if (fifoPageFaults[i][k] <= mruPageFaults[i][k] && fifoPageFaults[i][k] <= lruPageFaults[i][k]) {
                    fifoMinPf++;
                }
                if (lruPageFaults[i][k] <= fifoPageFaults[i][k] && lruPageFaults[i][k] <= mruPageFaults[i][k]) {
                    lruMinPf++;
                }
                if (mruPageFaults[i][k] <= fifoPageFaults[i][k] && mruPageFaults[i][k] <= lruPageFaults[i][k]) {
                    mruMinPf++;
                }
            }
        }
        System.out.printf("FIFO min PF : %d\n", fifoMinPf);
        System.out.printf("LRU min PF  : %d\n", lruMinPf);
        System.out.printf("MRU min PF  : %d\n", mruMinPf);
        System.out.println();
    }

    // This goes through all 1000 simulation sequences to determine how many beladies occured and the max anomaly difference
    private static void findBeladyAnomalies(String report, int[][] pageFaults) {
        int belAnomalies = 0;
        int maxAnom = 0;
        System.out.printf("Belady's Anomaly Report for %s \n", report);
        for (int i = 0; i < MAX_NUMBER_SIMULATIONS; i++) {
            // how many beladies occured on a given sequence with memory frames 1 to 100
            int[] beladies = belady(pageFaults[i]);
            belAnomalies += beladies[0];
            if (beladies[1] > maxAnom) {
                maxAnom = beladies[1];
            }
        }
        System.out.printf("\t Anomaly detected %d times with a max difference of %d\n", belAnomalies, maxAnom);
        System.out.println();
    }

    // This runs through the simulations of page faults on a given sequence of numbers with ranging memory frames from
    // 1 to 100. It checks to see if belady's anomaly occurs, adds 1 if it does, and then checks to see if it is largest
    // anomaly found so far. It then returns the number of anomalies [0] and the largest anomaly [1]
    private static int[] belady(int[] pageFaults) {
        int[] anomaliesAndMax = new int[2];
        anomaliesAndMax[0] = 0; // number of anomalies
        anomaliesAndMax[1] = 0; // max anomaly
        for (int k = 2; k < pageFaults.length; k++) {
            // if belady's anomaly occurs, update the return values
            if (pageFaults[k] > pageFaults[k-1]) {
                System.out.printf("\tdetected - Previous %d : Current %d (%d)\n", pageFaults[k-1], pageFaults[k], pageFaults[k] - pageFaults[k-1]);
                anomaliesAndMax[0]++;
                if (pageFaults[k] - pageFaults[k-1] > anomaliesAndMax[1]) {
                    anomaliesAndMax[1] = pageFaults[k] - pageFaults[k-1];
                }
            }

        }
        return anomaliesAndMax;
    }

    // Dean's test code
    public static void testLRU() {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames index is 3

        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskLRU(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);

        // Replacement should be: 2, 1, 3, 1, 2
        // Page Faults should be 7
        (new TaskLRU(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);

        // Replacement should be: 1
        // Page Faults should be 4
        (new TaskLRU(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }

    //Dean's test code
    public static void testMRU() {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames index is 3

        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskMRU(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);

        // Replacement should be: 1, 2, 1, 3
        // Page Faults should be 6
        (new TaskMRU(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);

        // Replacement should be: 3
        // Page Faults should be 4
        (new TaskMRU(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }

    // Dean's test code
    public static void validateAlgorithms() {
        // These shadows the global constants of the same name.
        final int MAX_PAGE_REFERENCE = 25;
        final int MAX_MEMORY_FRAMES = 10;
        //
        // Sequence length: 200
        // Pages: [0, 25)
        int[] sequence = {20, 16, 9, 6, 21, 23, 21, 2, 24, 5, 15, 13, 5, 6, 3, 17, 16, 6, 18, 22, 22, 7, 12, 18, 12, 7, 8, 3, 22, 3, 20, 0, 13, 6, 8, 18, 14, 11, 20, 18, 2, 12, 9, 24, 7, 3, 9, 8, 24, 10, 2, 5, 9, 8, 4, 12, 20, 10, 5, 22, 17, 6, 3, 23, 6, 7, 6, 23, 14, 8, 5, 7, 0, 3, 7, 8, 24, 14, 7, 7, 21, 4, 19, 15, 20, 23, 21, 1, 21, 18, 1, 19, 9, 22, 17, 5, 11, 3, 19, 20, 6, 22, 9, 24, 21, 3, 14, 7, 11, 4, 12, 1, 23, 6, 14, 12, 21, 21, 11, 12, 21, 9, 21, 14, 0, 23, 7, 14, 7, 19, 11, 23, 22, 6, 20, 19, 14, 21, 9, 8, 19, 23, 19, 20, 24, 4, 20, 14, 9, 3, 24, 6, 23, 13, 13, 6, 23, 3, 19, 1, 11, 15, 24, 8, 1, 14, 3, 5, 6, 2, 18, 20, 0, 16, 16, 2, 15, 5, 18, 15, 12, 11, 20, 15, 7, 9, 24, 3, 20, 2, 19, 22, 11, 2, 0, 18, 11, 11, 16, 11};
        //
        // First entry in each of these arrays is 0, because we don't simulate 0 frames of memory
        final int[] expectedFIFO = {0, 194, 184, 174, 163, 155, 148, 140, 133, 120, 115};
        final int[] expectedLRU = {0, 194, 185, 173, 164, 155, 147, 140, 126, 117, 110};
        final int[] expectedMRU = {0, 194, 188, 179, 170, 167, 157, 152, 147, 136, 126};
        //
        // This array is used to store the page faults for each of the memory frame sizes
        int[] pageFaults = new int[MAX_MEMORY_FRAMES + 1];

        (new TaskFIFO(sequence, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 4, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 5, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 6, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 7, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 8, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 9, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 10, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.println(java.util.Arrays.toString(pageFaults));
        if (java.util.Arrays.equals(expectedFIFO, pageFaults)) {
            System.out.println("FIFO Passed");
        }
        else {
            System.out.println("FIFO Failed");
        }

        (new TaskLRU(sequence, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 4, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 5, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 6, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 7, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 8, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 9, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 10, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.println(java.util.Arrays.toString(pageFaults));
        if (java.util.Arrays.equals(expectedLRU, pageFaults)) {
            System.out.println("LRU Passed");
        }
        else {
            System.out.println("LRU Failed");
        }

        (new TaskMRU(sequence, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 4, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 5, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 6, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 7, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 8, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 9, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 10, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.println(java.util.Arrays.toString(pageFaults));
        if (java.util.Arrays.equals(expectedMRU, pageFaults)) {
            System.out.println("MRU Passed");
        }
        else {
            System.out.println("MRU Failed");
        }

    }
}
