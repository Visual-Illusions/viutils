package net.visualillusionsent.utils;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void longToDateTest() {
        Assert.assertEquals("01-Jan-1970", DateUtils.longToDate(43200001));
    }
}
