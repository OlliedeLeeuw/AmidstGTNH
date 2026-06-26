package sedridor.amidst.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.swing.filechooser.FileFilter;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.UsernameCache;

import sedridor.amidst.Util;
import sedridor.amidst.logging.Log;
import sedridor.amidst.map.MapObjectPlayer;

public class SaveLoader {

    public static final Type[] worldTypes = new Type[256];

    public static class Type {

        public static final Type DEFAULT = new Type("Default", "default", 0);

        public static final Type FLAT = new Type("Flat", "flat", 1);

        public static final Type LARGE_BIOMES = new Type("Large Biomes", "largeBiomes", 2);

        public static final Type AMPLIFIED = new Type("Amplified", "amplified", 3);

        public static final Type CUSTOMIZED = new Type("Customized", "customized", 15);

        private final String name;

        private final String value;

        private final int id;

        Type(String name, String value, int id) {
            SaveLoader.worldTypes[id] = this;
            this.name = name;
            this.value = value;
            this.id = id;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public int getId() {
            return this.id;
        }

        public static Type fromMixedCase(String name) {
            name = name.toLowerCase();
            for (Type t : SaveLoader.worldTypes) {
                if (t != null) if (t.name.toLowerCase()
                    .equals(name)
                    || t.value.toLowerCase()
                        .equals(name))
                    return t;
            }
            Log.crash("Unable to find World Type: " + name);
            return null;
        }
    }

    public static Type genType = Type.DEFAULT;

    public static Type[] selectableTypes = new Type[] { Type.DEFAULT, Type.LARGE_BIOMES };

    private File file;

    private List<MapObjectPlayer> players;

    public long seed;

    private boolean multi;

    private List<String> back;

    public static FileFilter getFilter() {
        return new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String[] st = f.getName()
                    .split("\\/");
                return st[st.length - 1].equalsIgnoreCase("level.dat");
            }

            public String getDescription() {
                return "Minecraft Data File (level.dat)";
            }
        };
    }

    private String generatorOptions = "";

    public List<MapObjectPlayer> getPlayers() {
        return this.players;
    }

    public void movePlayer(String name, int x, int y) {
        if (this.multi) {
            String outPath = this.file.getParent() + "/playerdata/" + name + ".dat";
            File out = new File(outPath);
            backupFile(out);
            try {
                NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(out));
                NBTTagList nbtTagList = new NBTTagList();
                nbtTagList.appendTag(new NBTTagDouble((double) x));
                nbtTagList.appendTag(new NBTTagDouble(120.0D));
                nbtTagList.appendTag(new NBTTagDouble((double) y));
                nbtTagCompound.setTag("Pos", (NBTBase) nbtTagList);
                CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(out));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File out = this.file;
            backupFile(out);
            try {
                NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(out));
                NBTTagCompound root = nbtTagCompound.getCompoundTag("Data");
                NBTTagCompound playerTag = root.getCompoundTag("Player");
                NBTTagList pos = nbtTagCompound.getTagList("Pos", 6);
                NBTTagList nbtTagList = new NBTTagList();
                nbtTagList.appendTag(new NBTTagDouble((double) x));
                nbtTagList.appendTag(new NBTTagDouble(120.0D));
                nbtTagList.appendTag(new NBTTagDouble((double) y));
                playerTag.setTag("Pos", (NBTBase) nbtTagList);
                root.setTag("Player", (NBTBase) playerTag);
                nbtTagCompound.setTag("Data", (NBTBase) root);
                CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(out));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void backupFile(File inputFile) {
        File outputFile = new File(inputFile.toPath() + "_bak");
        try {
            Files.copy(inputFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SaveLoader(File f) {
        this.file = f;
        this.players = new ArrayList<MapObjectPlayer>();
        this.back = new ArrayList<String>();
        try {
            NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(f));
            NBTTagCompound root = nbtTagCompound.getCompoundTag("Data");
            this.seed = root.getLong("RandomSeed");
            if (root.hasKey("generatorName", 8)) {
                genType = Type.fromMixedCase(root.getString("generatorName"));
                if (genType == Type.CUSTOMIZED) this.generatorOptions = root.getString("generatorOptions");
            }
            File playersFolder = new File(f.getParent(), "playerdata");
            this.multi = (playersFolder.exists() && !root.hasKey("Player", 10));
            if (this.multi) {
                Log.i("Multiplayer map detected.");
            } else {
                Log.i("Singleplayer map detected.");
            }
            if (!this.multi) {
                NBTTagCompound playerTag = root.getCompoundTag("Player");
                UUID uuid = new UUID(playerTag.getLong("UUIDMost"), playerTag.getLong("UUIDLeast"));
                String name = UsernameCache.getLastKnownUsername(uuid);
                if (name == null) name = "Player";
                addPlayer(name, playerTag);
            } else {
                File[] listing = playersFolder.listFiles();
                int i = 0;
                while (true) {
                    if (i < ((listing != null) ? listing.length : 0)) {
                        if (listing[i].isFile()) {
                            NBTTagCompound playerTagCompound = CompressedStreamTools
                                .readCompressed(new FileInputStream(listing[i]));
                            UUID uuid = new UUID(
                                playerTagCompound.getLong("UUIDMost"),
                                playerTagCompound.getLong("UUIDLeast"));
                            String name = UsernameCache.getLastKnownUsername(uuid);
                            if (name == null) name = "Player-" + uuid.toString();
                            addPlayer(name, playerTagCompound);
                        }
                        i++;
                        continue;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Util.showError(e);
        }
    }

    private void addPlayer(String name, NBTTagCompound nbtTagCompound) {
        NBTTagList pos = nbtTagCompound.getTagList("Pos", 6);
        double x = pos.func_150309_d(0);
        double z = pos.func_150309_d(2);
        this.players.add(new MapObjectPlayer(name, (int) x, (int) z));
    }

    public String getGeneratorOptions() {
        return this.generatorOptions;
    }

    public static int getNextSelectableType() {
        for (int i = 0; i < selectableTypes.length; i++) {
            if (selectableTypes[i] == null) return i;
        }
        return -1;
    }

    static {
        for (WorldType worldType : WorldType.worldTypes) {
            if (worldType != null && worldTypes[worldType.getWorldTypeID()] == null
                && worldType.getWorldTypeID() >= 4
                && worldType.getCanBeCreated()) {
                new Type(
                    I18n.format(worldType.getTranslateName(), new Object[0]),
                    worldType.getWorldTypeName(),
                    worldType.getWorldTypeID());
                selectableTypes = Arrays.<Type>copyOf(selectableTypes, selectableTypes.length + 1);
                selectableTypes[selectableTypes.length - 1] = worldTypes[worldType.getWorldTypeID()];
            }
        }
    }
}
