package me.rogueliver.staffonsteroids.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([dhms])");

    public static long parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return -1;
        }

        long totalMillis = 0;
        Matcher matcher = TIME_PATTERN.matcher(duration.toLowerCase());

        while (matcher.find()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "d" -> totalMillis += TimeUnit.DAYS.toMillis(amount);
                case "h" -> totalMillis += TimeUnit.HOURS.toMillis(amount);
                case "m" -> totalMillis += TimeUnit.MINUTES.toMillis(amount);
                case "s" -> totalMillis += TimeUnit.SECONDS.toMillis(amount);
            }
        }

        return totalMillis > 0 ? totalMillis : -1;
    }

    public static Date getExpirationDate(String duration) {
        long millis = parseDuration(duration);
        return millis > 0 ? new Date(System.currentTimeMillis() + millis) : null;
    }

    public static String formatDuration(String duration) {
        return duration.toLowerCase()
                .replace("d", " day(s)")
                .replace("h", " hour(s)")
                .replace("m", " minute(s)")
                .replace("s", " second(s)");
    }
}