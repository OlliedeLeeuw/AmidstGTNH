package sedridor.amidst;

import java.io.File;
import java.util.prefs.Preferences;

import sedridor.amidst.preferences.BiomeColorProfile;
import sedridor.amidst.preferences.BooleanPrefModel;
import sedridor.amidst.preferences.FilePrefModel;
import sedridor.amidst.preferences.SelectPrefModel;
import sedridor.amidst.preferences.StringPreference;

public enum Options {

    instance;

    public String minecraftPath = new File(".").getAbsolutePath();

    public String minecraftJar = new File(Util.minecraftDirectory, "versions/1.7.10/1.7.10.jar").getAbsolutePath();

    public long seed;

    public String seedText;

    public final BooleanPrefModel showSlimeChunks;

    public final BooleanPrefModel showGrid;

    public final BooleanPrefModel metricGrid;

    public final BooleanPrefModel showNetherFortresses;

    public final BooleanPrefModel showTemples;

    public final BooleanPrefModel showPlayers;

    public final BooleanPrefModel showStrongholds;

    public final BooleanPrefModel showOceanMonuments;

    public final BooleanPrefModel showVillages;

    public final BooleanPrefModel showSpawn;

    public final BooleanPrefModel hideObjects;

    public final BooleanPrefModel mapFlicking;

    public final BooleanPrefModel mapFading;

    public final BooleanPrefModel showBiomeIDs;

    public final BooleanPrefModel sortedBiomeList;

    public final BooleanPrefModel showFPS;

    public final BooleanPrefModel showDebug;

    public final BooleanPrefModel showClimate;

    public final BooleanPrefModel showScale;

    public final BooleanPrefModel showWorldTypeWidget;

    public final BooleanPrefModel maxZoom;

    public final BooleanPrefModel colorByClimate;

    public final BooleanPrefModel rtgHighResolution;

    public final BooleanPrefModel logToFile;

    public final FilePrefModel savesDir;

    public final StringPreference selectedColorProfile;

    public final SelectPrefModel worldType;

    public final SelectPrefModel mapType;

    public BiomeColorProfile biomeColorProfile;

    private Preferences preferences;

    Options() {
        this.seed = 0L;
        this.seedText = null;
        Preferences pref = Preferences.userNodeForPackage(Amidst.class);
        this.preferences = pref;
        this.showSlimeChunks = new BooleanPrefModel(pref, "slimeChunks", false);
        this.showGrid = new BooleanPrefModel(pref, "grid", true);
        this.metricGrid = new BooleanPrefModel(pref, "metricGrid", true);
        this.showNetherFortresses = new BooleanPrefModel(pref, "netherFortressIcons", false);
        this.mapFlicking = new BooleanPrefModel(pref, "mapFlicking", true);
        this.mapFading = new BooleanPrefModel(pref, "mapFading", true);
        this.maxZoom = new BooleanPrefModel(pref, "maxZoom", true);
        this.showStrongholds = new BooleanPrefModel(pref, "strongholdIcons", false);
        this.showPlayers = new BooleanPrefModel(pref, "playerIcons", true);
        this.showTemples = new BooleanPrefModel(pref, "templeIcons", true);
        this.showOceanMonuments = new BooleanPrefModel(pref, "oceanMonumentIcons", true);
        this.showVillages = new BooleanPrefModel(pref, "villageIcons", true);
        this.showSpawn = new BooleanPrefModel(pref, "spawnIcon", true);
        this.showFPS = new BooleanPrefModel(pref, "showFPS", false);
        this.showDebug = new BooleanPrefModel(pref, "showDebug", false);
        this.showClimate = new BooleanPrefModel(pref, "showClimate", false);
        this.showScale = new BooleanPrefModel(pref, "showScale", false);
        this.showWorldTypeWidget = new BooleanPrefModel(pref, "showWorldTypeWidget", true);
        this.selectedColorProfile = new StringPreference(pref, "profile", null);
        this.savesDir = new FilePrefModel(pref, "saveFolder", new File(".").getAbsoluteFile());
        this.hideObjects = new BooleanPrefModel(pref, "hideObjects", false);
        this.biomeColorProfile = new BiomeColorProfile();
        this.biomeColorProfile.fillColorArray();
        this.worldType = new SelectPrefModel(
            pref,
            "worldType",
            "Prompt each time",
            new String[] { "Prompt each time", "Default", "Large Biomes" });
        this.mapType = new SelectPrefModel(pref, "mapType", "Default", new String[] { "Default", "Climate Control" });
        this.colorByClimate = new BooleanPrefModel(pref, "colorByClimate", false);
        this.showBiomeIDs = new BooleanPrefModel(pref, "showBiomeIDs", false);
        this.sortedBiomeList = new BooleanPrefModel(pref, "sortedBiomeList", false);
        this.logToFile = new BooleanPrefModel(pref, "logToFile", false);
        this.rtgHighResolution = new BooleanPrefModel(pref, "rtgHighResolution", false);
    }

    public Preferences getPreferences() {
        return this.preferences;
    }

    public String getSeedText() {
        if (this.seedText == null) return "Seed: " + this.seed;
        return "Seed: \"" + this.seedText + "\" (" + this.seed + ")";
    }
}
