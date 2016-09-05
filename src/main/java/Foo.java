import it.sauronsoftware.cron4j.Scheduler;

import java.util.concurrent.TimeUnit;

/**
 * Created by abhi on 3/9/16.
 */
public class Foo {
    public static void main(String[] args) {
        Scheduler s = new Scheduler();
        s.schedule("* * * * *", new Runnable() {
            @Override
            public void run() {
                System.out.println("Tick tock");
            }
        });

        s.start();

        try {
            TimeUnit.MINUTES.sleep(2);
        } catch (InterruptedException e) {
        }

        s.stop();
    }
}
