package sedridor.amidst.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonSyntaxException;

import sedridor.amidst.Options;
import sedridor.amidst.Util;
import sedridor.amidst.logging.Log;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.project.FinderWindow;
import sedridor.amidst.project.Project;
import sedridor.amidst.project.SaveLoader;
import sedridor.forgeamidst.ForgeAmidst;

public class BiomeColorProfile {

    private class BiomeColor {

        public String alias;

        public int r = 0;

        public int g = 0;

        public int b = 0;

        public BiomeColor(int rgb) {
            this.r = rgb >> 16 & 0xFF;
            this.g = rgb >> 8 & 0xFF;
            this.b = rgb & 0xFF;
        }

        public int toColorInt() {
            return Util.makeColor(this.r, this.g, this.b);
        }
    }

    public static boolean isEnabled = false;

    public LinkedHashMap<String, BiomeColor> colorMap = new LinkedHashMap<String, BiomeColor>();

    public int[] colorArray = new int[Biome.biomes.length];

    public String[] nameArray = new String[Biome.biomes.length];

    public String name;

    public String shortcut;

    public int version;

    public BiomeColorProfile() {
        this.name = "Default";
        for (int i = 0; i < Biome.biomes.length; i++) {
            if (Biome.biomes[i] != null)
                this.colorMap.put((Biome.biomes[i]).name, new BiomeColor((Biome.biomes[i]).color));
        }
    }

    public void fillColorArray() {
        for (Map.Entry<String, BiomeColor> pairs : this.colorMap.entrySet()) {
            int index = Biome.indexFromName(pairs.getKey());
            if (index != -1) {
                this.colorArray[index] = ((BiomeColor) pairs.getValue()).toColorInt();
                this.nameArray[index] = (((BiomeColor) pairs.getValue()).alias != null)
                    ? ((BiomeColor) pairs.getValue()).alias
                    : (Biome.biomes[index]).name;
                continue;
            }
            Log.i(
                new Object[] { "Failed to find biome for: " + (String) pairs.getKey() + " in profile: " + this.name });
        }
    }

