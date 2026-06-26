package sedridor.amidst;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import sedridor.amidst.logging.Log;
import sedridor.amidst.minecraft.MinecraftInterface;

public class Util {

    public static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";

    private static String osString;

    public static MinecraftInterface mcInterface;

    public static File minecraftDirectory;

    private static final int TEMP_DIR_ATTEMPTS = 1000;

    public static String getOs() {
        if (osString == null) {
            String os = System.getProperty("os.name")
                .toLowerCase();
            if (os.contains("win")) {
                osString = "windows";
            } else if (os.contains("mac")) {
                osString = "osx";
            } else {
                osString = "linux";
            }
        }
        return osString;
    }

    public static void showError(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        String trace = baos.toString();
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, trace, e.toString(), 0);
    }

    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Log.printTraceStack(e);
        }
    }

    public static void setMinecraftInterface(MinecraftInterface minecraftInterface) {
        if (mcInterface == null) mcInterface = minecraftInterface;
    }

    public static void setMinecraftDirectory() {
        if (Options.instance.minecraftPath != null) {
            minecraftDirectory = new File(Options.instance.minecraftPath);
            if (minecraftDirectory.exists() && minecraftDirectory.isDirectory()) return;
            Log.w(
                new Object[] { "Unable to set Minecraft directory to: " + minecraftDirectory
                    + " as that location does not exist or is not a folder." });
        }
        File mcDir = null;
        File homeDirectory = new File(System.getProperty("user.home", "."));
        String os = System.getProperty("os.name")
            .toLowerCase();
        if (os.contains("win")) {
            File appData = new File(System.getenv("APPDATA"));
            if (appData.isDirectory()) mcDir = new File(appData, ".minecraft");
        } else if (os.contains("mac")) {
            mcDir = new File(homeDirectory, "Library/Application Support/minecraft");
        }
        minecraftDirectory = (mcDir != null) ? mcDir : new File(homeDirectory, ".minecraft");
    }

    public static int makeColor(int r, int g, int b) {
        int color = -16777216;
        color |= 0xFF0000 & r << 16;
        color |= 0xFF00 & g << 8;
        color |= 0xFF & b;
        return color;
    }

    public static int mcColor(int color) {
        return 0xFF000000 | color;
    }

    public static int[] getColor(int color) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        return new int[] { r, g, b };
    }

    public static int deselectColor(int color) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int average = r + g + b;
        r = (r + average) / 30;
        g = (g + average) / 30;
        b = (b + average) / 30;
        return makeColor(r, g, b);
    }

    public static int lightenColor(int color, int brightness) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        r += brightness;
        g += brightness;
        b += brightness;
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        return makeColor(r, g, b);
    }

    public static int greyScale(int color) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        int average = (r + g + b) / 3;
        return makeColor(average, average, average);
    }

    public static <T> T readObject(BufferedReader reader, Class<T> clazz) throws JsonIOException, JsonSyntaxException {
        return (T) Amidst.gson.fromJson(reader, clazz);
    }

    public static <T> T readObject(File path, Class<T> clazz) throws IOException, JsonIOException, JsonSyntaxException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        T object = (T) Amidst.gson.fromJson(reader, clazz);
        reader.close();
        return object;
    }

    public static <T> T readObject(String path, Class<T> clazz) throws IOException {
        return Util.<T>readObject(new File(path), clazz);
    }

    public static String capitalizeString(String givenString) {
        if (givenString == null || givenString.isEmpty()) return givenString;
        String[] arr = givenString.replaceAll("_", " ")
            .split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) sb.append(Character.toUpperCase(arr[i].charAt(0)))
            .append(
                arr[i].substring(1)
                    .toLowerCase())
            .append(" ");
        return sb.toString()
            .trim();
    }

    public static File getTempDir(String name) {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = name + "-";
        for (int counter = 0; counter < 1000; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.isDirectory() || tempDir.mkdir()) return tempDir;
        }
        throw new IllegalStateException(
            "Failed to create directory within 1000 attempts (tried " + baseName + "0 to " + baseName + 'ϧ' + ')');
    }
}
