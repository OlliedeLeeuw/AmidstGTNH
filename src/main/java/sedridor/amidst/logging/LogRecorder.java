package sedridor.amidst.logging;

import java.sql.Timestamp;
import java.util.Date;

public class LogRecorder implements LogListener {

    private static StringBuffer buffer = new StringBuffer();

    public void debug(Object... o) {
        write("debug", o);
    }

    public void info(Object... o) {
        write("info", o);
    }

    public void warning(Object... o) {
        write("warning", o);
    }

    public void error(Object... o) {
        write("error", o);
    }

    public void crash(Throwable e, String exceptionText, String message) {
        write("crash", message);
        if (exceptionText.length() > 0) write("crash", exceptionText);
    }

    private static void write(String tag, Object... msgs) {
        buffer.append(
            "[" + new Timestamp(new Date().getTime()).toString()
                .substring(11) + "] ");
        buffer.append("[" + tag.toUpperCase() + "] ");
        for (int i = 0; i < msgs.length; i++) buffer.append(msgs[i] + ((i < msgs.length - 1) ? " " : "\n"));
    }

    public static String getContents() {
        return buffer.toString();
    }
}
