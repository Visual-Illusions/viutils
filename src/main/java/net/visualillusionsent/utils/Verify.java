package net.visualillusionsent.utils;

/**
 * Verify
 * <p/>
 * Provides methods to help with verifying input
 * <p/>
 * Currently internal use only
 *
 * @author Jason (darkdiplomat)
 * @version 1.0
 * @since 1.2.0
 */
final class Verify {

    private static final float classVersion = 1.0F;

    static final void notNull(Object obj, String arg) throws UtilityException {
        if (obj == null)
            throw new UtilityException("arg.null", arg);
    }

    static final void notEmpty(String str, String arg) throws UtilityException {
        if (str.trim().isEmpty())
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(Object[] objArray, String arg) throws UtilityException {
        if (objArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(byte[] byteArray, String arg) throws UtilityException {
        if (byteArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(short[] shortArray, String arg) throws UtilityException {
        if (shortArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(int[] intArray, String arg) throws UtilityException {
        if (intArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(long[] longArray, String arg) throws UtilityException {
        if (longArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(float[] floatArray, String arg) throws UtilityException {
        if (floatArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(double[] doubleArray, String arg) throws UtilityException {
        if (doubleArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notEmpty(boolean[] booleanArray, String arg) throws UtilityException {
        if (booleanArray.length <= 0)
            throw new UtilityException("arg.empty", arg);
    }

    static final void notNegativeOrZero(Number number, String arg) {
        if (number.doubleValue() <= 0)
            throw new UtilityException("num.zeroOrLess", arg);
    }

    static final void notNegative(Number number, String arg) {
        if (number.doubleValue() < 0)
            throw new UtilityException("num.negative", arg);
    }

    static final void notOutOfRange(long check, long range, String msg) {
        if (check > range)
            throw new UtilityException(msg);
    }

    static final void notOutOfRangeEqual(long check, long range, String msg) {
        if (check >= range)
            throw new UtilityException(msg);
    }

    /**
     * Gets this class's version number
     *
     * @return the class version
     */
    public static float getClassVersion() {
        return classVersion;
    }
}
