package io.github.steaf23.bingoreloaded.util;

import java.util.function.Consumer;

public abstract class GameTimer {
    protected Consumer<Long> notifier;
    protected String worldName;
    private long time;

    public GameTimer(String worldName) {
        this.worldName = worldName;
    }

    public static String getTimeAsString(long seconds) {
        seconds = Math.max(seconds, 0);
        if (seconds >= 60) {
            long minutes = (seconds / 60) % 60;
            if (seconds >= 3600) {
                long hours = seconds / 3600;
                return String.format("%02d:%02d:%02d", hours, minutes, seconds % 60);
            }
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
        return String.format("00:%02d", seconds % 60);
    }

    public abstract void start();

    public abstract long pause();

    public abstract long stop();

    public abstract Message getTimeDisplayMessage();

    public long getTime() {
        return time;
    }

    protected void updateTime(long newTime) {
        time = newTime;
        notifier.accept(newTime);
    }

    public void setNotifier(Consumer<Long> notifier) {
        this.notifier = notifier;
    }
}
