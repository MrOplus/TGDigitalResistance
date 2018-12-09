package tg.digitalresistance.jni;

/**
 * Created by Oplus on 2018/05/03.
 */

public class System {
    static {
        java.lang.System.loadLibrary("system");
    }
    public static native void exec(String cmd);
    public static native String getABI();
}
