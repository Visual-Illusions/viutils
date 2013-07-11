package net.visualillusionsent.utils.testing;

import java.util.TimeZone;

import net.visualillusionsent.utils.DateUtils;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void longToDateTest() {
        Assert.assertEquals("13-Feb-2013", DateUtils.longToDate(1360800460000L, TimeZone.getTimeZone("CST")));
    }

    @Test
    public void longToTimeTest() {
        Assert.assertEquals("00:07:40", DateUtils.longToTime(1360800460000L, TimeZone.getTimeZone("GMT")));
    }
}
