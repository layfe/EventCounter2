import counter.EventCounter;
import counter.EventCounterImpl;
import org.junit.Test;

import static counter.EventCounter.SECONDS_PER_DAY;
import static counter.EventCounter.SECONDS_PER_HOUR;
import static counter.EventCounter.SECONDS_PER_MINUTE;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;

public class EventCounterTest {

    @Test
    public void checkLastMinute59() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusSeconds(59));

        assertEquals("Событие 59 сек назад, ожидалось, что попадет",
                eventCounter.getCountEventsLastMinute(), 2);
    }

    @Test
    public void checkLastMinute60() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusSeconds(60));

        assertEquals("Событие 60 сек назад, ожидалось, что не попадет",
                eventCounter.getCountEventsLastMinute(), 1);
    }

    @Test
    public void checkLastMinuteSame() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());

        assertEquals("Неправильное количество добавлений, сделанных в одну секунду",
                eventCounter.getCountEventsLastMinute(), 6);
    }

    @Test
    public void checkLastMinuteDifferent() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusSeconds(7));
        eventCounter.addEvent(now().minusSeconds(62));
        eventCounter.addEvent(now().minusSeconds(87));
        eventCounter.addEvent(now().minusSeconds(45));
        assertEquals("Неправильное количество добавлений, сделанных в разные секунды",
                eventCounter.getCountEventsLastMinute(), 3);
    }


    @Test
    public void checkLastHour59() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusMinutes(59).minusSeconds(59));

        assertEquals("Событие 59 мин 59 сек назад, ожидалось, что попадет",
                eventCounter.getCountEventsLastHour(), 2);
    }

    @Test
    public void checkLastHour60() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusMinutes(60));

        assertEquals("Событие 60 мин назад, ожидалось, что не попадет",
                eventCounter.getCountEventsLastHour(), 1);
    }

    @Test
    public void checkLastHourSame() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());

        assertEquals("Неправильное количество добавлений, сделанных в одну секунду",
                eventCounter.getCountEventsLastHour(), 6);
    }

    @Test
    public void checkLastHourDifferent() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusMinutes(7));
        eventCounter.addEvent(now().minusMinutes(62));
        eventCounter.addEvent(now().minusMinutes(87));
        eventCounter.addEvent(now().minusMinutes(45));
        assertEquals("Неправильное количество добавлений, сделанных в разные минуты",
                eventCounter.getCountEventsLastHour(), 3);
    }


    @Test
    public void checkLastDay59() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusHours(23).minusMinutes(59).minusSeconds(59));

        assertEquals("Событие 23 часа 59 мин 59 сек назад, ожидалось, что попадет",
                eventCounter.getCountEventsLastDay(), 2);
    }

    @Test
    public void checkLastDay60() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusHours(24));

        assertEquals("Событие 60 мин назад, ожидалось, что не попадет",
                eventCounter.getCountEventsLastDay(), 1);
    }

    @Test
    public void checkLastDaySame() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());
        eventCounter.addEvent(now());

        assertEquals("Неправильное количество добавлений, сделанных в одну секунду",
                eventCounter.getCountEventsLastDay(), 6);
    }

    @Test
    public void checkLastDayDifferent() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusHours(7));
        eventCounter.addEvent(now().minusHours(24));
        eventCounter.addEvent(now().minusHours(25));
        eventCounter.addEvent(now().minusHours(1));
        assertEquals("Неправильное количество добавлений, сделанных в разные часы",
                eventCounter.getCountEventsLastDay(), 3);
    }

    @Test
    public void checkEventsCountInSecondsDifferent() {
        EventCounter eventCounter = new EventCounterImpl();
        eventCounter.addEvent(now());
        eventCounter.addEvent(now().minusSeconds(7));
        eventCounter.addEvent(now().minusHours(2));
        eventCounter.addEvent(now().minusHours(23));
        eventCounter.addEvent(now().minusMinutes(25));
        eventCounter.addEvent(now().minusSeconds(95));

        assertEquals("Неправильное количество добавлений за последнюю секунду",
                eventCounter.getCountEventsLastSeconds(1), 1);

        assertEquals("Неправильное количество добавлений за последние 30 сек",
                eventCounter.getCountEventsLastSeconds(30), 2);

        assertEquals("Неправильное количество добавлений за последние 30 мин",
                eventCounter.getCountEventsLastSeconds(30 * SECONDS_PER_MINUTE), 4);

        assertEquals("Неправильное количество добавлений за последние 3 часа",
                eventCounter.getCountEventsLastSeconds(3 * SECONDS_PER_HOUR), 5);

        assertEquals("Неправильное количество добавлений за последний день",
                eventCounter.getCountEventsLastSeconds(SECONDS_PER_DAY), 6);
    }
}
