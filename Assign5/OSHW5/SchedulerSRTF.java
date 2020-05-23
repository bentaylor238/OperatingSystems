import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Benjamin Taylor
 *
 * The class applies the Shortest Remaining Time First algorithm for scheduling processes. It schedules the process with
 * the shortest remaining burst time. The scheduler is preemptive.
 */
public class SchedulerSRTF extends SchedulerBase implements Scheduler {
    private Platform platform;
    private PriorityQueue<Process> srtfQueue = new PriorityQueue<>(new SRTFProcessComparator());

    public SchedulerSRTF(Platform platform) {
        this.platform = platform;
    }

    // When a process is added to the scheduler place it in the priority queue based on its remaining burst time.
    public void notifyNewProcess(Process p) {
        this.srtfQueue.add(p);
    }

    /**
     * Check if a process is running and start one if not. If a process is running, then verify it has the shortest
     * remaining burst. If it has the shortest remaining burst, then check if the burst/execution is complete to know
     * whether to start a new process.
     *
     * @param cpu the current running process
     * @return the process that should be running
     */
    public Process update(Process cpu) {
        // if no process is running, then start one
        if (cpu == null) {
            Process scheduleProcess = srtfQueue.poll();
            platform.log(" Scheduled: " + scheduleProcess.getName());
            this.contextSwitches++;
            return scheduleProcess;
        }
        // grab the next process in the queue but don't remove it yet, so we can check if it has a smaller remaining burst time
        Process nextScheduled = srtfQueue.peek();
        // if the next process has a smaller remaining burst, replace it as the running process and delete it from the queue
        if (nextScheduled != null && cpu.getRemainingBurst() > nextScheduled.getRemainingBurst()) {
            srtfQueue.poll();
            platform.log(" Preemptively removed: " + cpu.getName());
            this.contextSwitches++;
            srtfQueue.add(cpu);

            platform.log(" Scheduled: " + nextScheduled.getName());
            this.contextSwitches++;
            return nextScheduled;

        }
        // The current running process has the shortest remaining burst, so check if the burst/execution is complete
        // if it is complete, then start the next process. if the execution is not complete, readd the process to the queue
        if (cpu.isBurstComplete()) {
            platform.log(" Process " + cpu.getName() + " burst complete");
            if (cpu.isExecutionComplete()) {
                platform.log(" Process " + cpu.getName() + " execution complete");
            }
            else {
                srtfQueue.add(cpu);
            }
            this.contextSwitches++;
            srtfQueue.poll();
            if (nextScheduled != null) {
                platform.log(" Scheduled: " + nextScheduled.getName());
                this.contextSwitches++;
            }
            return nextScheduled;
        }
        // the burst is not complete, so continue running the shortest remaining burst timed process
        else {
            return cpu;
        }
    }
}

/**
 * This class compares processes in the priority queue to determine which process has the shorter remaining burst.
 */
class SRTFProcessComparator implements Comparator<Process> {
    public int compare(Process p1, Process p2) {
        if (p1.getRemainingBurst() < p2.getRemainingBurst()) {
            return -1;
        }
        else if (p1.getRemainingBurst() > p2.getRemainingBurst()) {
            return 1;
        }
        return 0;
    }
}