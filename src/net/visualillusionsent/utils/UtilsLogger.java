package net.visualillusionsent.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utilties Logger Manager
 * <p>
 * For internal use by VIUtils to log errors
 * <p>
 * This File is part of the VIUtils<br>
 * (c) 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtuils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class UtilsLogger {
    private static Logger logger;

    static {
        logger = Logger.getLogger("VIUtils");
        File logDir = new File("viutilslogs/");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        try {
            UtilsLogFormat lf = new UtilsLogFormat();
            ConsoleHandler chand = new ConsoleHandler();
            FileHandler fhand = new FileHandler("viutilslogs/utilslog%g.log", 52428800, 150, true);
            chand.setLevel(Level.ALL);
            fhand.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
            chand.setFormatter(lf);
            fhand.setFormatter(lf);
            fhand.setEncoding("UTF-8");
            chand.setEncoding("UTF-8");
            logger.addHandler(chand);
            logger.addHandler(fhand);
        }
        catch (IOException e) {
            logger.warning("Fail to initialize Logging Formats!");
        }
    }

    /**
     * This class should never be constructed
     */
    private UtilsLogger() {}

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void info(String msg, Throwable thrown) {
        logger.log(Level.INFO, msg, thrown);
    }

    public static void warning(String msg) {
        logger.warning(msg);
    }

    public static void warning(String msg, Throwable thrown) {
        logger.log(Level.WARNING, msg, thrown);
    }

    public static void severe(String msg) {
        logger.severe(msg);
    }

    public static void severe(String msg, Throwable thrown) {
        logger.log(Level.SEVERE, msg, thrown);
    }

    private final static class UtilsLogFormat extends SimpleFormatter {
        private SimpleDateFormat dateform = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        private String linesep = System.getProperty("line.separator");

        public final String format(LogRecord rec) {
            StringBuilder message = new StringBuilder();

            message.append(dateform.format(rec.getMillis()));
            message.append(" [VIUtils - " + rec.getLevel().getName() + "] ");
            message.append(rec.getMessage());
            message.append(linesep);

            if (rec.getThrown() != null) {
                StringWriter stringwriter = new StringWriter();
                rec.getThrown().printStackTrace(new PrintWriter(stringwriter));
                message.append(stringwriter.toString());
            }

            return message.toString();
        }
    }
}
