package ShefRobot;

import java.util.*;
import java.util.concurrent.*;

abstract class PortManager<T> implements Runnable {

    private Thread thread;
    private Thread parentThread;
    private Semaphore actSem;
    private LinkedList<T> actions;
    private boolean killflag;

    public PortManager(Thread parentThread) {
        this.parentThread = parentThread;
        actSem = new Semaphore(1);
        actions = new LinkedList<T>();
        killflag = false;
        thread = new Thread(this);
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    public void addAction(T act) {
        try {
            actSem.acquire();
            actions.add(act);
            actSem.release();
            thread.interrupt();
        } catch (InterruptedException e) {
            // Try again...
            addAction(act);
        }
    }

    public void kill() {
        killflag = true;
        this.thread.interrupt();
    }

    public void run() {
        while (!killflag) {
            if (this.parentThread.getState() == Thread.State.TERMINATED) {
                this.killflag = true;
                this.close();
            } else {
                try {
                    actSem.acquire();
                    for (T act: actions) {
                        action(act);
                    }
                    actions.clear();
                    actSem.release();

                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }
        }
    }

    abstract void action(T act);
    protected abstract void close();
}