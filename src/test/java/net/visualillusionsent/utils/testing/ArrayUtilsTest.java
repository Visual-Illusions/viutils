package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jason (darkdiplomat)
 */
public class ArrayUtilsTest {

    @Test
    public void mergeByteArray() {
        byte[] test1 = new byte[]{ 0, 20, 50, 127 };
        byte[] test2 = new byte[]{ -1, -21, -51, -128 };
        byte[] expect = new byte[]{ 0, 20, 50, 127, -1, -21, -51, -128 };
        Assert.assertArrayEquals(expect, ArrayUtils.arrayMerge(test1, test2));
    }

    @Test
    public void mergeObjectArray() {
        String[] test1 = new String[]{ "A", "B", "C", "D" };
        String[] test2 = new String[]{ "E", "F", "G", "H" };
        String[] expect = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H" };
        Assert.assertArrayEquals(expect, ArrayUtils.arrayMerge(test1, test2));
    }

    @Test
    public void mergeObjectArraySafe() {
        String[] test1 = new String[]{ "A", "B", "C", "D", "E", "F" };
        String[] test2 = new String[]{ "E", "F", "G", "H" };
        String[] expect = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H" };
        Assert.assertArrayEquals(expect, ArrayUtils.safeArrayMerge(test1, test2, new String[1]));
    }

    @Test
    public void mergeEmptyArray() {
        Object[] testA = new Object[0];
        Object[] testB = new Object[]{ "IMATEST" };
        Object[] expect = new Object[]{ "IMATEST" };
        Assert.assertArrayEquals(expect, ArrayUtils.arrayMerge(testA, testB));
    }
}