    public boolean save(String name, File path) {
        String output = "";
        output = output + "{ \"name\":\"" + name + "\", \"colorMap\":[\r\n";
        for (Map.Entry<String, BiomeColor> pairs : this.colorMap.entrySet()) {
            output = output + "[ \"" + (String) pairs.getKey() + "\", { ";
            output = output + "\"r\":" + ((BiomeColor) pairs.getValue()).r + ", ";
            output = output + "\"g\":" + ((BiomeColor) pairs.getValue()).g + ", ";
            output = output + "\"b\":" + ((BiomeColor) pairs.getValue()).b + " } ],\r\n";
        }
        output = output.substring(0, output.length() - 3);
        output = output + "\r\n], \"version\":4 }\r\n";
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(output);
            writer.close();
            return true;
        } catch (IOException e) {
            try {
                if (writer != null) writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
    }

    public void activate() {
        Options.instance.biomeColorProfile = this;
        Log.i(new Object[] { "Biome color profile activated." });
        for (int i = 0; i < Biome.biomes.length; i++) {
            if (Biome.biomes[i] != null) (Biome.biomes[i]).color = this.colorArray[i];
        }
        if (sedridor.amidst.map.Map.instance != null) sedridor.amidst.map.Map.instance.resetFragments();
    }

    public static void scan() {
        Log.i(new Object[] { "Searching for biome color profiles." });
        File colorProfileFolder = new File(ForgeAmidst.getDataDir(), "config/forgeamidst/biome");
        if (!colorProfileFolder.exists() || !colorProfileFolder.isDirectory()) colorProfileFolder.mkdirs();
        File defaultProfileFile = new File(ForgeAmidst.getDataDir(), "config/forgeamidst/biome/default.json");
        if (!defaultProfileFile.exists()) {
            Options.instance.biomeColorProfile = createFromResource();
            if (!Options.instance.biomeColorProfile.save("Default", defaultProfileFile))
                Log.i(new Object[] { "Attempted to save default biome color profile, but encountered an error." });
        } else if (!defaultProfileSorted(defaultProfileFile)) {
            Options.instance.biomeColorProfile = createFromResource();
            if (!Options.instance.biomeColorProfile.save("Default", defaultProfileFile))
                Log.i(new Object[] { "Attempted to save default biome color profile, but encountered an error." });
        }
        isEnabled = true;
    }

    public static void scan(Project curProject) {
        SaveLoader.Type worldType = SaveLoader.Type.fromMixedCase(curProject.worldType);
        if (worldType == null || worldType.getId() < 4) return;
        File colorProfileFolder = new File(ForgeAmidst.getDataDir(), "config/forgeamidst/biome");
        if (!colorProfileFolder.exists() || !colorProfileFolder.isDirectory()) colorProfileFolder.mkdirs();
        String colorProfile = worldType.getValue()
            .replaceAll("[^a-zA-Z0-9]", "");
        File colorProfileFile = new File(
            ForgeAmidst.getDataDir(),
            "config/forgeamidst/biome/" + colorProfile.toLowerCase() + ".json");
        if (!colorProfileFile.exists()) {
            if (!Options.instance.biomeColorProfile.save(worldType.getName(), colorProfileFile)) Log.i(
                new Object[] {
                    "Attempted to save " + colorProfile + " biome color profile, but encountered an error." });
        } else if (!defaultProfileSorted(colorProfileFile)) {
            if (!Options.instance.biomeColorProfile.save(worldType.getName(), colorProfileFile)) Log.i(
                new Object[] {
                    "Attempted to save " + colorProfile + " biome color profile, but encountered an error." });
        }
        FinderWindow.instance.menuBar.reloadMenuItem.doClick();
    }

    public static BiomeColorProfile createFromFile(File file) {
        BiomeColorProfile profile = null;
        if (file.exists() && file.isFile()) try {
            profile = Util.<BiomeColorProfile>readObject(file, BiomeColorProfile.class);
            profile.setColorMapDefaults();
            profile.fillColorArray();
        } catch (JsonSyntaxException e) {
            Log.w(new Object[] { "Unable to load file: " + file });
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(new Object[] { "Unable to load file: " + file });
        }
        return profile;
    }

    public static BiomeColorProfile createFromResource() {
        BiomeColorProfile profile = null;
        IResourceManager resourceManager = ForgeAmidst.getMC()
            .getResourceManager();
        if (resourceManager != null) try {
            IResource resource = resourceManager.getResource(new ResourceLocation("forgeamidst:biome/default.json"));
            if (resource != null) {
                BufferedReader file = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                try {
                    profile = Util.<BiomeColorProfile>readObject(file, BiomeColorProfile.class);
                    profile.setColorMapDefaults();
                    profile.fillColorArray();
                } catch (JsonSyntaxException e) {
                    Log.w(new Object[] { "Unable to load file: " + file });
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Log.w(new Object[] { "Unable to load resource: forgeamidst:biome/default.json" });
        }
        return profile;
    }

    private void setColorMapDefaults() {
        for (int i = 0; i < Biome.biomes.length; i++) {
            if (Biome.biomes[i] != null && this.colorMap.get((Biome.biomes[i]).name) == null)
                this.colorMap.put((Biome.biomes[i]).name, new BiomeColor((Biome.biomes[i]).color));
        }
    }

    public String getAliasForId(int id) {
        if (this.nameArray[id] != null) return this.nameArray[id];
        return (Biome.biomes[id] != null) ? (Biome.biomes[id]).name : ("BIOME_" + id);
    }

    public static boolean defaultProfileSorted(File path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = "";
            int i = 0;
            while ((line = reader.readLine()) != null) {
                i++;
                if (i == 2) {
                    if (!line.contains("[ \"Ocean\",")) {
                        reader.close();
                        return false;
                    }
                    continue;
                }
                if (line.startsWith("]")) {
                    reader.close();
                    if (line.contains("\"version\":4")) return true;
                    return false;
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.i(new Object[] { "Unable to load file: " + path });
        }
        return false;
    }
}
