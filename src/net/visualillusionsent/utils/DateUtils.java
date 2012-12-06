package net.visualillusionsent.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utilities
 * <p>
 * Provides static methods to help with {@link Date} manipulations
 * <p>
 * This File is part of the VIUtils<br>
 * (c) 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class DateUtils {
    private static DateFormat date_form = new SimpleDateFormat("dd-MMM-yyyy"), //
            time_form = new SimpleDateFormat("HH:mm:ss"), //
            datetime_form = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    /**
     * This class should never be constructed
     */
    private DateUtils() {}

    /**
     * Returns the Date as a {@link String} formatted as dd-MMM-yyyy
     * 
     * @param time
     *            the time in milliseconds
     * @return date as a string formatted as dd-MMM-yyyy
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToDate(long time) throws UtilityException {
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }
        Date date = new Date(time);
        return date_form.format(date);
    }

    /**
     * Returns the Time as a {@link String} formatted as HH:mm:ss
     * 
     * @param time
     *            the time in milliseconds
     * @return time as a string formatted as HH:mm:ss
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToTime(long time) throws UtilityException {
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }
        Date date = new Date(time);
        return time_form.format(date);
    }

    /**
     * Returns the Date and Time as a {@link String} formatted as dd-MMM-yyyy HH:mm:ss
     * 
     * @param time
     *            the time in milliseconds
     * @return date and time as a string formatted as dd-MMM-yyyy HH:mm:ss
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToDateTime(long time) throws UtilityException {
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }
        Date date = new Date(time);
        return datetime_form.format(date);
    }

    /**
     * Returns the Date and/or Time as a {@link String} formatted as specified
     * 
     * @param time
     *            the time in milliseconds
     * @param format
     *            the format to use
     * @return date and time as a string formatted as specifed
     * @throws UtilityException
     * <br>
     *             if time is less than 0<br>
     *             or if the format is invalid/{@code null}
     */
    public static final String longToFormatedDateTime(long time, String format) throws UtilityException {
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }
        else if (format == null) {
            throw new UtilityException("Format cannot be null");
        }
        Date date = new Date(time);
        DateFormat form;
        try {
            form = new SimpleDateFormat(format);
        }
        catch (IllegalArgumentException iae) {
            throw new UtilityException("Improper format syntax");
        }
        return form.format(date);
    }
}
