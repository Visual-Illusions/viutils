package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testPadRight() {
        Assert.assertEquals("Test---", StringUtils.padCharRight("Test", 3, '-'));
    }

    @Test
    public void testPadLeft() {
        Assert.assertEquals("---Test", StringUtils.padCharLeft("Test", 3, '-'));
    }

    @Test
    public void byteArrayToStringArrayTest() {
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, StringUtils.byteArrayToStringArray(new byte[]{ 1, 2, 3 }));
    }

    @Test
    public void testByteArrayToString() {
        Assert.assertEquals("1,2,3", StringUtils.byteArrayToString(new byte[]{ 1, 2, 3 }, ","));
    }

    @Test
    public void hexadecimalStringToLongArray() {
        Assert.assertArrayEquals(new long[]{ 0, 255 }, StringUtils.stringToLongArray("0x0,0XFF"));
    }

    @Test
    public void testDoubleArrayToString() {
        Assert.assertEquals("3.14,9.3214,14.4233", StringUtils.doubleArrayToString(new double[]{ 3.14, 9.3214, 14.4233 }, ","));
    }
}
