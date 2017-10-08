/*
 * see license.txt 
 */
package colony.game;

/**
 * @author Tony
 *
 */
public class Logger {

    /**
     * 
     */
    public Logger() {
        // TODO Auto-generated constructor stub
    }


    
    public static RuntimeException elog(Exception e) {
        return elog("", e);
    }
    
    public static RuntimeException elog(String msg, Exception e) {
        return elog("*** " + msg + " ==> " + e);
    }
    
    public static RuntimeException elog(String msg) {
        System.out.println(msg);
        return new RuntimeException(msg);
    }
    
    public static void log(Exception e) {
        log("", e);
    }
    
    public static void log(String msg, Exception e) {
        log("*** " + msg + " ==> " + e);
    }
    
    public static void log(Object msg) {
        System.out.println(msg);
    }
}
