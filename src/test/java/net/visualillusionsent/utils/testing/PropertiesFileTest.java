package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.PropertiesFile;
import org.junit.Assert;
import org.junit.Test;

public class PropertiesFileTest {

    private static final PropertiesFile cfg = new PropertiesFile("src/test/resources/test.cfg");

    @Test
    public void stringTest() {
        Assert.assertEquals("string_value", cfg.getString("string_test"));
    }

    @Test
    public void awkardKeyTest() {
        Assert.assertEquals("akward", cfg.getString("akward#key#test"));
    }

    @Test
    public void primativeTest() {
        Assert.assertEquals(0, cfg.getInt("integer_test"));
        Assert.assertEquals(123456789123456789L, cfg.getLong("long_test"));
    }

    @Test
    public void primativeArrayTest() {
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3 }, cfg.getIntArray("integer_array_test"));
        Assert.assertArrayEquals(new long[]{ 123456789123456789L, 201307110430L, 197001010000L }, cfg.getLongArray("long_array_test"));
    }

    @Test
    public void stringArrayTest() {
        Assert.assertArrayEquals(new String[]{ "string1", "string2", "string3" }, cfg.getStringArray("string_array_test"));
    }

    @Test
    public void inlineCommentTest() {
        Assert.assertEquals("value", cfg.getString("inLineComment"));
        Assert.assertEquals(" Comment", cfg.getInlineComment("inLineComment"));
    }

    @Test
    public void filePathTest() {
        Assert.assertTrue(cfg.getFilePath().contains("src/test/resources/test.cfg"));
    }

    @Test
    public void fileNameTest() {
        Assert.assertEquals("test.cfg", cfg.getFileName());
    }
}
