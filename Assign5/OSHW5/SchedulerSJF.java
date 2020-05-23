import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Benjamin Taylor
 *
 * The class schedules processes based on the burst times. Whichever process' burst time is smallest will be scheduled
 * once the current running process completes. The scheduler is non-preemptive.
 */
public class SchedulerSJF extends SchedulerBase implements Scheduler {
    private Platform platform;
    private PriorityQueue<Process> sjfQueue = new PriorityQueue<>(new ProcessComparator());

    public SchedulerSJF(Platform platform) {
        this.platform = platform;
    }

    // When a new process is given to the scheduler, add it to the priority queue
    public void notifyNewProcess(Process p) {
        sjfQueue.add(p);
    }

    /**
     * The update checks the current running process, if one is not running then start one. If there is a process
     * running, then check if the burst/execution is complete and if so then start a new process.
     *
     * @param cpu the current running process
     * @return what should be the current running process
     */
    public Process update(Process cpu) {
        // if no process is running, then start one
        if (cpu == null) {
            Process scheduleProcess = sjfQueue.poll();
            platform.log(" Scheduled: " + scheduleProcess.getName());
            this.contextSwitches++;
            return scheduleProcess;
        }
        // find if the current running process' burst time is complete
        if (cpu.isBurstComplete()) {
            platform.log(" Process " + cpu.getName() + " burst complete");
            if (cpu.isExecutionComplete()) {
                platform.log(" Process " + cpu.getName() + " execution complete");
            }
            // If the execution is not complete, then add it back into the priority queue to finish running later
            else {
                sjfQueue.add(cpu);
            }
            // add a context switch for finishing the burst/execution, then grab the next process and schedule it if one exists
            this.contextSwitches++;
            Process scheduleProcess = sjfQueue.poll();
            if (scheduleProcess != null) {
                platform.log(" Scheduled: " + scheduleProcess.getName());
                this.contextSwitches++;
            }
            return scheduleProcess;
        }
        // if the process has not completed its burst time then continue running it.
        else {
            return cpu;
        }
    }
}

/**
 * This class is used in the PriorityQueue to compare Processes. It compares the length of burst times to know which
 * process to place at the front of the priority queue
 */
class ProcessComparator implements Comparator<Process> {
    public int compare(Process p1, Process p2) {
        if (p1.getBurstTime() < p2.getBurstTime()) {
            return -1;
        }
        else if (p1.getBurstTime() > p2.getBurstTime()) {
            return 1;
        }
        return 0;
    }
}
