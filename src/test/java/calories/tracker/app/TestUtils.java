package calories.tracker.app;


import org.apache.commons.lang3.NotImplementedException;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Utility methods used for test purposes
 *
 */
public class TestUtils {

    private TestUtils() {
        throw new NotImplementedException("Utility classes cannot be instantiated");
    }

    public static Date date(int year, int month, int day) {
        return new Date(year - 1900, month -1, day);
    }

    public static Time time(String timeStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Time time = null;
        try {
            Date date = formatter.parse("1970/01/01 " + timeStr);
            time = new Time(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

}
