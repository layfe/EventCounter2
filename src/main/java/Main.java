import counter.EventCounter;
import counter.EventCounterImpl;

import static java.time.LocalDateTime.now;

/**
 * Created by Vitalina on 15.09.18.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {


        EventCounter eventCounter = new EventCounterImpl(5);
        Thread addThread =
                new Thread(() -> {
                    while (true) {
                        eventCounter.addEvent(now());
                        eventCounter.addEvent(now().minusSeconds(1));
                        eventCounter.addEvent(now().minusSeconds(2));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Thread resultThread =
                new Thread(() -> {
                    while (true) {
                        System.out.println("------------------------------------");
                        System.out.println("За последнюю минуту: " + eventCounter.getCountEventsLastMinute());
                        System.out.println("За последний час: " + eventCounter.getCountEventsLastHour());
                        System.out.println("За последний день: " + eventCounter.getCountEventsLastDay());
                        System.out.println("------------------------------------");
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

        addThread.start();
        resultThread.start();

        addThread.join();
        resultThread.join();
    }
}
