package foo;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TestClass extends Task {
    private String classId;

    protected TestClass(String classId) {
        this.classId = classId;
    }

    @Override
    public void execute(final TaskExecutionContext context) throws RuntimeException {
        Thread outer = Thread.currentThread();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (outer.isInterrupted()) {
                    System.out.println("Outer thread is interrupted");
                }
            }
        }, 60 * 1000, 60 * 1000);


        System.out.println(this.classId + " - Tick tock");
        while (true) {
/*            try {
                TimeUnit.DAYS.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Stopped");
            }*/
        }
    }

    @Override
    public boolean canBeStopped() {
       return true;
    }

    public static void main(String[] args) throws InterruptedException {
        Scheduler s = new Scheduler();
        TestClass task = new TestClass("1");
        s.schedule("* * * * *", task);

        s.start();

        TimeUnit.MINUTES.sleep(1);

        TaskExecutor[] executors = s.getExecutingTasks();

        System.out.println("Stopping thread");
        TaskExecutor executor = executors[0];
        executor.stop();
    }
}
