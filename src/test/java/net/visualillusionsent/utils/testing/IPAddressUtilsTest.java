package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.IPAddressUtils;
import org.junit.Assert;
import org.junit.Test;

public class IPAddressUtilsTest {

    @Test
    public void isIPv4Test() {
        Assert.assertTrue(IPAddressUtils.isIPv4Address("127.0.0.1"));
    }

    @Test
    public void isIPv6Test() {
        Assert.assertTrue(IPAddressUtils.isIPv6Address("0:12:345:6789:A:BC:DEF:A2C4"));
    }

    public void ipv4ToLongTest() {
        Assert.assertEquals(16777343, IPAddressUtils.ipv4ToLong(new byte[]{ 127, 0, 0, 1 }));
    }

    @Test
    public void longIPtoBytestoStringTest() {
        Assert.assertEquals("127.0.0.1", IPAddressUtils.ipv4BytestoString(IPAddressUtils.longToIPv4(16777343)));
    }
}
