package sedridor.amidst.minecraft;

import java.io.File;
import java.util.HashMap;

import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import sedridor.amidst.Options;
import sedridor.amidst.logging.Log;
import sedridor.amidst.project.FinderWindow;
import sedridor.amidst.project.SaveLoader;
import sedridor.amidst.version.VersionInfo;
import sedridor.forgeamidst.BiomeProviderRTG;
import sedridor.forgeamidst.ForgeAmidst;
import sedridor.forgeamidst.MapWorld;
import sedridor.forgeamidst.MapWorldRTG;

public class MinecraftInterface {

    private File jarFile;

    protected String versionId = "";

    protected VersionInfo version = VersionInfo.unknown;

    private static long worldSeed;

    private static String worldType;

    private static GenLayer biomeGen;

    private static GenLayer biomeIndexLayer;

    private static HashMap<String, int[]> chunkMap = new HashMap<String, int[]>();

    private static int[] gridPoints = new int[] { 0, 4, 8, 12, 64, 68, 72, 76, 128, 132, 136, 140, 192, 196, 200, 204 };

    public static BiomeProviderRTG biomePatcher;

    public MinecraftInterface(File jarFile) {
        this.jarFile = jarFile;
        Log.i(new Object[] { "Generating version ID..." });
        this.versionId = "V" + "1.7.10".replace(".", "_");
        this.version = VersionInfo.forName(this.versionId);
        Log.i(
            new Object[] {
                "Identified Minecraft [" + this.version.toString() + "] with versionID of " + this.versionId });
    }

    public int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolutionMap) {
        if (Options.instance.rtgHighResolution.get() && worldType.equals("RTG")
            && ForgeAmidst.rtgFound()
            && width == 128
            && height == 128) {
            int cX = x >> 2;
            int cY = y >> 2;
            String chunkId = cX + ":" + cY;
            if (chunkMap.containsKey(chunkId)) return chunkMap.get(chunkId);
            int[] ints = new int[width * height];
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++) {
                    int[] biomes = biomePatcher.provideInts(cX + i, cY + j, width, height);
                    for (int k = 0; k < gridPoints.length; k++)
                        ints[(k & 0x3) + (k >> 2) * 128 + i * 4 + j * 128 * 4] = biomes[gridPoints[k] + 17];
                }
            }
            chunkMap.put(chunkId, ints);
            return ints;
        }
        IntCache.resetIntCache();
        return (useQuarterResolutionMap ? biomeGen : biomeIndexLayer).getInts(x, y, width, height);
    }

    public static void setBiomeGen() {
        biomeGen = ForgeAmidst.biomeGen;
        biomeIndexLayer = ForgeAmidst.biomeIndexLayer;
    }

    public void createWorld(long seed, String typeName, String generatorOptions) {
        Log.debug(new Object[] { "Attempting to create world with seed: " + seed + ", type: " + typeName + "." });
        if (ForgeAmidst.biomeGen == null || ForgeAmidst.getServer() != null) {
            worldSeed = seed;
            MinecraftInterface.worldType = typeName;
            ForgeAmidst.getInstance()
                .serverAboutToStart(null);
            if (ForgeAmidst.getServer() == null && ((Options.instance.mapType.get()
                .equals("Climate Control") && ForgeAmidst.biomeGen == null && ForgeAmidst.climateControlFound())
                || WorldType.parseWorldType(typeName)
                    .getWorldTypeID() >= 4)) {
                SaveLoader.Type type = SaveLoader.Type.fromMixedCase(typeName);
                WorldType worldType = WorldType.worldTypes[type.getId()];
                startWorld(seed, worldType);
                biomeGen = ForgeAmidst.biomeGen;
                biomeIndexLayer = ForgeAmidst.biomeIndexLayer;
                if (typeName.equals("RTG") && ForgeAmidst.rtgFound()) {
                    ForgeAmidst.getInstance();
                    biomePatcher = new BiomeProviderRTG(ForgeAmidst.getWorld(), seed);
                }
            } else {
                SaveLoader.Type type = SaveLoader.Type.fromMixedCase(typeName);
                WorldType worldType = WorldType.worldTypes[type.getId()];
                GenLayer[] genLayers = GenLayer.initializeAllBiomeGenerators(seed, worldType);
                biomeGen = genLayers[0];
                biomeIndexLayer = genLayers[1];
            }
            FinderWindow.instance.menuBar.newMenu.setEnabled(true);
        } else {
            worldSeed = seed;
            MinecraftInterface.worldType = typeName;
            biomeGen = ForgeAmidst.biomeGen;
            biomeIndexLayer = ForgeAmidst.biomeIndexLayer;
            if (MinecraftInterface.worldType.equals("RTG") && ForgeAmidst.rtgFound()) {
                ForgeAmidst.getInstance();
                biomePatcher = new BiomeProviderRTG(ForgeAmidst.getWorld(), seed);
            }
        }
        Biome.Climate.updateClimates();
        chunkMap.clear();
    }

    private World startWorld(long seed, WorldType worldType) {
        WorldSettings par5WorldSettings = new WorldSettings(
            seed,
            WorldSettings.GameType.SURVIVAL,
            true,
            false,
            worldType);
        ForgeAmidst.setServer(new IntegratedServer(ForgeAmidst.getMC(), "MAPWORLD", "ForgeAmidst", par5WorldSettings));
        if (ForgeAmidst.climateControlFound())
            return new MapWorldRTG(ForgeAmidst.getServer(), seed, worldType, par5WorldSettings);
        return new MapWorld(ForgeAmidst.getServer(), seed, worldType, par5WorldSettings);
    }

    public VersionInfo getVersion() {
        return this.version;
    }
}
