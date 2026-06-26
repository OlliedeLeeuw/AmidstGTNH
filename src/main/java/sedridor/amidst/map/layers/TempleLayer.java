package sedridor.amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import sedridor.amidst.Options;
import sedridor.amidst.map.Fragment;
import sedridor.amidst.map.IconLayer;
import sedridor.amidst.map.MapObjectDesertTemple;
import sedridor.amidst.map.MapObjectIgloo;
import sedridor.amidst.map.MapObjectJungleTemple;
import sedridor.amidst.map.MapObjectWitchHut;
import sedridor.amidst.minecraft.Biome;
import sedridor.amidst.minecraft.MinecraftUtil;
import sedridor.amidst.version.VersionInfo;

public class TempleLayer extends IconLayer {

    public static final boolean showIgloosOverride = true;

    public static List<Biome> validBiomes;

    private Random random = new Random();

    public TempleLayer() {
        validBiomes = getValidBiomes();
    }

    public boolean isVisible() {
        return Options.instance.showTemples.get();
    }

    public void generateMapObjects(Fragment frag) {
        int size = 32;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int chunkX = x + frag.getChunkX();
                int chunkY = y + frag.getChunkY();
                if (checkChunk(chunkX, chunkY)) {
                    String biomeName = BiomeLayer.getBiomeNameForFragment(frag, (x << 4) + 8, (y << 4) + 8);
                    if (biomeName.equals("Swampland")) {
                        frag.addObject(new MapObjectWitchHut((x << 4) + 8, (y << 4) + 8).setParent(this));
                    } else if (biomeName.equals("Jungle") || biomeName.equals("Jungle Hills")) {
                        frag.addObject(new MapObjectJungleTemple((x << 4) + 8, (y << 4) + 8).setParent(this));
                    } else if (biomeName.equals("Desert") || biomeName.equals("Desert Hills")) {
                        frag.addObject(new MapObjectDesertTemple((x << 4) + 8, (y << 4) + 8).setParent(this));
                    } else if (biomeName.equals("Ice Plains") || biomeName.equals("Cold Taiga")) {
                        frag.addObject(new MapObjectIgloo((x << 4) + 8, (y << 4) + 8).setParent(this));
                    }
                }
            }
        }
    }

    public List<Biome> getValidBiomes() {
        if (!MinecraftUtil.getVersion()
            .isAtLeast(VersionInfo.V1_9));
        Biome[] validBiomes = { Biome.desert, Biome.desertHills, Biome.jungle, Biome.jungleHills, Biome.swampland,
            Biome.icePlains, Biome.coldTaiga };
        return Arrays.<Biome>asList(validBiomes);
    }

    public boolean checkChunk(int chunkX, int chunkY) {
        int maxDistanceBetweenScatteredFeatures = 32;
        int minDistanceBetweenScatteredFeatures = 8;
        int k = chunkX;
        int m = chunkY;
        if (chunkX < 0) chunkX -= maxDistanceBetweenScatteredFeatures - 1;
        if (chunkY < 0) chunkY -= maxDistanceBetweenScatteredFeatures - 1;
        int n = chunkX / maxDistanceBetweenScatteredFeatures;
        int i1 = chunkY / maxDistanceBetweenScatteredFeatures;
        long l1 = (long) n * 341873128712L + (long) i1 * 132897987541L + Options.instance.seed + 14357617L;
        this.random.setSeed(l1);
        n *= maxDistanceBetweenScatteredFeatures;
        i1 *= maxDistanceBetweenScatteredFeatures;
        n += this.random.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
        i1 += this.random.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
        return (k == n && m == i1 && MinecraftUtil.isValidBiome(k * 16 + 8, m * 16 + 8, 0, validBiomes));
    }
}
