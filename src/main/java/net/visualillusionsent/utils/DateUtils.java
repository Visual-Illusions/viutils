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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provides static methods to help with {@link Date} manipulations
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class DateUtils{

    private static final float classVersion = 1.0F;
    /**
     * Date Format as dd-MMM-yyyy
     */
    private static final DateFormat date_form = new SimpleDateFormat("dd-MMM-yyyy");
    /**
     * Date Format as HH:mm:ss
     */
    private static final DateFormat time_form = new SimpleDateFormat("HH:mm:ss");
    /**
     * Date Format as dd-MMM-yyyy HH:mm:ss
     */
    private static final DateFormat datetime_form = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    /**
     * Date Format as HH:mm:ss dd-MMM-yyyy
     */
    private static final DateFormat timedate_form = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");

    /**
     * This class should never be externally constructed
     */
    private DateUtils(){}

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
    public static final String longToDate(long time) throws UtilityException{
        return longToDate(time, TimeZone.getDefault());
    }

    /**
     * Returns the Date as a {@link String} formatted as dd-MMM-yyyy for the given TimeZone
     * 
     * @param time
     *            the time in milliseconds
     * @param zone
     *            the TimeZone to use
     * @return date as a string formatted as dd-MMM-yyyy
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToDate(long time, TimeZone zone) throws UtilityException{
        //Is time negative?
        if(time < 0){
            throw new UtilityException("Time cannot be a negative number");
        }
        if(zone != null && zone != TimeZone.getDefault()){
            date_form.setTimeZone(zone);
        }
        return date_form.format(parseCal(time, zone).getTime());
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
    public static final String longToTime(long time) throws UtilityException{
        return longToTime(time, TimeZone.getDefault());
    }

    /**
     * Returns the Time as a {@link String} formatted as HH:mm:ss in given TimeZone
     * 
     * @param time
     *            the time in milliseconds
     * @return time as a string formatted as HH:mm:ss
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToTime(long time, TimeZone zone) throws UtilityException{
        //Is time negative?
        if(time < 0){
            throw new UtilityException("Time cannot be a negative number");
        }
        if(zone != null && zone != TimeZone.getDefault()){
            time_form.setTimeZone(zone);
        }
        return time_form.format(parseCal(time, zone).getTime());
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
    public static final String longToDateTime(long time) throws UtilityException{
        return longToDateTime(time, TimeZone.getDefault());
    }

    /**
     * Returns the Date and Time as a {@link String} formatted as dd-MMM-yyyy HH:mm:ss in given TimeZone
     * 
     * @param time
     *            the time in milliseconds
     * @param zone
     *            the TimeZone to use
     * @return date and time as a string formatted as dd-MMM-yyyy HH:mm:ss
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToDateTime(long time, TimeZone zone) throws UtilityException{
        // Is time negative?
        if(time < 0){
            throw new UtilityException("Time cannot be a negative number");
        }
        if(zone != null && zone != TimeZone.getDefault()){
            datetime_form.setTimeZone(zone);
        }
        return datetime_form.format(parseCal(time, zone).getTime());
    }

    /**
     * Returns the Time and Date as a {@link String} formatted as HH:mm:ss dd-MMM-yyyy
     * 
     * @param time
     *            the time in milliseconds
     * @return date and time as a string formatted as HH:mm:ss dd-MMM-yyyy
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToTimeDate(long time) throws UtilityException{
        return longToTimeDate(time, TimeZone.getDefault());
    }

    /**
     * Returns the Time and Date as a {@link String} formatted as HH:mm:ss dd-MMM-yyyy in given TimeZone
     * 
     * @param time
     *            the time in milliseconds
     * @param zone
     *            the TimeZone to use
     * @return date and time as a string formatted as HH:mm:ss dd-MMM-yyyy
     * @throws UtilityException
     * <br>
     *             if time is less than 0
     */
    public static final String longToTimeDate(long time, TimeZone zone) throws UtilityException{
        // Is time negative?
        if(time < 0){
            throw new UtilityException("Time cannot be a negative number");
        }
        if(zone != null && zone != TimeZone.getDefault()){
            timedate_form.setTimeZone(zone);
        }
        return timedate_form.format(parseCal(time, zone).getTime());
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
    public static final String longToFormatedDateTime(long time, String format) throws UtilityException{
        //Is time negative?
        if(time < 0){
            throw new UtilityException("Time cannot be a negative number");
        }
        //Is format null?
        else if(format == null){
            throw new UtilityException("arg.null", "format");
        }
        //Create a Date object from the time given
        Date date = new Date(time);
        //Create DateFormat object and verify the format is valid
        DateFormat form;
        try{
            form = new SimpleDateFormat(format);
        }
        catch(IllegalArgumentException iae){
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
    public static final Date getDateFromString(String date) throws UtilityException{
        Date theDate = null;
        try{
            theDate = datetime_form.parse(date);
        }
        catch(ParseException pe){
            throw new UtilityException(pe.getMessage());
        }
        return theDate;
    }

    private final static Calendar parseCal(long time, TimeZone to){
        Calendar calendar = Calendar.getInstance();
        TimeZone fromTimeZone = calendar.getTimeZone();
        TimeZone toTimeZone = to != null ? to : TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(fromTimeZone);
        calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
        if(fromTimeZone.inDaylightTime(calendar.getTime())){
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
        }
        calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
        if(toTimeZone.inDaylightTime(calendar.getTime())){
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
    public static long getUnixTimestamp(){
        return (System.currentTimeMillis() / 1000L);
    }

    /**
     * Gets a readable string for the days/hours/minutes/seconds until a period of time
     * 
     * @param time
     *            the Unix-TimeStamp to start from
     * @param delay
     *            the delay from the start point until expiration
     * @return the String representation of the time until
     */
    public static String getTimeUntil(long time, long delay){
        long correctedTime = (time + delay) - getUnixTimestamp();
        if(correctedTime <= 0){
            return "ERR Time";
        }
        return getTimeUntil(correctedTime);
    }

    /**
     * Gets a readable string for the days/hours/minutes/seconds until a period of time
     * 
     * @param time
     *            the Unix-TimeStamp of (or amount of seconds until) the future time expiration
     * @return the String representation of the time until
     */
    public static String getTimeUntil(long time){
        if(time <= 0){
            return "ERR Time";
        }
        // How many days left?
        String stringTimeLeft = "";
        if(time >= 60 * 60 * 24){
            int days = (int)Math.floor(time / (60 * 60 * 24));
            time -= 60 * 60 * 24 * days;
            if(days == 1){
                stringTimeLeft += Integer.toString(days) + " day, ";
            }
            else{
                stringTimeLeft += Integer.toString(days) + " days, ";
            }
        }
        if(time >= 60 * 60){
            int hours = (int)Math.floor(time / (60 * 60));
            time -= 60 * 60 * hours;
            if(hours == 1){
                stringTimeLeft += Integer.toString(hours) + " hour, ";
            }
            else{
                stringTimeLeft += Integer.toString(hours) + " hours, ";
            }
        }
        if(time >= 60){
            int minutes = (int)Math.floor(time / (60));
            time -= 60 * minutes;
            if(minutes == 1){
                stringTimeLeft += Integer.toString(minutes) + " minute ";
            }
            else{
                stringTimeLeft += Integer.toString(minutes) + " minutes ";
            }
        }
        else{
            // Lets remove the last comma, since it will look bad with 2 days, 3 hours, and 14 seconds.
            if(!stringTimeLeft.isEmpty()){
                stringTimeLeft = stringTimeLeft.substring(0, stringTimeLeft.length() - 1);
            }
        }
        int secs = (int)time;
        if(!stringTimeLeft.isEmpty()){
            stringTimeLeft += "and ";
        }
        if(secs == 1){
            stringTimeLeft += secs + " second.";
        }
        else{
            stringTimeLeft += secs + " seconds.";
        }
        return stringTimeLeft;
    }

    /**
     * Gets this class's version number
     * 
     * @return the class version
     */
    public final static float getClassVersion(){
        return classVersion;
    }
}
