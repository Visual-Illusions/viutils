package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.TimeZone;

public class DateUtilsTest {

    @Test
    public void longToDateTest() {
        Assert.assertEquals("13-Feb-2013", DateUtils.longToDate(1360800460000L, TimeZone.getTimeZone("CST")));
    }

    @Test
    public void longToTimeTest() {
        Assert.assertEquals("00:07:40", DateUtils.longToTime(1360800460000L, TimeZone.getTimeZone("GMT")));
    }

    @Test
    public void dayshoursminutesseconds() {
        Assert.assertEquals("2 days, 4 hours, 8 minutes and 16 seconds", DateUtils.getTimeUntil(187696));
    }

    @Test
    public void hoursminutesseconds() {
        Assert.assertEquals("4 hours, 8 minutes and 16 seconds", DateUtils.getTimeUntil(14896));
    }

    @Test
    public void daysminutes() {
        Assert.assertEquals("2 days and 8 minutes", DateUtils.getTimeUntil(173280));
    }

    @Test
    public void hoursseconds() {
        Assert.assertEquals("4 hours and 16 seconds", DateUtils.getTimeUntil(14416));
    }
}
