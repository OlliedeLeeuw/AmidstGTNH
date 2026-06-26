package sedridor.amidst;

import java.awt.Image;
import java.io.File;

import com.google.gson.Gson;

import sedridor.amidst.logging.FileLogger;
import sedridor.amidst.logging.Log;
import sedridor.amidst.minecraft.MinecraftInterface;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.preferences.BiomeColorProfile;
import sedridor.amidst.project.FinderWindow;
import sedridor.amidst.resources.ResourceLoader;

public class Amidst {

    private static final int version_major = 1;

    private static final int version_minor = 4;

    private static final String versionOffset = "";

    public static Image icon = ResourceLoader.getImage("icon.png");

    public static final Gson gson = new Gson();

    private static FinderWindow instance;

    public static void main(String[] args) {
        if (Util.mcInterface != null) {
            instance.setVisible(true);
            return;
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread thread, Throwable e) {
                Log.crash(e, "AMIDST has encounted an uncaught exception on thread: " + thread);
            }
        });
        if (Options.instance.logToFile.get() != null)
            Log.addListener("file", new FileLogger(new File(Options.instance.minecraftPath, "logs/forgeamidst.log")));
        if (!isOSX()) Util.setLookAndFeel();
        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("sun.java2d.accthreshold", "0");
        if (Options.instance.minecraftJar != null) {
            try {
                BiomeColorProfile.scan();
                Util.setMinecraftDirectory();
                MinecraftInterface minecraftInterface = new MinecraftInterface(new File(Options.instance.minecraftJar));
                MinecraftUtil.setBiomeInterface(minecraftInterface);
                Util.setMinecraftInterface(minecraftInterface);
                instance = new FinderWindow();
            } catch (Exception e) {
                Log.crash(e, "MalformedURLException on Minecraft load.");
            }
        } else {
            Log.w("Path to Minecraft .jar missing.");
        }
    }

    public static FinderWindow getInstance() {
        return instance;
    }

    private static boolean isOSX() {
        String osName = System.getProperty("os.name");
        return osName.contains("OS X");
    }

    public static String version() {
        if (MinecraftUtil.hasInterface()) return "1.4 [Minecraft " + MinecraftUtil.getVersion() + "]";
        return "1.4";
    }

    public static String getVersion() {
        return "1.4";
    }
}
