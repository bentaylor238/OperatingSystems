import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Benjamin Taylor
 *
 * The class acts as a scheduler applying the Priority algorithm. The highest priority process is scheduled. It allows
 * for pre-emption.
 */
public class SchedulerPriority extends SchedulerBase implements Scheduler {
    private Platform platform;
    private PriorityQueue<Process> priorityQueue = new PriorityQueue<>(new PriorityComparator());


    public SchedulerPriority(Platform platform) {
        this.platform = platform;
    }

    // add a new process into our scheduling queue
    public void notifyNewProcess(Process p) {
        this.priorityQueue.add(p);
    }

    /**
     * Starts a process to begin and as the simulation occurs it schedules the highest priority process.
     *
     * @param cpu current running process
     * @return the process that should be running
     */
    public Process update(Process cpu) {
        // if no process is running, then start one
        if (cpu == null) {
            Process scheduleProcess = priorityQueue.poll();
            platform.log(" Scheduled: " + scheduleProcess.getName());
            this.contextSwitches++;
            return scheduleProcess;
        }
        // take the next process in the queue and compare it to the currently running to know if the current running is the highest priority
        // since a higher priority may have been added to the queue while the current was running. Allows for preemption
        Process nextScheduled = priorityQueue.peek();
        if (nextScheduled != null && cpu.getPriority() > nextScheduled.getPriority()) {
            priorityQueue.poll();
            platform.log(" Preemptively removed: " + cpu.getName());
            this.contextSwitches++;
            priorityQueue.add(cpu);

            platform.log(" Scheduled: " + nextScheduled.getName());
            this.contextSwitches++;
            return nextScheduled;
        }
        // check if the current running process' burst/execution is complete and schedulet the next one if so
        if (cpu.isBurstComplete()) {
            platform.log(" Process " + cpu.getName() + " burst complete");
            if (cpu.isExecutionComplete()) {
                platform.log(" Process " + cpu.getName() + " execution complete");
            }
            // if execution isn't complete then re add the current to the queue
            else {
                priorityQueue.add(cpu);
            }
            this.contextSwitches++;
            priorityQueue.poll();
            if (nextScheduled != null) {
                platform.log(" Scheduled: " + nextScheduled.getName());
                this.contextSwitches++;
            }
            return nextScheduled;
        }
        // if the burst is not complete, then keep running the current.
        else {
            return cpu;
        }
    }
}

/**
 * This class compares the processes priorities to put the lowest number (highest priority) at teh front of the queue
 */
class PriorityComparator implements Comparator<Process> {
    public int compare(Process p1, Process p2) {
        if (p1.getPriority() < p2.getPriority()) {
            return -1;
        }
        else if (p1.getPriority() > p2.getPriority()) {
            return 1;
        }
        return 0;
    }
}