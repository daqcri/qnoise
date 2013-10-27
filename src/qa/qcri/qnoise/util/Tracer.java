/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Tracer {

    //<editor-fold desc="Private fields">
    private static boolean infoFlag;
    private static boolean verboseFlag;
    private static String logFileName;
    private static Calendar calendar;
    private static DateFormat dateFormat;
    private Class classType;
    private PrintStream logger = System.out;

    //</editor-fold>

    static {
        infoFlag = true;
        verboseFlag = false;
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMddHHmmss");
        logFileName = dateFormat.format(calendar.getTime()) + ".txt";
    }

    //<editor-fold desc="Tracer creation">
    private Tracer(Class classType) {
        this.classType = Preconditions.checkNotNull(classType);
        // logger = Logger.getLogger(classType);
    }

    /**
     * Creates a tracer class
     * @param classType input class type.
     * @return Tracer instance.
     */
    public static Tracer getTracer(Class classType) {
        return new Tracer(classType);
    }

    //</editor-fold>

    //<editor-fold desc="Public methods">

    /**
     * Print out info message.
     * @param msg info message.
     */
    public void info(String msg) {
        if (isInfoOn()) {
            logger.println(msg);
        }
    }

    /**
     * Print out verbose message.
     * @param msg message.
     */
    public void verbose(String msg) {
        if (isVerboseOn()) {
            logger.println(msg);
        }
    }

    /**
     * Print out error message.
     * @param message error message.
     * @param ex exceptions.
     */
    public void err(String message, Exception ex) {
        if (!Strings.isNullOrEmpty(message)) {
            logger.println("Error: " + message);
            logger.println("Exception: " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * Print out error message.
     * @param message error message.
     */
    public void err(String message) {
        if (!Strings.isNullOrEmpty(message)) {
            logger.println("Error: " + message);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Static methods">

    /**
     * Turn on / off info printing flag.
     * @param mode on / off.
     */
    public static void setInfo(boolean mode) {
        infoFlag = mode;
    }

    /**
     * Turn on / off verbose printing flag.
     * @param mode on / off.
     */
    public static void setVerbose(boolean mode) {
        verboseFlag = mode;
    }

    /**
     * Returns <code>True</code> when Info flag is on.
     * @return <code>True</code> when Info flag is on.
     */
    public static boolean isInfoOn() {
        return infoFlag;
    }

    /**
     * Returns <code>True</code> when Verbose flag is on.
     * @return <code>True</code> when Verbose flag is on.
     */
    public static boolean isVerboseOn() {
        return verboseFlag;
    }

    //</editor-fold>
}