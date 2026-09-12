package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    // ---------------------------------------------------------------------
    // Appointment module additions (Rohan). Appended only — nothing above
    // this line was modified, so other modules can add their own helpers
    // below without conflicting.
    // ---------------------------------------------------------------------

    /**
     * Formats a date using the application-wide pattern from {@link Constants}.
     * <p>
     * A new {@link SimpleDateFormat} is created per call on purpose: SimpleDateFormat
     * is not thread-safe, and a shared static instance would corrupt output if the
     * clinic app ever formats dates from more than one thread.
     *
     * @param date the date to format; may be {@code null}
     * @return the formatted date, or an empty string when {@code date} is {@code null}
     */
    public static String format(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
    }

    /**
     * Parses a date written in the application-wide pattern.
     *
     * @param text the text to parse, e.g. {@code "2026-09-15 10:30"}
     * @return the parsed date
     * @throws IllegalArgumentException when the text is blank or does not match the pattern
     */
    public static Date parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Date must not be empty. Expected format: " + Constants.DATE_FORMAT);
        }
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        formatter.setLenient(false);
        try {
            return formatter.parse(text.trim());
        } catch (ParseException e) {
            // Chained so the original parse failure is not lost.
            throw new IllegalArgumentException(
                    "Invalid date '" + text + "'. Expected format: " + Constants.DATE_FORMAT, e);
        }
    }

    /**
     * @param date the date to test
     * @return {@code true} when the date is non-null and strictly in the future
     */
    public static boolean isFuture(Date date) {
        return date != null && date.after(new Date());
    }

    /**
     * Compares two dates for slot equality at minute precision, ignoring any
     * stray seconds/milliseconds that parsing or {@code new Date()} may carry.
     *
     * @param first  first date; may be {@code null}
     * @param second second date; may be {@code null}
     * @return {@code true} when both are non-null and fall in the same minute
     */
    public static boolean isSameSlot(Date first, Date second) {
        if (first == null || second == null) {
            return false;
        }
        return first.getTime() / 60000L == second.getTime() / 60000L;
    }
}
