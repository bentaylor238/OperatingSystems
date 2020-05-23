import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Benjamin Taylor
 *
 * The class acts as a Round Robin Scheduler by accepting a time quantum amount and giving each process that amount
 * of time and then re running through the processes until all have completed.
 */
public class SchedulerRR extends SchedulerBase implements Scheduler {
    private Platform platform;
    private int timeQuantum;
    private Queue<Process> rrQueue = new LinkedList<>();

    // Accepts a time quantum value to know how long each process should be allowed to run
    public SchedulerRR(Platform platform, int tq) {
        this.platform = platform;
        this.timeQuantum = tq;
    }

    // When a new process is added to the scheduler, add it into the queue
    public void notifyNewProcess(Process p) {
        this.rrQueue.add(p);
    }

    /**
     * Starts a process if one is not running.
     *
     * @param cpu the current running process
     * @return the next running process(often the current one)
     */
    public Process update(Process cpu) {
        // start a process if one is not running
        if (cpu == null) {
            Process scheduleProcess = rrQueue.poll();
            platform.log(" Scheduled: " + scheduleProcess.getName());
            this.contextSwitches++;
            return scheduleProcess;
        }
        // take the next process and hold onto it in case we need to schedule it
        Process nextScheduled = rrQueue.peek();
        // if the burst is complete re add the process to the queue, unless the execution is complete then start the next
        if (cpu.isBurstComplete()) {
            if (cpu.isExecutionComplete()) {
                platform.log(" Process " + cpu.getName() + " execution complete");
            }
            else {
                rrQueue.add(cpu);
            }
            this.contextSwitches++;
            rrQueue.poll();
            if (nextScheduled != null) {
                platform.log(" Scheduled: " + nextScheduled.getName());
                this.contextSwitches++;
            }
            return nextScheduled;
        }
        // if the burst is not complete, then check if the time quantum is complete. If time quantum complete, start next process
        else {
            if (cpu.getElapsedBurst() != 0 && cpu.getElapsedBurst() % this.timeQuantum == 0) {
                this.rrQueue.add(cpu);
                platform.log(" Time quantum complete for process " + cpu.getName());
                this.contextSwitches++;

                nextScheduled = rrQueue.poll();
                if (nextScheduled != null) {
                    platform.log(" Scheduled: " + nextScheduled.getName());
                    this.contextSwitches++;
                }
                return nextScheduled;
            }
            return cpu;
        }
    }
}
