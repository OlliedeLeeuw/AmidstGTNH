package sedridor.amidst.minecraft;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import sedridor.amidst.logging.Log;
import sedridor.amidst.version.VersionInfo;

public class MinecraftUtil {

    private static MinecraftInterface minecraftInterface;

    public static int[] getBiomeData(int x, int y, int width, int height, boolean useQuarterResolutionMap) {
        return minecraftInterface.getBiomeData(x, y, width, height, useQuarterResolutionMap);
    }

    public static Point findValidLocation(int searchX, int searchY, int size, List<Biome> paramList, Random random) {
        int x1 = searchX - size >> 2;
        int y1 = searchY - size >> 2;
        int x2 = searchX + size >> 2;
        int y2 = searchY + size >> 2;
        int width = x2 - x1 + 1;
        int height = y2 - y1 + 1;
        int[] arrayOfInt = getBiomeData(x1, y1, width, height, true);
        Point location = null;
        int numberOfValidFound = 0;
        for (int i = 0; i < width * height; i++) {
            int x = x1 + i % width << 2;
            int y = y1 + i / width << 2;
            if (arrayOfInt[i] > Biome.biomes.length) Log.crash("Unsupported biome type detected");
            Biome localBiome = Biome.biomes[arrayOfInt[i]];
            if (paramList.contains(localBiome) && (location == null || random.nextInt(numberOfValidFound + 1) == 0)) {
                location = new Point(x, y);
                numberOfValidFound++;
            }
        }
        return location;
    }

    public static boolean isValidBiome(int x, int y, int size, List<Biome> validBiomes) {
        int x1 = x - size >> 2;
        int y1 = y - size >> 2;
        int x2 = x + size >> 2;
        int y2 = y + size >> 2;
        int width = x2 - x1 + 1;
        int height = y2 - y1 + 1;
        int[] arrayOfInt = getBiomeData(x1, y1, width, height, true);
        for (int i = 0; i < width * height; i++) {
            Biome localBiome = Biome.biomes[arrayOfInt[i]];
            if (!validBiomes.contains(localBiome)) return false;
        }
        return true;
    }

    public static Biome getBiomeAt(int x, int y) {
        int[] arrayOfInt = getBiomeData(x, y, 1, 1, false);
        return Biome.biomes[arrayOfInt[0] & 0xFF];
    }

    public static void createWorld(long seed, String type) {
        minecraftInterface.createWorld(seed, type, "");
    }

    public static void createWorld(long seed, String type, String generatorOptions) {
        minecraftInterface.createWorld(seed, type, generatorOptions);
    }

    public static void setBiomeInterface(MinecraftInterface biomeInterface) {
        minecraftInterface = biomeInterface;
    }

    public static VersionInfo getVersion() {
        return minecraftInterface.getVersion();
    }

    public static boolean hasInterface() {
        return (minecraftInterface != null);
    }
}
