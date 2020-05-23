import java.util.LinkedList;

/**
 * Contains a linked list queue through aggregation so only one thread can access the certain methods at a time
 * @author Benjamin Taylor
 */
public class TaskQueue {
    private LinkedList<Integer> taskQueue = new LinkedList();

    // May not necessarily need to be synchronized since only a single thread accesses it, but adds tasks to the queue
    public synchronized void enqueue(int digit) {
        taskQueue.add(digit);
    }

    /**
     * takes a task from the queue, then I check the size to know whether to update the status. This method is synchronized
     * so no other thread will be able to create a race condition while updating the status or while dequeueing.
     * @return a task, or the digit to compute for pi
     */
    public synchronized int dequeue() {
        int digit = taskQueue.remove();
        int size = size();
        if (size % 10 == 0 && size != 1000) {
            System.out.print(".");
        }
        if (size % 200 == 0 && size != 1000) {
            System.out.println();
        }
        return digit;
    }

    public synchronized boolean isEmpty() {
        return taskQueue.size() == 0;
    }

    public synchronized int size() {
        return taskQueue.size();
    }
}
