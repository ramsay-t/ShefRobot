package ShefRobot;

import java.util.*;
import java.util.concurrent.*;

abstract class PortManager implements Runnable {

    private Thread thread;
    private Thread parentThread;
    private Semaphore actSem;
    private LinkedList<String> actions;
    private boolean killflag;

    public PortManager(Thread parentThread) {
	this.parentThread = parentThread;
	actSem = new Semaphore(1);
	actions = new LinkedList<String>();
	killflag = false;
	thread = new Thread(this);
	thread.start();
    }

    public Thread getThread() {
	return thread;
    }
    
    public void addAction(String act) {
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
	while(!killflag) {
	    if(this.parentThread.getState() == Thread.State.TERMINATED) {
		this.killflag = true;
		this.close();
	    } else {
		try {
		    actSem.acquire();
		    for(String act : actions) {
			action(act);
		    }
		    actions.clear();
		    actSem.release();
		    
		    Thread.sleep(500);
		} catch (InterruptedException e) {
		    ;
		}
	    }
	}
    }

    abstract void action(String act);
    abstract void close();
}
