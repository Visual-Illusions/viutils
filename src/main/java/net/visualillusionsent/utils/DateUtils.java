/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2015 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this library.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static net.visualillusionsent.utils.Verify.notEmpty;
import static net.visualillusionsent.utils.Verify.notNegative;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * Provides static methods to help with {@link Date} manipulations
 *
 * @author Jason (darkdiplomat)
 * @version 1.1
 * @since 1.0.0
 */
public final class DateUtils {

    /* 1.1 @ VIUtils 1.4.0 */
    private static final float classVersion = 1.1F;
    /** Date Format as dd-MMM-yyyy */
    private static final DateFormat date_form = new SimpleDateFormat("dd-MMM-yyyy");
    /** Date Format as HH:mm:ss */
    private static final DateFormat time_form = new SimpleDateFormat("HH:mm:ss");
    /** Date Format as dd-MMM-yyyy HH:mm:ss */
    private static final DateFormat datetime_form = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    /** Date Format as HH:mm:ss dd-MMM-yyyy */
    private static final DateFormat timedate_form = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");

    /** This class should never be externally constructed */
    private DateUtils() {
    }

    /**
     * Returns the Date as a {@link String} formatted as dd-MMM-yyyy
     *
     * @param time
     *         the time in milliseconds
     *
     * @return date as a string formatted as dd-MMM-yyyy
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative
     */
    public static String longToDate(long time) {
        return longToDate(time, TimeZone.getDefault());
    }

    /**
     * Returns the Date as a {@link String} formatted as dd-MMM-yyyy for the given TimeZone
     *
     * @param time
     *         the time in milliseconds
     * @param zone
     *         the TimeZone to use
     *
     * @return date as a string formatted as dd-MMM-yyyy
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative
     */
    public static String longToDate(long time, TimeZone zone) {
        notNegative(time, "Time");

        if (zone != null && zone != TimeZone.getDefault()) {
            date_form.setTimeZone(zone);
        }
        return date_form.format(parseCal(time, zone).getTime());
    }

    /**
     * Returns the Time as a {@link String} formatted as HH:mm:ss
     *
     * @param time
     *         the time in milliseconds
     *
     * @return time as a string formatted as HH:mm:ss
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative
     */
    public static String longToTime(long time) {
        return longToTime(time, TimeZone.getDefault());
    }

    /**
     * Returns the Time as a {@link String} formatted as HH:mm:ss in given TimeZone
     *
     * @param time
     *         the time in milliseconds
     *
     * @return time as a string formatted as HH:mm:ss
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative
     */
    public static String longToTime(long time, TimeZone zone) {
        notNegative(time, "Time");

        if (zone != null && zone != TimeZone.getDefault()) {
            time_form.setTimeZone(zone);
        }
        return time_form.format(parseCal(time, zone).getTime());
    }

    /**
     * Returns the Date and Time as a {@link String} formatted as dd-MMM-yyyy HH:mm:ss
     *
     * @param time
     *         the time in milliseconds
     *
     * @return date and time as a string formatted as dd-MMM-yyyy HH:mm:ss
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative
     */
    public static String longToDateTime(long time) {
        return longToDateTime(time, TimeZone.getDefault());
    }

    /**
     * Returns the Date and Time as a {@link String} formatted as dd-MMM-yyyy HH:mm:ss in given TimeZone
     *
     * @param time
     *         the time in milliseconds
     * @param zone
     *         the TimeZone to use
     *
     * @return date and time as a string formatted as dd-MMM-yyyy HH:mm:ss
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative
     */
    public static String longToDateTime(long time, TimeZone zone) {
        notNegative(time, "Time");

        if (zone != null && zone != TimeZone.getDefault()) {
            datetime_form.setTimeZone(zone);
        }
        return datetime_form.format(parseCal(time, zone).getTime());
    }

    /**
     * Returns the Time and Date as a {@link String} formatted as HH:mm:ss dd-MMM-yyyy
     *
     * @param time
     *         the time in milliseconds
     *
     * @return date and time as a string formatted as HH:mm:ss dd-MMM-yyyy
     */
    public static String longToTimeDate(long time) {
        return longToTimeDate(time, TimeZone.getDefault());
    }

    /**
     * Returns the Time and Date as a {@link String} formatted as HH:mm:ss dd-MMM-yyyy in given TimeZone
     *
     * @param time
     *         the time in milliseconds
     * @param zone
     *         the TimeZone to use
     *
     * @return date and time as a string formatted as HH:mm:ss dd-MMM-yyyy
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative
     */
    public static String longToTimeDate(long time, TimeZone zone) {
        notNegative(time, "Time");

        if (zone != null && zone != TimeZone.getDefault()) {
            timedate_form.setTimeZone(zone);
        }
        return timedate_form.format(parseCal(time, zone).getTime());
    }

    /**
     * Returns the Date and/or Time as a {@link String} formatted as specified
     *
     * @param time
     *         the time in milliseconds as per: the difference between the current time and 00:00:00 January 1, 1970 UTC.
     * @param format
     *         the format to use
     *
     * @return date and time as a string formatted as specified
     *
     * @throws java.lang.IllegalArgumentException
     *         if time is negative or if format is empty
     * @throws java.lang.NullPointerException
     *         if format is null
     */
    public static String longToFormatedDateTime(long time, String format) {
        notNegative(time, "Time");
        notNull(format, "String format");
        notEmpty(format, "String format");

        //Create a Date object from the time given
        Date date = new Date(time);
        //Create DateFormat object and verify the format is valid
        DateFormat form = new SimpleDateFormat(format);
        //Format it and return
        return form.format(date);
    }

