package sedridor.amidst.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sedridor.amidst.Amidst;
import sedridor.amidst.Options;
import sedridor.amidst.Util;
import sedridor.amidst.logging.Log;
import sedridor.amidst.map.MapObjectPlayer;
import sedridor.amidst.map.layers.StrongholdLayer;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.preferences.BiomeColorProfile;
import sedridor.amidst.preferences.SelectPrefModel;
import sedridor.amidst.project.FinderWindow;
import sedridor.amidst.project.Project;
import sedridor.amidst.project.SaveLoader;
import sedridor.amidst.resources.ResourceLoader;
import sedridor.amidst.version.VersionInfo;
import sedridor.forgeamidst.ForgeAmidst;

public class AmidstMenu extends JMenuBar {

    private final JMenu fileMenu;

    public final JMenu mapMenu;

    private final JMenu optionsMenu;

    private final JMenu helpMenu;

    public JMenuItem reloadMap;

    public JMenuItem newMenu;

    public JMenuItem currentWorld;

    public JMenuItem saveLevel;

    public JMenuItem reloadMenuItem;

    private int selectedWorldType = -1;

    private final FinderWindow window;

    public AmidstMenu(FinderWindow window) {
        this.window = window;
        this.fileMenu = add((JMenu) new FileMenu());
        this.mapMenu = add((JMenu) new MapMenu());
        this.optionsMenu = add((JMenu) new OptionsMenu());
        this.helpMenu = add((JMenu) new HelpMenu());
    }

    private class FileMenu extends JMenu {

