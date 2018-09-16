package counter;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Регистрирует события и выдает их количество за последние минуту, час, сутки
 */
public interface EventCounter {
    /**
     * Seconds per minute.
     */
    int SECONDS_PER_MINUTE = 60;
    /**
     * Seconds per hour.
     */
    int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
    /**
     * Seconds per day.
     */
    int SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;

    /**
     * Добавляет событие для его учета в статистике
     *
     * @param second время наступления события в секундах Unix time
     */
    void addEvent(long second);

    /**
     * Возвращает количество событий за последнии seconds секунд
     *
     * @param seconds количество секунд, за которые нужно вернуть события
     * @return количество событий, зарегистрированных за seconds
     */
    long getCountEventsLastSeconds(int seconds);

    /**
     * Добавляет событие для его учета в статистике
     *
     * @param time время наступления события в LocalDataTime
     */
    default void addEvent(LocalDateTime time) {
        addEvent(time.atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    /**
     * Возвращает количество событий за последнюю минуту
     *
     * @return количество событий, зарегистрированных за последнюю минуту
     */
    default long getCountEventsLastMinute() {
        return getCountEventsLastSeconds(SECONDS_PER_MINUTE);
    }

    /**
     * Возвращает количество событий за последний час
     *
     * @return количество событий, зарегистрированных за последний час
     */
    default long getCountEventsLastHour() {
        return getCountEventsLastSeconds(SECONDS_PER_HOUR);
    }

    /**
     * Возвращает количество событий за последний день
     *
     * @return количество событий, зарегистрированных за последний день
     */
    default long getCountEventsLastDay() {
        return getCountEventsLastSeconds(SECONDS_PER_DAY);
    }
}