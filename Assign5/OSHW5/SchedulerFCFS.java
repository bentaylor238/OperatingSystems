import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Benjamin Taylor
 *
 * This class accepts processes and schedules them according to the First Come First Serve algorithm
 */
public class SchedulerFCFS extends SchedulerBase implements Scheduler {
    private Queue<Process> fcfsQueue = new LinkedList<>();
    private Platform platform;

    public SchedulerFCFS(Platform platform) {
        this.platform = platform;
    }

    // When a new process is added to the scheduler, add the process into the fcfs queue
    public void notifyNewProcess(Process p) {
        fcfsQueue.add(p);
    }

    /**
     * Updates the current running process by verifying there is one and if so, check if the burst and execution are
     * complete to know whether to replace the running process with a new one.
     *
     * @param cpu current running process
     * @return what should be the current running process
     */
    public Process update(Process cpu) {
        // If no process is running, start one
        if (cpu == null) {
            Process scheduleProcess = fcfsQueue.poll();
            platform.log(" Scheduled: " + scheduleProcess.getName());
            this.contextSwitches++;
            return scheduleProcess;
        }
        // Check if the burst is complete to know whether to replace the process with the next in the queue
        if (cpu.isBurstComplete()) {
            platform.log(" Process " + cpu.getName() + " burst complete");
            // If the execution is complete, then log it
            if (cpu.isExecutionComplete()) {
                platform.log(" Process " + cpu.getName() + " execution complete");
            }
            // if the execution is not complete, then re-add the process to the scheduler queue
            else {
                fcfsQueue.add(cpu);
            }
            // add a context switch for ending the burst/execution and then grab the next process to schedule
            this.contextSwitches++;
            Process scheduleProcess = fcfsQueue.poll();
            // if there is another process to schedule, then log the scheduling and add a context switch for it
            if (scheduleProcess != null) {
                platform.log(" Scheduled: " + scheduleProcess.getName());
                this.contextSwitches++;
            }
            return scheduleProcess;
        }
        // if the burst is not complete then continue running the current process
        else {
            return cpu;
        }
    }
}
