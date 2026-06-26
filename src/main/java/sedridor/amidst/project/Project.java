package sedridor.amidst.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import sedridor.amidst.Options;
import sedridor.amidst.logging.Log;
import sedridor.amidst.map.MapObject;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.preferences.BiomeColorProfile;
import sedridor.forgeamidst.ForgeAmidst;

public class Project extends JPanel {

    public MapViewer map;

    public static int FRAGMENT_SIZE = 256;

    private Timer timer;

    public MapObject curTarget;

    public boolean saveLoaded;

    public SaveLoader save;

    public long seed;

    public String worldType;

    public Project(String seed) {
        this(stringToLong(seed));
        Options.instance.seedText = seed;
    }

    public Project(long seed) {
        this(seed, SaveLoader.Type.DEFAULT.getName());
    }

    public Project(SaveLoader file) {
        this(file.seed, SaveLoader.genType.getName(), file);
    }

    public Project(String seed, String type) {
        this(stringToLong(seed), type, null);
    }

    public Project(long seed, String type) {
        this(seed, type, null);
    }

    private void logSeedHistory(long seed, String type) {
        File historyFile = new File(ForgeAmidst.getDataDir(), "config/forgeamidst/seedhistory.txt");
        if (!historyFile.exists()) try {
            historyFile.createNewFile();
        } catch (IOException e) {
            Log.w("Unable to create history file: " + historyFile);
            e.printStackTrace();
            return;
        }
        if (historyFile.exists() && historyFile.isFile()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(historyFile, true);
                writer.append(
                    "[" + MinecraftUtil.getVersion()
                        + " "
                        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                        + "] "
                        + seed
                        + " ("
                        + type
                        + (Options.instance.mapType.get()
                            .equals("Climate Control") ? ":CC" : "")
                        + ")\n");
            } catch (IOException e) {
                Log.w("Unable to write to history file.");
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) writer.close();
                } catch (IOException e) {
                    Log.w("Unable to close writer for history file.");
                    e.printStackTrace();
                }
            }
        }
    }

    public Project(long seed, String type, SaveLoader saveLoader) {
        logSeedHistory(seed, type);
        this.saveLoaded = (saveLoader != null);
        this.save = saveLoader;
        Options.instance.seed = seed;
        this.seed = seed;
        this.worldType = type;
        SaveLoader.Type[] selectableWorldTypes = SaveLoader.selectableTypes;
        BiomeColorProfile.scan(this);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        if (this.saveLoaded) {
            MinecraftUtil.createWorld(seed, type, this.save.getGeneratorOptions());
        } else {
            MinecraftUtil.createWorld(seed, type);
        }
        this.map = new MapViewer(this);
        add(this.map, "Center");
        setBackground(Color.BLUE);
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                Project.this.tick();
            }
        }, 20L, 20L);
    }

    public void tick() {
        this.map.repaint();
    }

    public void dispose() {
        this.map.dispose();
        this.map = null;
        this.timer.cancel();
        this.timer = null;
        this.curTarget = null;
        this.save = null;
        System.gc();
    }

    private static long stringToLong(String seed) {
        long ret;
        try {
            ret = Long.parseLong(seed);
        } catch (NumberFormatException err) {
            ret = (long) seed.hashCode();
        }
        return ret;
    }

    public KeyListener getKeyListener() {
        return this.map;
    }

    public void moveMapTo(long x, long y) {
        this.map.centerAt(x, y);
    }
}
