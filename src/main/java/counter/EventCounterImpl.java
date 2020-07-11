package counter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.LongStream;

/**
 * Счетчик событий, регистрирует события и выдает их количество за последние минуту, час или сутки.
 * Thread-safe.
 */
public class EventCounterImpl implements EventCounter {
    private static Logger LOGGER = Logger.getLogger(EventCounterImpl.class.getName());
    private static final LongAdder ZERO_LONG_ADDER = new LongAdder();

    /**
     * Мапа, где хранятся пары с ключом = номер секунды, значение = количество событий в эту секунду
     */
    private final Map<Long, LongAdder> eventsFrequency = new ConcurrentHashMap<>();

    /**
     * Создает экземпляр счетчика событий с отчисткой старых элементов каждую минуту
     */
    public EventCounterImpl() {
        this(60);
    }

    /**
     * Создает экземпляр счетчика событий с указанной периодичностью чистки от старых элементов
     *
     * @param periodCleanupInSeconds периодичность отчистки от старых элементов в секундах
     */
    public EventCounterImpl(int periodCleanupInSeconds) {
        ScheduledExecutorService schedulerCleanup = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
        schedulerCleanup.scheduleAtFixedRate(
                this::cleanup,
                periodCleanupInSeconds,
                periodCleanupInSeconds,
                TimeUnit.SECONDS
        );
    }

    /**
     * Добавляет событие для его учета в статистике
     *
     * @param timeInSeconds время наступления события
     */
    @Override
    public void addEvent(long timeInSeconds) {
        if (timeInSeconds >= currentSecond() - SECONDS_PER_DAY + 1) {
            eventsFrequency.computeIfAbsent(timeInSeconds, k -> new LongAdder()).increment();
            LOGGER.info("добавили событие в секунду " + timeInSeconds);
        }
    }

    /**
     * Возвращает количество событий за последнии seconds секунд
     *
     * @param seconds количество секунд, за которые нужно вернуть события
     * @return количество событий, зарегистрированных за seconds
     */
    @Override
    public long getCountEventsLastSeconds(int seconds) {
        long now = currentSecond();
        return sumValues(now - seconds + 1, now);
    }

    /**
     * Суммирует значения из бакетов секунд
     *
     * @param from с какой секунды начинать суммирование
     * @param to   до какой секунды суммировать
     * @return сумму значений из бакетов нужных секунд
     */
    private long sumValues(long from, long to) {
        return LongStream.rangeClosed(from, to)
                .map(s -> eventsFrequency.getOrDefault(s, ZERO_LONG_ADDER).sum())
                .sum();
    }

    /**
     * Удаляет ненужные записи из нашего хранилища. Удаляет записи, старше, чем внесенные за сутки.
     * Если удаление не произошло - исключение логгируется, в надежде на то, что получится
     * в следующую чистку
     */
    private void cleanup() {
        try {
            long now = currentSecond();
            long firstBucket = now - SECONDS_PER_DAY + 1;
            eventsFrequency.keySet().stream()
                    .filter(k -> k < firstBucket)
                    .forEach(eventsFrequency::remove);
            LOGGER.info("Провели отчистку от старых записей");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Не смогли провести отчистку от старых записей", e);
        }
    }

    /**
     * Возвращает текущий Unix time в секундах
     * @return количество секунд с эпохи Unix
     */
    private long currentSecond() {
        return System.currentTimeMillis() / 1000;
    }
}
