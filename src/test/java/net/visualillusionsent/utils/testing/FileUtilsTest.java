package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jason (darkdiplomat)
 */
public class FileUtilsTest {

    @Test
    public void signatureTest() {
        Assert.assertArrayEquals(new byte[]{ (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE }, FileUtils.FileSignatures.JAVA_CLASS.getSignature());
    }
}
