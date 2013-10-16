package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.BooleanUtils;
import org.junit.Assert;
import org.junit.Test;

public class BooleanUtilsTest {

    @Test
    public void regBooleanTrue() {
        Assert.assertNull(BooleanUtils.registerBoolean("golden", true));
    }

    @Test
    public void testBooleanTrue() {
        Assert.assertTrue(BooleanUtils.parseBoolean("golden"));
    }

    @Test
    public void regBooleanFalse() {
        Assert.assertNotNull(BooleanUtils.registerBoolean("no", false));
    }

    @Test
    public void testBooleanFalse() {
        Assert.assertFalse(BooleanUtils.parseBoolean("no"));
    }
}
