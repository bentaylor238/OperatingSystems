import java.util.LinkedList;
import java.util.Optional;

/**
 * Contains a linked list queue through aggregation so only one thread can access the certain methods at a time
 * @author Benjamin Taylor
 */
public class TaskQueue<E> {
    private LinkedList<E> taskQueue = new LinkedList();

    // May not necessarily need to be synchronized since only a single thread accesses it, but adds tasks to the queue
    public synchronized void enqueue(E digit) {
        taskQueue.add(digit);
    }

    /**
     * takes a task from the queue, returns
     * @return a task, or the digit to compute for pi
     */
    public synchronized Optional<E> dequeue() {
        if (taskQueue.isEmpty()) return Optional.empty();

        return Optional.of(taskQueue.remove());
    }

    public synchronized boolean isEmpty() {
        return taskQueue.size() == 0;
    }

    public synchronized int size() {
        return taskQueue.size();
    }
}
