package sedridor.forgeamidst;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import rtg.util.CellNoise;
import rtg.util.OpenSimplexNoise;
import rtg.util.PlaneLocation;
import rtg.util.SimplexCellularNoise;
import rtg.util.TimedHashMap;
import rtg.world.biome.BiomeAnalyzer;
import rtg.world.biome.RTGBiomeProvider;
import rtg.world.biome.realistic.RealisticBiomeBase;
import rtg.world.biome.realistic.RealisticBiomePatcher;
import rtg.world.gen.ChunkLandscape;

public class BiomeProviderRTG {

    private final int sampleSize = 8;

    private final int sampleArraySize;

    private int[] biomeData;

    private float[][] weightings;

    private float[] weightedBiomes = new float[(BiomeGenBase.getBiomeGenArray()).length];

    private BiomeAnalyzer analyzer = new BiomeAnalyzer();

    private int[] xyinverted = this.analyzer.xyinverted();

    private TimedHashMap<PlaneLocation, ChunkLandscape> storage = new TimedHashMap(60000);

    private World worldObj;

    protected RTGBiomeProvider cmr;

    private OpenSimplexNoise simplex;

    private CellNoise cell;

    private BiomeGenBase[] baseBiomesList;

    private RealisticBiomePatcher biomePatcher;

    public BiomeProviderRTG(World world, long seed) {
        this.worldObj = world;
        this.cmr = (RTGBiomeProvider) this.worldObj.getWorldChunkManager();
        this.simplex = new OpenSimplexNoise(seed);
        this.cell = new SimplexCellularNoise(seed);
        this.baseBiomesList = new BiomeGenBase[256];
        this.biomePatcher = new RealisticBiomePatcher();
        this.sampleArraySize = 21;
        this.biomeData = new int[this.sampleArraySize * this.sampleArraySize];
        setWeightings();
    }

    public int[] provideInts(int cx, int cy, int width, int height) {
        ChunkLandscape landscape = landscape(this.cmr, cx * 16, cy * 16);
        for (int i = 0; i < 256; i++) {
            try {
                this.baseBiomesList[i] = (landscape.biome[i]).baseBiome;
            } catch (Exception e) {
                this.baseBiomesList[i] = this.biomePatcher
                    .getPatchedBaseBiome("" + (landscape.biome[i]).baseBiome.biomeID);
            }
        }
        int[] biomeIndices = new int[256];
        for (int k = 0; k < biomeIndices.length; k++)
            biomeIndices[k] = (this.baseBiomesList[this.xyinverted[k]]).biomeID;
        return biomeIndices;
    }

    private void setWeightings() {
        this.weightings = new float[this.sampleArraySize * this.sampleArraySize][256];
        int adjustment = 4;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int locationIndex = (i + adjustment) * 25 + j + adjustment;
                float totalWeight = 0.0F;
                float limit = (float) Math.pow(3136.0D, 0.699999988079071D);
                for (int mapX = 0; mapX < this.sampleArraySize; mapX++) {
                    for (int mapZ = 0; mapZ < this.sampleArraySize; mapZ++) {
                        float xDist = (float) (i - chunkCoordinate(mapX));
                        float yDist = (float) (j - chunkCoordinate(mapZ));
                        float distanceSquared = xDist * xDist + yDist * yDist;
                        float distance = (float) Math.pow((double) distanceSquared, 0.699999988079071D);
                        float weight = 1.0F - distance / limit;
                        if (weight < 0.0F) weight = 0.0F;
                        this.weightings[mapX * this.sampleArraySize + mapZ][i * 16 + j] = weight;
                    }
                }
            }
        }
    }

    private int chunkCoordinate(int biomeMapCoordinate) {
        return (biomeMapCoordinate - 8) * 8;
    }

    public int getBiomeDataAt(RTGBiomeProvider cmr, int worldX, int worldY) {
        int chunkX = worldX & 0xF;
        int chunkY = worldY & 0xF;
        ChunkLandscape target = landscape(cmr, worldX - chunkX, worldY - chunkY);
        return (target.biome[chunkX * 16 + chunkY]).baseBiome.biomeID;
    }

    public synchronized ChunkLandscape landscape(RTGBiomeProvider cmr, int worldX, int worldY) {
        PlaneLocation.Invariant invariant = new PlaneLocation.Invariant(worldX, worldY);
        ChunkLandscape preExisting = (ChunkLandscape) this.storage.get(invariant);
        if (preExisting != null) return preExisting;
        ChunkLandscape result = new ChunkLandscape();
        getNewerNoise(cmr, worldX, worldY, result);
        int[] biomeIndices = cmr.getBiomesGens(worldX, worldY, 16, 16);
        getClass();
        this.analyzer.newRepair(biomeIndices, result.biome, this.biomeData, 8, result.noise, result.river);
        this.storage.put(invariant, result);
        return result;
    }

    private synchronized void getNewerNoise(RTGBiomeProvider cmr, int x, int y, ChunkLandscape landscape) {
        for (int i = -8; i < 13; i++) {
            for (int m = -8; m < 13; m++) this.biomeData[(i + 8) * this.sampleArraySize + m
                + 8] = (cmr.getBiomeDataAt(x + i * 8, y + m * 8)).baseBiome.biomeID;
        }
        int adjustment = 4;
        for (int k = 0; k < 16; k++) {
            for (int m = 0; m < 16; m++) {
                float totalWeight = 0.0F;
                for (int mapX = 0; mapX < this.sampleArraySize; mapX++) {
                    for (int mapZ = 0; mapZ < this.sampleArraySize; mapZ++) {
                        float weight = this.weightings[mapX * this.sampleArraySize + mapZ][k * 16 + m];
                        if (weight > 0.0F) {
                            totalWeight += weight;
                            this.weightedBiomes[this.biomeData[mapX * this.sampleArraySize
                                + mapZ]] = this.weightedBiomes[this.biomeData[mapX * this.sampleArraySize + mapZ]]
                                    + weight;
                        }
                    }
                }
                for (int biomeIndex = 0; biomeIndex < this.weightedBiomes.length; biomeIndex++)
                    this.weightedBiomes[biomeIndex] = this.weightedBiomes[biomeIndex] / totalWeight;
                landscape.noise[k * 16 + m] = 0.0F;
                float river = cmr.getRiverStrength(x + k, y + m);
                landscape.river[k * 16 + m] = -river;
                float totalBorder = 0.0F;
                for (int n = 0; n < 256; n++) {
                    if (this.weightedBiomes[n] > 0.0F) {
                        totalBorder += this.weightedBiomes[n];
                        landscape.noise[k * 16 + m] = landscape.noise[k * 16 + m] + RealisticBiomeBase.getBiome(n)
                            .rNoise(this.simplex, this.cell, x + k, y + m, this.weightedBiomes[n], river + 1.0F)
                            * this.weightedBiomes[n];
                        this.weightedBiomes[n] = 0.0F;
                    }
                }
                if ((double) totalBorder < 0.999D || (double) totalBorder > 1.001D)
                    throw new RuntimeException("" + totalBorder);
            }
        }
        for (int j = 0; j < 16; j++) {
            for (int m = 0; m < 16; m++)
                landscape.biome[j * 16 + m] = cmr.getBiomeDataAt(x + (j - 7) * 8 + 4, y + (m - 7) * 8 + 4);
        }
    }
}
