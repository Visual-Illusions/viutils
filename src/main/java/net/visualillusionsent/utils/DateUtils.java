/*
 * Copyright © 2012-2013 Visual Illusions Entertainment.
 *  
 * This file is part of VIUtils.
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with VIUtils.
 * If not, see http://www.gnu.org/licenses/lgpl.html
 */
package net.visualillusionsent.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides static methods to help with {@link Date} manipulations
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class DateUtils {

    /**
     * Date Format as dd-MMM-yyyy
     */
    private static DateFormat date_form = new SimpleDateFormat("dd-MMM-yyyy");

    /**
     * Date Format as HH:mm:ss
     */
    private static DateFormat time_form = new SimpleDateFormat("HH:mm:ss");

    /**
     * Date Formate as dd-MMM-yyyy HH:mm:ss
     */
    private static DateFormat datetime_form = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

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
        //Is time negative?
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }

        //Create a Date object from the time given
        Date date = new Date(time);

        //Format it and return
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
        //Is time negative?
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }

        //Create a Date object from the time given
        Date date = new Date(time);

        //Format it and return
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
        // Is time negative?
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }

        //Create a Date object from the time given
        Date date = new Date(time);

        //Format it and return
        return datetime_form.format(date);
    }

    /**
     * Returns the Date and/or Time as a {@link String} formatted as specified
     * 
     * @param time
     *            the time in milliseconds as per: the difference between the current time and 00:00:00 January 1, 1970 UTC.
     * @param format
     *            the format to use
     * @return date and time as a string formatted as specifed
     * @throws UtilityException
     * <br>
     *             if time is less than 0<br>
     *             if the format is invalid or {@code null}
     */
    public static final String longToFormatedDateTime(long time, String format) throws UtilityException {
        //Is time negative?
        if (time < 0) {
            throw new UtilityException("Time cannot be a negative number");
        }
        //Is format null?
        else if (format == null) {
            throw new UtilityException("arg.null", "format");
        }

        //Create a Date object from the time given
        Date date = new Date(time);

        //Create DateFormat object and verify the format is valid
        DateFormat form;
        try {
            form = new SimpleDateFormat(format);
        }
        catch (IllegalArgumentException iae) {
            //Invalid format throws an UtilityException
            throw new UtilityException("Invalid format syntax");
        }

        //Format it and return
        return form.format(date);
    }

    /**
     * Parses a {@link String} date into a {@link Date} object
     * 
     * @param date
     *            the {@link String} representation of the Date
     * @return {@link Date} parsed from the date parameter
     * @throws UtilityException
     */
    public static final Date getDateFromString(String date) throws UtilityException {
        Date theDate = null;
        try {
            theDate = datetime_form.parse(date);
        }
        catch (ParseException pe) {
            throw new UtilityException(pe.getMessage());
        }
        return theDate;
    }
}
