package sedridor.amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.MapObjectOceanMonument;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.preferences.BooleanPrefModel;
import sedridor.amidst.version.VersionInfo;

public class OceanMonumentLayer extends IconLayer {

    public static final boolean showOceanMonumentsOverride = true;

    private boolean useFixedAlogirithm = true;

    public static List<Biome> validBiomes = Arrays.<Biome>asList(Biome.deepOcean);

    public static List<Biome> validSurroundingBiomes = Arrays
        .<Biome>asList(Biome.ocean, Biome.deepOcean, Biome.frozenOcean, Biome.river, Biome.frozenRiver);

    private Random random = new Random();

    public OceanMonumentLayer() {
        if (MinecraftUtil.getVersion()
            .isAtLeast(VersionInfo.V1_9)) {
            this.useFixedAlogirithm = true;
        } else {
            this.useFixedAlogirithm = false;
        }
    }

    public static void initializeUIOptions(BooleanPrefModel oceanMonumentPrefModel) {
        if (!minecraftVersionSupportsOceanMonuments()) oceanMonumentPrefModel.setSelected(false);
    }

    public static boolean minecraftVersionSupportsOceanMonuments() {
        return MinecraftUtil.getVersion()
            .isAtLeast(VersionInfo.V1_8);
    }

    public boolean isVisible() {
        return Options.instance.showOceanMonuments.get();
    }

    public void generateMapObjects(Fragment frag) {
        int size = 32;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int chunkX = x + frag.getChunkX();
                int chunkY = y + frag.getChunkY();
                if (checkChunk(chunkX, chunkY))
                    frag.addObject(new MapObjectOceanMonument((x << 4) + 8, (y << 4) + 8).setParent(this));
            }
        }
    }

    public void setRandomSeed(int a, int b, int structureSeed) {
        long positionSeed = (long) a * 341873128712L + (long) b * 132897987541L
            + Options.instance.seed
            + (long) structureSeed;
        this.random.setSeed(positionSeed);
    }

    public boolean checkChunk(int chunkX, int chunkY) {
        boolean result = false;
        byte maxDistanceBetweenScatteredFeatures = 32;
        byte minDistanceBetweenScatteredFeatures = 5;
        int structureSize = 29;
        int structureCenterSize = 16;
        int structureSeed = 10387313;
        int chunkXadj = chunkX;
        int chunkYadj = chunkY;
        if (chunkXadj < 0) chunkXadj -= maxDistanceBetweenScatteredFeatures - 1;
        if (chunkYadj < 0) chunkYadj -= maxDistanceBetweenScatteredFeatures - 1;
        int i = chunkXadj / maxDistanceBetweenScatteredFeatures;
        int j = chunkYadj / maxDistanceBetweenScatteredFeatures;
        setRandomSeed(i, j, structureSeed);
        int distanceRange = maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures;
        i *= maxDistanceBetweenScatteredFeatures;
        j *= maxDistanceBetweenScatteredFeatures;
        i += (this.random.nextInt(distanceRange) + this.random.nextInt(distanceRange)) / 2;
        j += (this.random.nextInt(distanceRange) + this.random.nextInt(distanceRange)) / 2;
        if (chunkX == i && chunkY == j) if (this.useFixedAlogirithm) {
            result = (MinecraftUtil.isValidBiome(chunkX * 16 + 8, chunkY * 16 + 8, structureCenterSize, validBiomes)
                && MinecraftUtil.isValidBiome(chunkX * 16 + 8, chunkY * 16 + 8, structureSize, validSurroundingBiomes));
        } else {
            result = (MinecraftUtil.getBiomeAt(chunkX * 16 + 8, chunkY * 16 + 8) == Biome.deepOcean
                && MinecraftUtil.isValidBiome(chunkX * 16 + 8, chunkY * 16 + 8, structureSize, validSurroundingBiomes));
        }
        return result;
    }
}
