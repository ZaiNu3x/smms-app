package group.intelliboys.smms.utils.converters;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeConverter {
    // Converts a Long timestamp to LocalDateTime, handling null values
    @TypeConverter
    public static LocalDateTime fromTimestamp(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    // Converts a LocalDateTime to Long timestamp, handling null values
    @TypeConverter
    public static Long toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