        private FileMenu() {
            super("File");
            setMnemonic(70);
            AmidstMenu.this.newMenu = add((JMenuItem) new JMenu("New") {

                {
                    setEnabled((ForgeAmidst.getWorld() == null));
                    setMnemonic(78);
                    add((JMenuItem) new AmidstMenu.FileMenu.SeedMenuItem());
                    add((JMenuItem) new AmidstMenu.FileMenu.FileMenuItem());
                    add((JMenuItem) new AmidstMenu.FileMenu.RandomSeedMenuItem());
                }
            });
            AmidstMenu.this.reloadMap = add(new JMenuItem("Reload") {

                {
                    setEnabled(false);
                    setAccelerator(KeyStroke.getKeyStroke(85, 128));
                    addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (AmidstMenu.this.window.curProject != null) {
                                long seed = AmidstMenu.this.window.curProject.seed;
                                SaveLoader.Type worldType = null;
                                if (ForgeAmidst.getWorld() != null) {
                                    seed = (ForgeAmidst.getWorld()).provider.getSeed();
                                    worldType = SaveLoader.Type.fromMixedCase(
                                        (ForgeAmidst.getWorld()).provider.terrainType.getWorldTypeName());
                                } else {
                                    worldType = SaveLoader.Type
                                        .fromMixedCase(AmidstMenu.this.window.curProject.worldType);
                                }
                                if (worldType != null) {
                                    Point centerLocation = new Point(
                                        (int) ((float) AmidstMenu.this.window.curProject.map.getWidth() * 0.5F),
                                        (int) ((float) AmidstMenu.this.window.curProject.map.getHeight() * 0.5F));
                                    final int mapY = (AmidstMenu.this.window.curProject.map.getMap()
                                        .screenToLocal(centerLocation.getLocation())).y;
                                    final int mapX = (AmidstMenu.this.window.curProject.map.getMap()
                                        .screenToLocal(centerLocation.getLocation())).x;
                                    AmidstMenu.this.window.clearProject();
                                    AmidstMenu.this.window.setProject(new Project(seed, worldType.getValue()));
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {

                                        public void run() {
                                            AmidstMenu.this.window.curProject.moveMapTo((long) mapX, (long) mapY);
                                        }
                                    }, 20L);
                                }
                            }
                        }
                    });
                }
            });
            AmidstMenu.this.currentWorld = add(new JMenuItem("Current world") {

                {
                    setEnabled((ForgeAmidst.getWorld() != null));
                    setAccelerator(KeyStroke.getKeyStroke(87, 128));
                    addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (ForgeAmidst.getWorld() != null) {
                                AmidstMenu.this.window.clearProject();
                                AmidstMenu.this.window.setProject(
                                    new Project(
                                        (ForgeAmidst.getWorld()).provider.getSeed(),
                                        (ForgeAmidst.getWorld()).provider.terrainType.getWorldTypeName()));
                            }
                        }
                    });
                }
            });
            addSeparator();
            AmidstMenu.this.saveLevel = add(new JMenuItem("Save player locations") {

                {
                    setEnabled(false);
                    setAccelerator(KeyStroke.getKeyStroke(83, 128));
                    addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            if (AmidstMenu.this.window.curProject.saveLoaded) {
                                for (MapObjectPlayer player : AmidstMenu.this.window.curProject.save.getPlayers()) {
                                    if (player.needSave) {
                                        AmidstMenu.this.window.curProject.save
                                            .movePlayer(player.getName(), player.globalX, player.globalY);
                                        player.needSave = false;
                                    }
                                }
                                setEnabled(false);
                            }
                        }
                    });
                }
            });
            addSeparator();
            add(new JMenuItem("Exit") {

                {
                    addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            AmidstMenu.this.window.dispose();
                        }
                    });
                }
            });
        }

        private String showSeedPrompt(String title) {
            String blankText = "A random seed will be generated if left blank.";
            String leadingSpaceText = "Warning: There is a space at the start!";
            String trailingSpaceText = "Warning: There is a space at the end!";
            final JTextField inputText = new JTextField();
            inputText.addAncestorListener(new AncestorListener() {

                public void ancestorAdded(AncestorEvent e) {
                    inputText.requestFocus();
                }

                public void ancestorMoved(AncestorEvent e) {
                    inputText.requestFocus();
                }

                public void ancestorRemoved(AncestorEvent e) {
                    inputText.requestFocus();
                }
            });
            final JLabel inputInformation = new JLabel("A random seed will be generated if left blank.");
            inputInformation.setForeground(Color.red);
            inputInformation.setFont(new Font("arial", 1, 10));
            inputText.getDocument()
                .addDocumentListener(new DocumentListener() {

                    public void changedUpdate(DocumentEvent e) {
                        update();
                    }

                    public void insertUpdate(DocumentEvent e) {
                        update();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        update();
                    }

                    public void update() {
                        String text = inputText.getText();
                        if (text.equals("")) {
                            inputInformation.setText("A random seed will be generated if left blank.");
                            inputInformation.setForeground(Color.red);
                        } else if (text.startsWith(" ")) {
                            inputInformation.setText("Warning: There is a space at the start!");
                            inputInformation.setForeground(Color.red);
                        } else if (text.endsWith(" ")) {
                            inputInformation.setText("Warning: There is a space at the end!");
                            inputInformation.setForeground(Color.red);
                        } else {
                            try {
                                Long.parseLong(text);
                                inputInformation.setText("Seed is valid.");
                                inputInformation.setForeground(Color.gray);
                            } catch (NumberFormatException e) {
                                inputInformation.setText("This seed's value is " + text.hashCode() + ".");
                                inputInformation.setForeground(Color.black);
                            }
                        }
                    }
                });
            JComponent[] inputs = { new JLabel("Enter your seed: "), inputInformation, inputText };
            int result = JOptionPane.showConfirmDialog(AmidstMenu.this.window, inputs, title, 2);
            return (result == 0) ? inputText.getText() : null;
        }

        private class SeedMenuItem extends JMenuItem {

            private SeedMenuItem() {
                super("From seed");
                setAccelerator(KeyStroke.getKeyStroke(78, 128));
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String seed = FileMenu.this.showSeedPrompt("New Project");
                        if (seed != null) {
                            String worldTypePreference = Options.instance.worldType.get();
                            SaveLoader.Type worldType = null;
                            if (worldTypePreference.equals("Prompt each time")) {
                                if (AmidstMenu.this.selectedWorldType > -1) {
                                    worldType = (SaveLoader.Type) AmidstMenu.this.choose(
                                        "New Project",
                                        "Select world type\n",
                                        SaveLoader.selectableTypes,
                                        AmidstMenu.this.selectedWorldType);
                                } else {
                                    worldType = (SaveLoader.Type) AmidstMenu.this
                                        .choose("New Project", "Select world type\n", SaveLoader.selectableTypes);
                                }
                                for (int i = 0; i < SaveLoader.selectableTypes.length; i++) {
                                    if (SaveLoader.selectableTypes[i] == worldType)
                                        AmidstMenu.this.selectedWorldType = i;
                                }
                            } else {
                                worldType = SaveLoader.Type.fromMixedCase(worldTypePreference);
                            }
                            if (seed.equals("")) seed = String.valueOf(new Random().nextLong());
                            if (worldType != null) {
                                AmidstMenu.this.window.clearProject();
                                AmidstMenu.this.window.setProject(new Project(seed, worldType.getValue()));
                            }
                        }
                    }
                });
            }
        }

        private class RandomSeedMenuItem extends JMenuItem {

            private RandomSeedMenuItem() {
                super("From random seed");
                setAccelerator(KeyStroke.getKeyStroke(82, 128));
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Random random = new Random();
                        long seed = random.nextLong();
                        String worldTypePreference = Options.instance.worldType.get();
                        SaveLoader.Type worldType = null;
                        if (worldTypePreference.equals("Prompt each time")) {
                            if (AmidstMenu.this.selectedWorldType > -1) {
                                worldType = (SaveLoader.Type) AmidstMenu.this.choose(
                                    "New Project",
                                    "Select world type\n",
                                    SaveLoader.selectableTypes,
                                    AmidstMenu.this.selectedWorldType);
                            } else {
                                worldType = (SaveLoader.Type) AmidstMenu.this
                                    .choose("New Project", "Select world type\n", SaveLoader.selectableTypes);
                            }
                            for (int i = 0; i < SaveLoader.selectableTypes.length; i++) {
                                if (SaveLoader.selectableTypes[i] == worldType) AmidstMenu.this.selectedWorldType = i;
                            }
                        } else {
                            worldType = SaveLoader.Type.fromMixedCase(worldTypePreference);
                        }
                        if (worldType != null) {
                            AmidstMenu.this.window.clearProject();
                            AmidstMenu.this.window.setProject(new Project(seed, worldType.getValue()));
                        }
                    }
                });
            }
        }

        private class FileMenuItem extends JMenuItem {

            private FileMenuItem() {
                super("From saved game...");
                setAccelerator(KeyStroke.getKeyStroke(79, 128));
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(SaveLoader.getFilter());
                        fc.setAcceptAllFileFilterUsed(false);
                        fc.setFileSelectionMode(2);
                        File savesDir = null;
                        if (Util.minecraftDirectory != null) {
                            savesDir = new File(Util.minecraftDirectory, "saves");
                        } else {
                            savesDir = new File("saves");
                        }
                        fc.setCurrentDirectory(savesDir);
                        fc.setFileHidingEnabled(false);
                        if (fc.showOpenDialog(AmidstMenu.this.window) == 0) {
                            File f = fc.getSelectedFile();
                            SaveLoader s = null;
                            if (f.isDirectory()) {
                                s = new SaveLoader(new File(f.getAbsoluteFile() + "/level.dat"));
                            } else {
                                s = new SaveLoader(f);
                            }
                            AmidstMenu.this.window.clearProject();
                            AmidstMenu.this.window.setProject(new Project(s));
                        }
                    }
                });
            }
        }
    }

    private class DisplayingCheckbox extends JCheckBoxMenuItem {

        private DisplayingCheckbox(String text, BufferedImage icon, int key, JToggleButton.ToggleButtonModel model) {
            super(text, (icon != null) ? new ImageIcon(icon) : null);
            if (key != -1) setAccelerator(KeyStroke.getKeyStroke(key, 128));
            setModel(model);
        }

        private DisplayingCheckbox(String text, BufferedImage icon, int key, JToggleButton.ToggleButtonModel model,
            ActionListener actionListener) {
            super(text, (icon != null) ? new ImageIcon(icon) : null);
            if (key != -1) setAccelerator(KeyStroke.getKeyStroke(key, 128));
            addActionListener(actionListener);
        }
    }

    private class MapMenu extends JMenu {

        private MapMenu() {
            super("Map");
            setEnabled(false);
            setMnemonic(77);
            add((JMenuItem) new FindMenu());
            add((JMenuItem) new GoToMenu());
            add((JMenuItem) new LayersMenu());
            add((JMenuItem) new ZoomMenu());
            add((JMenuItem) new CopySeedMenuItem());
            add((JMenuItem) new CaptureMenuItem());
        }

        private class FindMenu extends JMenu {

            private FindMenu() {
                super("Find");
                add(new JMenuItem("Stronghold") {

                    {
                        setAccelerator(KeyStroke.getKeyStroke(70, 128));
                        addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                AmidstMenu.this
                                    .goToChosenPoint(StrongholdLayer.instance.getStrongholds(), "Stronghold");
                            }
                        });
                    }
                });
            }
        }

        private class GoToMenu extends JMenu {

            private GoToMenu() {
                super("Go to");
                add(new JMenuItem("Coordinate") {

                    {
                        setAccelerator(KeyStroke.getKeyStroke(71, 128));
                        addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                String s = JOptionPane
                                    .showInputDialog(null, "Enter coordinates: (Ex. 123,456)", "Go To", 3);
                                if (s != null) {
                                    String[] c = s.replaceAll(" ", "")
                                        .split(",");
                                    try {
                                        long x = Long.parseLong(c[0]);
                                        long y = Long.parseLong(c[1]);
                                        AmidstMenu.this.window.curProject.moveMapTo(x, y);
                                    } catch (NumberFormatException e1) {
                                        Log.w("Invalid location entered, ignoring.");
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
                add(new JMenuItem("Player") {

                    {
                        addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                if (AmidstMenu.this.window.curProject.saveLoaded) {
                                    List<MapObjectPlayer> playerList = AmidstMenu.this.window.curProject.save
                                        .getPlayers();
                                    MapObjectPlayer[] players = playerList
                                        .<MapObjectPlayer>toArray(new MapObjectPlayer[playerList.size()]);
                                    MapObjectPlayer p = (MapObjectPlayer) AmidstMenu.this
                                        .choose("Go to", "Select player:", players);
                                    if (p != null)
                                        AmidstMenu.this.window.curProject.moveMapTo((long) p.globalX, (long) p.globalY);
                                } else if ((AmidstMenu.this.window.curProject.map.getPlayerLayer()).isEnabled) {
                                    MapObjectPlayer thePlayer = (AmidstMenu.this.window.curProject.map
                                        .getPlayerLayer()).thePlayer;
                                    if (thePlayer != null) AmidstMenu.this.window.curProject
                                        .moveMapTo((long) thePlayer.globalX, (long) thePlayer.globalY);
                                }
                            }
                        });
                    }
                });
            }
        }

        private class LayersMenu extends JMenu {

            private LayersMenu() {
                super("Layers");
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Grid",
                        ResourceLoader.getImage("grid.png"),
                        49,
                        Options.instance.showGrid));
                addSeparator();
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Player Icons",
                        ResourceLoader.getImage("player.png"),
                        50,
                        Options.instance.showPlayers));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Slime chunks",
                        ResourceLoader.getImage("slime.png"),
                        51,
                        Options.instance.showSlimeChunks));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Village Icons",
                        ResourceLoader.getImage("village.png"),
                        52,
                        Options.instance.showVillages));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Temple/Igloo/Witch Hut Icons",
                        ResourceLoader.getImage("desert_temple.png"),
                        53,
                        Options.instance.showTemples));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Ocean Monument Icons",
                        ResourceLoader.getImage("ocean_monument.png"),
                        54,
                        Options.instance.showOceanMonuments));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Stronghold Icons",
                        ResourceLoader.getImage("stronghold.png"),
                        55,
                        Options.instance.showStrongholds));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Nether Fortress Icons",
                        ResourceLoader.getImage("nether_fortress.png"),
                        56,
                        Options.instance.showNetherFortresses));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Spawn Location Icon",
                        ResourceLoader.getImage("spawn.png"),
                        57,
                        Options.instance.showSpawn));
                addSeparator();
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Hide icons",
                        null,
                        48,
                        Options.instance.hideObjects));
            }
        }

        private class ZoomMenu extends JMenuItem {

            private ZoomMenu() {
                super("Set zoom");
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String s = JOptionPane.showInputDialog(null, "Enter zoom: (Ex. 0.25)", "OK", 3);
                        if (s != null) {
                            s = s.replaceAll(" ", "");
                            try {
                                double scale = Double.parseDouble(s);
                                AmidstMenu.this.window.curProject.map.getMap()
                                    .setZoom(scale);
                            } catch (NumberFormatException e1) {
                                Log.w("Invalid value entered, ignoring.");
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

        private class CaptureMenuItem extends JMenuItem {

            private CaptureMenuItem() {
                super("Save to image...");
                setAccelerator(KeyStroke.getKeyStroke(84, 128));
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (2 == (0x2 & e.getModifiers())) {
                            String suggestedFilename = "map_" + AmidstMenu.this.window.curProject.seed
                                + "_"
                                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
                                + ".png";
                            File savesDir = Options.instance.savesDir.get();
                            if (savesDir != null && savesDir.exists()) {
                                savesDir = Options.instance.savesDir.get();
                            } else if (Util.minecraftDirectory != null) {
                                savesDir = new File(Util.minecraftDirectory, "");
                            } else {
                                savesDir = new File(System.getProperty("user.dir"));
                            }
                            AmidstMenu.this.window.curProject.map.saveToFile(new File(savesDir, suggestedFilename));
                        } else {
                            JFileChooser fc = new JFileChooser();
                            fc.setFileFilter(new PNGFileFilter());
                            fc.setAcceptAllFileFilterUsed(false);
                            String suggestedFilename = "map_" + AmidstMenu.this.window.curProject.seed
                                + "_"
                                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
                                + ".png";
                            fc.setSelectedFile(new File(suggestedFilename));
                            File savesDir = Options.instance.savesDir.get();
                            if (savesDir != null && savesDir.exists()) {
                                savesDir = Options.instance.savesDir.get();
                            } else if (Util.minecraftDirectory != null) {
                                savesDir = new File(Util.minecraftDirectory, "");
                            } else {
                                savesDir = new File(System.getProperty("user.dir"));
                            }
                            fc.setCurrentDirectory(savesDir);
                            int returnVal = fc.showSaveDialog(AmidstMenu.this.window);
                            if (returnVal == 0) {
                                Options.instance.savesDir.set(fc.getCurrentDirectory());
                                String s = fc.getSelectedFile()
                                    .toString();
                                if (!s.toLowerCase()
                                    .endsWith(".png")) s = s + ".png";
                                AmidstMenu.this.window.curProject.map.saveToFile(new File(s));
                            }
                        }
                    }
                });
            }
        }

        private class CopySeedMenuItem extends JMenuItem {

            private CopySeedMenuItem() {
                super("Copy seed to Clipboard");
                setAccelerator(KeyStroke.getKeyStroke(67, 128));
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        StringSelection stringSelection = new StringSelection(String.valueOf(Options.instance.seed));
                        Clipboard clipboard = Toolkit.getDefaultToolkit()
                            .getSystemClipboard();
                        clipboard.setContents(stringSelection, new ClipboardOwner() {

                            public void lostOwnership(Clipboard arg0, Transferable arg1) {}
                        });
                    }
                });
            }
        }
    }

    private class OptionsMenu extends JMenu {

        private OptionsMenu() {
            super("Options");
            add((JMenuItem) new MapOptionsMenu());
            add((JMenuItem) new MapTypeMenu());
            if (BiomeColorProfile.isEnabled) add((JMenuItem) new BiomeColorMenu());
            add((JMenuItem) new WorldTypeMenu());
            setMnemonic(77);
        }

        private class BiomeColorMenu extends JMenu {

            private ArrayList<JCheckBoxMenuItem> profileCheckboxes = new ArrayList<JCheckBoxMenuItem>();

            private class BiomeProfileActionListener implements ActionListener {

                private BiomeColorProfile profile;

                private ArrayList<JCheckBoxMenuItem> profileCheckboxes;

                private JCheckBoxMenuItem checkBox;

                public BiomeProfileActionListener(BiomeColorProfile profile, JCheckBoxMenuItem checkBox,
                    ArrayList<JCheckBoxMenuItem> profileCheckboxes) {
                    this.profile = profile;
                    this.checkBox = checkBox;
                    this.profileCheckboxes = profileCheckboxes;
                }

                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < this.profileCheckboxes.size(); i++) this.profileCheckboxes.get(i)
                        .setSelected(false);
                    this.checkBox.setSelected(true);
                    this.profile.activate();
                    Options.instance.selectedColorProfile.set(this.profile.name);
                }

                public BiomeColorProfile getProfile() {
                    return this.profile;
                }
            }

            private int getProfileIndex(String name) {
                int defaultIndex = 0;
                for (int i = 0; i < this.profileCheckboxes.size(); i++) {
                    if (this.profileCheckboxes.get(i)
                        .getText()
                        .equals(name)) return i;
                    if (this.profileCheckboxes.get(i)
                        .getText()
                        .equals("Default")) defaultIndex = i;
                }
                Options.instance.selectedColorProfile.set("Default");
                return defaultIndex;
            }

            private BiomeColorMenu() {
                super("Biome profile");
                AmidstMenu.this.reloadMenuItem = new JMenuItem("Reload Menu");
                final BiomeColorMenu biomeColorMenu = this;
                AmidstMenu.this.reloadMenuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String selectedBiomeColorProfile = "";
                        for (int i = 0; i < BiomeColorMenu.this.profileCheckboxes.size(); i++) {
                            if (BiomeColorMenu.this.profileCheckboxes.get(i)
                                .isSelected())
                                selectedBiomeColorProfile = BiomeColorMenu.this.profileCheckboxes.get(i)
                                    .getText();
                        }
                        BiomeColorMenu.this.profileCheckboxes.clear();
                        Log.i("Reloading additional biome color profiles.");
                        File colorProfileFolder = new File(ForgeAmidst.getDataDir(), "config/forgeamidst/biome");
                        biomeColorMenu.removeAll();
                        BiomeColorMenu.this.scanAndLoad(colorProfileFolder, biomeColorMenu);
                        for (int j = 0; j < BiomeColorMenu.this.profileCheckboxes.size(); j++) {
                            if (BiomeColorMenu.this.profileCheckboxes.get(j)
                                .getText()
                                .equals(selectedBiomeColorProfile)) {
                                BiomeColorMenu.this.profileCheckboxes.get(j)
                                    .setSelected(true);
                                BiomeColorProfile profile = ((AmidstMenu.OptionsMenu.BiomeColorMenu.BiomeProfileActionListener) ((JCheckBoxMenuItem) BiomeColorMenu.this.profileCheckboxes
                                    .get(j)).getActionListeners()[0]).getProfile();
                                profile.activate();
                            }
                        }
                        biomeColorMenu.addSeparator();
                        biomeColorMenu.add(AmidstMenu.this.reloadMenuItem);
                    }
                });
                AmidstMenu.this.reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
                Log.i("Checking for additional biome color profiles.");
                File colorProfileFolder = new File(ForgeAmidst.getDataDir(), "config/forgeamidst/biome");
                scanAndLoad(colorProfileFolder, this);
                if (Options.instance.selectedColorProfile.get() == null)
                    Options.instance.selectedColorProfile.set("Default");
                int selectedBiomeColorProfile = getProfileIndex(Options.instance.selectedColorProfile.get());
                this.profileCheckboxes.get(selectedBiomeColorProfile)
                    .setSelected(true);
                BiomeColorProfile profile = ((BiomeProfileActionListener) ((JCheckBoxMenuItem) this.profileCheckboxes
                    .get(selectedBiomeColorProfile)).getActionListeners()[0]).getProfile();
                profile.activate();
                addSeparator();
                add(AmidstMenu.this.reloadMenuItem);
            }

            private boolean scanAndLoad(File folder, JMenu menu) {
                File[] files = folder.listFiles();
                boolean foundProfiles = false;
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        if (files[i].getName()
                            .endsWith(".json")) {
                            BiomeColorProfile profile;
                            if ((profile = BiomeColorProfile.createFromFile(files[i])) != null) {
                                JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(profile.name);
                                menuItem.addActionListener(
                                    new BiomeProfileActionListener(profile, menuItem, this.profileCheckboxes));
                                if (profile.shortcut != null) {
                                    KeyStroke accelerator = KeyStroke.getKeyStroke(profile.shortcut);
                                    if (accelerator != null) {
                                        menuItem.setAccelerator(accelerator);
                                    } else {
                                        Log.i("Unable to create keyboard shortcut from: " + profile.shortcut);
                                    }
                                }
                                menu.add((JMenuItem) menuItem);
                                this.profileCheckboxes.add(menuItem);
                                foundProfiles = true;
                            }
                        }
                    } else {
                        JMenu subMenu = new JMenu(files[i].getName());
                        if (scanAndLoad(files[i], subMenu)) menu.add((JMenuItem) subMenu);
                    }
                }
                return foundProfiles;
            }
        }

        private class MapOptionsMenu extends JMenu {

            private MapOptionsMenu() {
                super("Map");
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Metric Grid",
                        null,
                        68,
                        Options.instance.metricGrid));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Map Flicking (Smooth Scrolling)",
                        null,
                        73,
                        Options.instance.mapFlicking));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Use Fragment Fading",
                        null,
                        -1,
                        Options.instance.mapFading));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Restrict Maximum Zoom",
                        null,
                        90,
                        Options.instance.maxZoom));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Show Framerate",
                        null,
                        76,
                        Options.instance.showFPS));
                add((JMenuItem) new AmidstMenu.DisplayingCheckbox("Show Scale", null, 75, Options.instance.showScale));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Show World Type",
                        null,
                        -1,
                        Options.instance.showWorldTypeWidget));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Color Biomes by Climate",
                        null,
                        88,
                        Options.instance.colorByClimate));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Show Climate Info",
                        null,
                        -1,
                        Options.instance.showClimate));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Sorted Biome List",
                        null,
                        -1,
                        Options.instance.sortedBiomeList));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Show Biome IDs",
                        null,
                        -1,
                        Options.instance.showBiomeIDs));
                add(
                    (JMenuItem) new AmidstMenu.OptionsMenu.RTGCheckbox(
                        "Show High Resolution RTG Map",
                        null,
                        72,
                        Options.instance.rtgHighResolution));
                add(
                    (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                        "Show Debug Info",
                        null,
                        -1,
                        Options.instance.showDebug));
            }
        }

        private class WorldTypeMenu extends JMenu {

            private WorldTypeMenu() {
                super("World type");
                SelectPrefModel.SelectButtonModel[] buttonModels = Options.instance.worldType.getButtonModels();
                for (int i = 0; i < buttonModels.length; i++) {
                    add(
                        (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                            buttonModels[i].getName(),
                            null,
                            -1,
                            buttonModels[i]));
                    if (i == 0) addSeparator();
                }
            }
        }

        private class RTGCheckbox extends JCheckBoxMenuItem {

            private RTGCheckbox(String text, BufferedImage icon, int key, JToggleButton.ToggleButtonModel model) {
                super(text, (icon != null) ? new ImageIcon(icon) : null);
                if (key != -1) setAccelerator(KeyStroke.getKeyStroke(key, 128));
                final JCheckBoxMenuItem checkBox = this;
                checkBox.setSelected(
                    Options.instance.rtgHighResolution.get()
                        .booleanValue());
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Options.instance.rtgHighResolution.set(Boolean.valueOf(checkBox.isSelected()));
                        if (AmidstMenu.this.window.curProject != null) if (ForgeAmidst.getWorld() != null)
                            if ((ForgeAmidst.getWorld()).provider.terrainType.getWorldTypeName()
                                .equals("RTG")) {
                                    Point centerLocation = new Point(
                                        (int) ((float) AmidstMenu.this.window.curProject.map.getWidth() * 0.5F),
                                        (int) ((float) AmidstMenu.this.window.curProject.map.getHeight() * 0.5F));
                                    int mapY = (AmidstMenu.this.window.curProject.map.getMap()
                                        .screenToLocal(centerLocation.getLocation())).y;
                                    int mapX = (AmidstMenu.this.window.curProject.map.getMap()
                                        .screenToLocal(centerLocation.getLocation())).x;
                                    int deltaY = (mapY != 0) ? (mapY / -mapY) : 1;
                                    int deltaX = (mapX != 0) ? (mapX / -mapX) : 1;
                                    AmidstMenu.this.window.curProject
                                        .moveMapTo((long) (deltaX * 1000000), (long) (deltaY * 1000000));
                                    AmidstMenu.this.window.curProject.moveMapTo((long) (mapX + 1), (long) (mapY + 1));
                                }
                    }
                });
            }
        }

        private class MapTypeMenu extends JMenu {

            private MapTypeMenu() {
                super("Map type");
                SelectPrefModel.SelectButtonModel[] buttonModels = Options.instance.mapType.getButtonModels();
                for (int i = 0; i < buttonModels.length; i++) {
                    add(
                        (JMenuItem) new AmidstMenu.DisplayingCheckbox(
                            buttonModels[i].getName(),
                            null,
                            -1,
                            buttonModels[i]));
                    if ((buttonModels[i]).name.equals("Climate Control")) {
                        boolean is172 = MinecraftUtil.getVersion()
                            .isAtLeast(VersionInfo.V1_7_2);
                        if (!is172) {
                            buttonModels[i].setEnabled(is172);
                            Options.instance.mapType.set("Default");
                        }
                    }
                }
            }
        }
    }

    private class HelpMenu extends JMenu {

        private HelpMenu() {
            super("Help");
            add(new JMenuItem("About") {

                {
                    addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            JOptionPane.showMessageDialog(
                                AmidstMenu.this.window,
                                "ForgeAMIDST Version " + Amidst.getVersion()
                                    + " by Sedridor\nThis is a version of AMIDST modified to integrate into Minecraft as a Forge Mod\nIt has been developed for Climate Control by Zeno410\n\nAdvanced Minecraft Interfacing and Data/Structure Tracking (AMIDST)\nBy Skidoodle",
                                "About",
                                -1);
                        }
                    });
                }
            });
        }
    }

    private <T> T choose(String title, String message, T[] choices) {
        return (T) JOptionPane.showInputDialog(this.window, message, title, -1, null, choices, choices[0]);
    }

    private <T> T choose(String title, String message, T[] choices, int selected) {
        if (choices.length > selected)
            return (T) JOptionPane.showInputDialog(this.window, message, title, -1, null, choices, choices[selected]);
        return (T) JOptionPane.showInputDialog(this.window, message, title, -1, null, choices, choices[0]);
    }

    private <T extends Point> void goToChosenPoint(T[] points, String name) {
        T p = choose("Go to", "Select " + name + ":", points);
        if (p != null) this.window.curProject.moveMapTo((long) p.x, (long) p.y);
    }
}