    /**
     * Parses a {@link String} date into a {@link Date} object
     *
     * @param date
     *         the {@link String} representation of the Date
     *
     * @return {@link Date} parsed from the date parameter
     *
     * @throws java.lang.IllegalArgumentException
     *         if date is empty or date is unable to be parsed
     * @throws java.lang.NullPointerException
     *         if date is null
     */
    public static Date getDateFromString(String date) {
        notNull(date, "String date");
        notEmpty(date, "String date");

        Date theDate;
        try {
            theDate = datetime_form.parse(date);
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException(pe.getMessage());
        }
        return theDate;
    }

    /**
     * Parses a {@link Date} object into a {@link String} date.
     * This is a String that can later be passed to {@link DateUtils#getDateFromString(java.lang.String)}.
     * 
     * The format here is: "dd-MMM-yyyy HH:mm:ss"
     *
     * @param date
     *         the {@link Date} to parse into a {@link String}.
     *
     * @return {@link String} formatted from the date parameter
     */
    public static String getStringFromDate(Date date) {
        notNull(date, "String date");
        
        return datetime_form.format(date);
    }

    private static Calendar parseCal(long time, TimeZone to) {
        Calendar calendar = Calendar.getInstance();
        TimeZone fromTimeZone = calendar.getTimeZone();
        TimeZone toTimeZone = to != null ? to : TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(fromTimeZone);
        calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
        if (fromTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
        }
        calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
        if (toTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
        }
        calendar.setTimeInMillis(time);
        return calendar;
    }

    /**
     * Get the Unix timestamp for the current time.<br>
     * The time in seconds since January 01 1970
     *
     * @return {@code long} timestamp
     */
    public static long getUnixTimestamp() {
        return (System.currentTimeMillis() / 1000L);
    }

    /**
     * Gets a readable string for the days/hours/minutes/seconds until a period of time
     *
     * @param time
     *         the Unix-TimeStamp to start from
     * @param delay
     *         the delay from the start point until expiration
     *
     * @return the String representation of the time until
     */
    public static String getTimeUntil(final long time, final long delay) {
        long correctedTime = (time + delay) - getUnixTimestamp();
        if (correctedTime <= 0) {
            return "ERR Time";
        }
        return getTimeUntil(correctedTime);
    }

    /**
     * Gets a readable English {@code String} for the days/hours/minutes/seconds until a period of time
     *
     * @param time
     *         the Unix-TimeStamp of (or amount of seconds until) the future time expiration
     *
     * @return the String representation of the time until
     */
    public static String getTimeUntil(final long time) {
        if (time <= 0) {
            return "ERR Time";
        }
        // How many days left?
        StringBuilder stringTimeLeft = new StringBuilder();
        int[] until = getTimeUntilArray(time);
        if (until[0] > 0) {
            stringTimeLeft.append(Integer.toString(until[0])).append(until[0] == 1 ? " day" : " days");
        }
        if (until[1] > 0) {
            if (stringTimeLeft.length() != 0) {
                stringTimeLeft.append(until[2] > 0 || until[3] > 0 ? ", " : " and ");
            }
            stringTimeLeft.append(Integer.toString(until[1])).append(until[1] == 1 ? " hour" : " hours");
        }
        if (until[2] > 0) {
            if (stringTimeLeft.length() != 0) {
                stringTimeLeft.append(until[3] > 0 ? ", " : " and ");
            }
            stringTimeLeft.append(Integer.toString(until[2])).append(until[2] == 1 ? " minute" : " minutes");
        }
        if (until[3] > 0) {
            if (stringTimeLeft.length() != 0) {
                stringTimeLeft.append(" and ");
            }
            stringTimeLeft.append(Integer.toString(until[3])).append(until[3] == 1 ? " second" : " seconds");
        }
        return stringTimeLeft.toString();
    }

    /**
     * Gets an {@code int[]} representing the number of days, hours, minutes and seconds based on a given Unix Timestamp<br/>
     * Useful for processing the information into a proper localized {@code String}
     *
     * @param time
     *         the time in seconds to break down into days, hours, minutes and seconds
     *
     * @return an {@code int[4]} as Index 0 = days, 1 = hours, 2 = minutes, and 3 = seconds including zeros
     */
    public static int[] getTimeUntilArray(final long time) {
        int[] toRet = new int[]{ 0, 0, 0, 0 };
        if (time <= 0) {
            return toRet;
        }
        long temp = time;
        long conversion = TimeUnit.DAYS.toSeconds(1);
        if (temp >= conversion) {
            int days = (int) Math.floor(temp / conversion);
            temp -= conversion * days;
            toRet[0] = days;
        }
        conversion = TimeUnit.HOURS.toSeconds(1);
        if (temp >= conversion) {
            int hours = (int) Math.floor(temp / conversion);
            temp -= conversion * hours;
            toRet[1] = hours;
        }
        conversion = TimeUnit.MINUTES.toSeconds(1);
        if (temp >= conversion) {
            int minutes = (int) Math.floor(temp / conversion);
            temp -= conversion * minutes;
            toRet[2] = minutes;
        }
        toRet[3] = (int) temp;
        return toRet;
    }

    /**
     * Gets this class's version number
     *
     * @return the class version
     */
    public static float getClassVersion() {
        return classVersion;
    }
}
