package com.airtribe.meditrack.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Formats a Date to a string using the default date format.
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * Formats a Date to a string using the specified format.
     *
     * @param date the date to format
     * @param format the date format pattern
     * @return formatted date string
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Parses a string to a Date using the default date format.
     *
     * @param dateString the date string to parse
     * @return parsed Date
     * @throws ParseException if parsing fails
     */
    public static Date parseDate(String dateString) throws ParseException {
        return parseDate(dateString, DEFAULT_DATE_FORMAT);
    }

    /**
     * Parses a string to a Date using the specified format.
     *
     * @param dateString the date string to parse
     * @param format the date format pattern
     * @return parsed Date
     * @throws ParseException if parsing fails
     */
    public static Date parseDate(String dateString, String format) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(dateString);
    }

    /**
     * Gets the current date as a formatted string.
     *
     * @return current date string
     */
    public static String getCurrentDate() {
        return formatDate(new Date());
    }

    /**
     * Gets the current date and time as a formatted string.
     *
     * @return current datetime string
     */
    public static String getCurrentDateTime() {
        return formatDate(new Date(), DEFAULT_DATETIME_FORMAT);
    }

    /**
     * Adds days to a date.
     *
     * @param date the original date
     * @param days number of days to add
     * @return new date with days added
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        long milliseconds = date.getTime() + (days * 24L * 60 * 60 * 1000);
        return new Date(milliseconds);
    }

    /**
     * Calculates the difference in days between two dates.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return difference in days
     */
    public static long daysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        long diffInMillis = date2.getTime() - date1.getTime();
        return diffInMillis / (24 * 60 * 60 * 1000);
    }

    /**
     * Checks if a date is before another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return true if date1 is before date2
     */
    public static boolean isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.before(date2);
    }

    /**
     * Checks if a date is after another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return true if date1 is after date2
     */
    public static boolean isAfter(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.after(date2);
    }

    /**
     * Checks if a date is today.
     *
     * @param date the date to check
     * @return true if the date is today
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        String today = formatDate(new Date());
        String checkDate = formatDate(date);
        return today.equals(checkDate);
    }

    /**
     * Validates if a date string is in the correct format.
     *
     * @param dateString the date string to validate
     * @param format the expected format
     * @return true if valid
     */
    public static boolean isValidDate(String dateString, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
