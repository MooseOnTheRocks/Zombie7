package dev.foltz;

public class Z7Util {
    public static final int TICKS_PER_SECOND = 20;

    public static int ticksFromSeconds(float seconds) {
        return (int) (seconds * TICKS_PER_SECOND);
    }

    public static int ticksFromMinutes(float minutes) {
        return (int) (minutes * 60 * TICKS_PER_SECOND);
    }
}
