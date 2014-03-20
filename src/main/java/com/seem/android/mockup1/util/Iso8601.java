package com.seem.android.mockup1.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @see http://stackoverflow.com/a/10621553/1005503
 */
public final class Iso8601 {
    /**
     * Performance note: I instantiate new SimpleDateFormat every time as means to avoid a bug in Android 2.1.
     * If you're as astonished as I was, see this riddle. For other Java engines,
     * you may cache the instance in a private static field (using ThreadLocal, to be thread safe).
     * http://stackoverflow.com/questions/10624752/simpledateformat-timezone-bug-on-android
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /** Transform Calendar to ISO 8601 string. */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = sdf.format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /** Get current date and time formatted as ISO 8601 string. */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /** Transform ISO 8601 string to Calendar. */
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = sdf.parse(s);
        calendar.setTime(date);
        return calendar;
    }


}
