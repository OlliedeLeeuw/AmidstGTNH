package sedridor.amidst.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileLogger extends Thread implements LogListener {

    private File file;

    private boolean enabled = true;

    private ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<String>();

    public FileLogger(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                this.enabled = file.createNewFile();
                if (!this.enabled) Log.w(
                    new Object[] { "Unable to create new file at: " + file
                        + " disabling logging to file. (No exception thrown)" });
            } catch (IOException e) {
                Log.w(new Object[] { "Unable to create new file at: " + file + " disabling logging to file." });
                e.printStackTrace();
                this.enabled = false;
            }
        } else if (file.isDirectory()) {
            Log.w(new Object[] { "Unable to log at path: " + file + " because location is a directory." });
            this.enabled = false;
        }
        write("log", new Object[] { "New FileLogger started." });
        start();
    }

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
        write("crash", new Object[] { message });
        if (exceptionText.length() > 0) write("crash", new Object[] { exceptionText });
    }

    private void write(String tag, Object... msgs) {
        StringBuilder stringBuilder = new StringBuilder("[").append(new Timestamp(new Date().getTime()).toString())
            .append("] [")
            .append(tag.toUpperCase())
            .append("] ");
        for (int i = 0; i < msgs.length; i++) {
            stringBuilder.append(msgs[i]);
            stringBuilder.append((i < msgs.length - 1) ? " " : "\r\n");
        }
        this.logQueue.add(stringBuilder.toString());
    }

    public void run() {
        while (this.enabled) {
            if (this.logQueue.size() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                while (this.logQueue.size() != 0) stringBuilder.append(this.logQueue.poll());
                if (this.file.exists() && this.file.isFile()) {
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(this.file, true);
                        writer.append(stringBuilder.toString());
                    } catch (IOException e) {
                        Log.w(new Object[] { "Unable to write to log file." });
                        e.printStackTrace();
                    } finally {
                        try {
                            if (writer != null) writer.close();
                        } catch (IOException e) {
                            Log.w(new Object[] { "Unable to close writer for log file." });
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
