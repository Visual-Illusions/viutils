package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

public class SystemUtilsTest {

    @Test
    public void testSystemCountry() {
        Assert.assertEquals(SystemUtils.SYSTEM_COUNTRY, System.getProperty("user.country"));
    }
}
