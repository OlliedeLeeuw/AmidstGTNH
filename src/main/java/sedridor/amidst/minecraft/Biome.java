package sedridor.amidst.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.minecraft.world.biome.BiomeGenBase;

import climateControl.api.BiomeSettings;
import climateControl.api.ClimateControlSettings;
import sedridor.amidst.Options;
import sedridor.amidst.Util;
import sedridor.forgeamidst.ForgeAmidst;

public class Biome {

    public static final HashMap<String, Biome> biomeMap = new HashMap<String, Biome>();

    public static final Height Default = new Height(0.1F, 0.2F);

    public static final Height ShallowWaters = new Height(-0.5F, 0.0F);

    public static final Height Oceans = new Height(-1.0F, 0.1F);

    public static final Height DeepOceans = new Height(-1.8F, 0.1F);

    public static final Height LowPlains = new Height(0.125F, 0.05F);

    public static final Height MidPlains = new Height(0.2F, 0.2F);

    public static final Height LowHills = new Height(0.45F, 0.3F);

    public static final Height HighPlateaus = new Height(1.5F, 0.025F);

    public static final Height MidHills = new Height(1.0F, 0.5F);

    public static final Height Shores = new Height(0.0F, 0.025F);

    public static final Height RockyWaters = new Height(0.1F, 0.8F);

    public static final Height LowIslands = new Height(0.2F, 0.3F);

    public static final Height PartiallySubmerged = new Height(-0.2F, 0.1F);

    public static final HashMap<String, Climate> climateMap = new LinkedHashMap<String, Climate>();

    public static final Climate[] climates = new Climate[256];

    public static final Climate SNOWY = new Climate("SNOWY", Util.makeColor(255, 255, 255), 1);

    public static final Climate COOL = new Climate("COOL", Util.makeColor(42, 107, 79), 2);

    public static final Climate WARM = new Climate("WARM", Util.makeColor(250, 222, 85), 4);

    public static final Climate HOT = new Climate("HOT", Util.makeColor(185, 99, 44), 8);

    public static final Climate MEDIUM = new Climate("MEDIUM", Util.makeColor(105, 155, 12), 6);

    public static final Climate PLAINS = new Climate("PLAINS", Util.makeColor(121, 179, 96), 14);

    public static final Climate OCEAN = new Climate("OCEAN", Util.makeColor(0, 0, 112), 16);

    public static final Climate DEEP_OCEAN = new Climate("DEEP_OCEAN", Util.makeColor(0, 0, 78), 32);

    public static final Climate LAND = new Climate("LAND", Util.makeColor(120, 142, 120), 15);

    public static final Climate WATER = new Climate("WATER", Util.makeColor(0, 0, 255), 64);

    public static final Climate DEFAULT = new Climate("DEFAULT", Util.makeColor(120, 142, 120), 128);

    public static final Climate UNKNOWN = new Climate("UNKNOWN", Util.makeColor(0, 0, 0), 0);

    public static final Biome[] biomes = new Biome[256];

    public static final Biome ocean = new Biome("Ocean", 0, Util.makeColor(0, 0, 112), Oceans, OCEAN);

    public static final Biome plains = new Biome("Plains", 1, Util.makeColor(121, 179, 96), Default, PLAINS);

    public static final Biome desert = new Biome("Desert", 2, Util.makeColor(247, 233, 163), LowPlains, HOT);

    public static final Biome extremeHills = new Biome(
        "Extreme Hills",
        3,
        Util.makeColor(120, 142, 120),
        MidHills,
        MEDIUM);

    public static final Biome forest = new Biome("Forest", 4, Util.makeColor(5, 102, 32), Default, MEDIUM);

    public static final Biome taiga = new Biome("Taiga", 5, Util.makeColor(42, 107, 79), MidPlains, COOL);

    public static final Biome swampland = new Biome(
        "Swampland",
        6,
        Util.makeColor(46, 60, 22),
        PartiallySubmerged,
        WARM);

    public static final Biome river = new Biome("River", 7, Util.makeColor(0, 0, 255), ShallowWaters);

    public static final Biome frozenOcean = new Biome("Frozen Ocean", 10, Util.makeColor(128, 128, 192), Oceans, OCEAN);

    public static final Biome frozenRiver = new Biome("Frozen River", 11, Util.makeColor(160, 160, 255), ShallowWaters);

    public static final Biome icePlains = new Biome("Ice Plains", 12, Util.makeColor(255, 255, 255), LowPlains, SNOWY);

    public static final Biome iceMountains = new Biome(
        "Ice Mountains",
        13,
        Util.makeColor(220, 220, 220),
        LowHills,
        SNOWY);

    public static final Biome mushroomIsland = new Biome(
        "Mushroom Island",
        14,
        Util.makeColor(120, 90, 150),
        LowIslands);

    public static final Biome mushroomIslandShore = new Biome(
        "Mushroom Island Shore",
        15,
        Util.makeColor(105, 78, 130),
        Shores);

    public static final Biome beach = new Biome("Beach", 16, Util.makeColor(250, 222, 85), Shores);

    public static final Biome desertHills = new Biome("Desert Hills", 17, Util.makeColor(214, 199, 141), LowHills, HOT);

    public static final Biome forestHills = new Biome("Forest Hills", 18, Util.makeColor(4, 84, 28), LowHills, MEDIUM);

    public static final Biome taigaHills = new Biome("Taiga Hills", 19, Util.makeColor(33, 84, 62), LowHills, COOL);

    public static final Biome extremeHillsEdge = new Biome(
        "Extreme Hills Edge",
        20,
        Util.makeColor(120, 132, 120),
        MidHills.getExtreme(),
        MEDIUM);

    public static final Biome jungle = new Biome("Jungle", 21, Util.makeColor(121, 178, 14), Default, WARM);

    public static final Biome jungleHills = new Biome("Jungle Hills", 22, Util.makeColor(105, 155, 12), LowHills, WARM);

    public static final Biome jungleEdge = new Biome("Jungle Edge", 23, Util.makeColor(98, 140, 24), Default, WARM);

    public static final Biome deepOcean = new Biome("Deep Ocean", 24, Util.makeColor(0, 0, 78), DeepOceans, DEEP_OCEAN);

    public static final Biome stoneBeach = new Biome("Stone Beach", 25, Util.makeColor(162, 162, 132), RockyWaters);

    public static final Biome coldBeach = new Biome("Cold Beach", 26, Util.makeColor(250, 240, 192), Shores);

    public static final Biome birchForest = new Biome("Birch Forest", 27, Util.makeColor(74, 125, 70), Default, MEDIUM);

    public static final Biome birchForestHills = new Biome(
        "Birch Forest Hills",
        28,
        Util.makeColor(60, 99, 56),
        LowHills,
        MEDIUM);

    public static final Biome roofedForest = new Biome(
        "Roofed Forest",
        29,
        Util.makeColor(64, 94, 26),
        Default,
        MEDIUM);

    public static final Biome coldTaiga = new Biome("Cold Taiga", 30, Util.makeColor(206, 220, 206), MidPlains, SNOWY);

    public static final Biome coldTaigaHills = new Biome(
        "Cold Taiga Hills",
        31,
        Util.makeColor(192, 206, 192),
        LowHills,
        SNOWY);

    public static final Biome megaTaiga = new Biome("Mega Taiga", 32, Util.makeColor(49, 85, 74), MidPlains, COOL);

    public static final Biome megaTaigaHills = new Biome(
        "Mega Taiga Hills",
        33,
        Util.makeColor(36, 63, 54),
        LowHills,
        COOL);

    public static final Biome extremeHillsPlus = new Biome(
        "Extreme Hills+",
        34,
        Util.makeColor(136, 156, 136),
        MidHills,
        MEDIUM);

    public static final Biome savanna = new Biome("Savanna", 35, Util.makeColor(178, 190, 95), LowPlains, HOT);

    public static final Biome savannaPlateau = new Biome(
        "Savanna Plateau",
        36,
        Util.makeColor(157, 167, 84),
        HighPlateaus,
        HOT);

    public static final Biome mesa = new Biome("Mesa", 37, Util.makeColor(185, 99, 44), Default, HOT);

    public static final Biome mesaPlateauF = new Biome(
        "Mesa Plateau F",
        38,
        Util.makeColor(135, 94, 36),
        HighPlateaus,
        HOT);

    public static final Biome mesaPlateau = new Biome(
        "Mesa Plateau",
        39,
        Util.makeColor(170, 90, 41),
        HighPlateaus,
        HOT);

    public static final Biome sunflowerPlains = new Biome(
        "Sunflower Plains",
        129,
        Util.makeColor(142, 207, 111),
        MEDIUM);

    public static final Biome desertM = new Biome("Desert M", 130, Util.makeColor(255, 247, 174), HOT);

    public static final Biome extremeHillsM = new Biome("Extreme Hills M", 131, Util.makeColor(91, 102, 91), MEDIUM);

    public static final Biome flowerForest = new Biome("Flower Forest", 132, Util.makeColor(46, 142, 74), MEDIUM);

    public static final Biome taigaM = new Biome("Taiga M", 133, Util.makeColor(50, 142, 28), COOL);

    public static final Biome swamplandM = new Biome("Swampland M", 134, Util.makeColor(36, 46, 14), WARM);

    public static final Biome icePlainsSpikes = new Biome(
        "Ice Plains Spikes",
        140,
        Util.makeColor(190, 232, 232),
        SNOWY);

    public static final Biome jungleM = new Biome("Jungle M", 149, Util.makeColor(123, 163, 49), WARM);

    public static final Biome jungleEdgeM = new Biome("Jungle Edge M", 151, Util.makeColor(140, 179, 64), WARM);

    public static final Biome birchForestM = new Biome("Birch Forest M", 155, Util.makeColor(80, 135, 76), MEDIUM);

    public static final Biome birchForestHillsM = new Biome(
        "Birch Forest Hills M",
        156,
        Util.makeColor(64, 107, 61),
        MEDIUM);

    public static final Biome roofedForestM = new Biome("Roofed Forest M", 157, Util.makeColor(72, 104, 29), MEDIUM);

    public static final Biome coldTaigaM = new Biome("Cold Taiga M", 158, Util.makeColor(178, 192, 178), COOL);

    public static final Biome megaSpruceTaiga = new Biome("Mega Spruce Taiga", 160, Util.makeColor(43, 74, 64), COOL);

    public static final Biome megaSpurceTaigaHills = new Biome(
        "Mega Spruce Taiga (Hills)",
        161,
        Util.makeColor(32, 60, 52),
        COOL);

    public static final Biome extremeHillsPlusM = new Biome(
        "Extreme Hills+ M",
        162,
        Util.makeColor(109, 121, 109),
        MEDIUM);

    public static final Biome savannaM = new Biome("Savanna M", 163, Util.makeColor(141, 150, 75), HOT);

    public static final Biome savannaPlateauM = new Biome("Savanna Plateau M", 164, Util.makeColor(127, 135, 70), HOT);

    public static final Biome mesaBryce = new Biome("Mesa (Bryce)", 165, Util.makeColor(221, 117, 55), HOT);

    public static final Biome mesaPlateauFM = new Biome("Mesa Plateau F M", 166, Util.makeColor(124, 64, 29), HOT);

    public static final Biome mesaPlateauM = new Biome("Mesa Plateau M", 167, Util.makeColor(150, 79, 36), HOT);

    public String name;

    public int index;

    public int color;

    public Height height;

    public Climate climate;

    public Biome(String name, int index, int color) {
        this(name, index, color, (biomes[index - 128]).height.getRare());
    }

    public Biome(String name, int index, int color, Height height) {
        this(name, index, color, height, UNKNOWN);
    }

    public Biome(String name, int index, int color, Climate climate) {
        this(name, index, color, (biomes[index - 128]).height.getRare(), climate);
    }

    public Biome(String name, int index, int color, Height height, Climate climate) {
        biomes[index] = this;
        this.name = name;
        this.index = index;
        this.color = color;
        this.height = height;
        this.climate = climate;
        biomeMap.put(name, this);
        if (index >= 128 && biomes[index - 128] != null && (biomes[index - 128]).color == color)
            this.color = Util.lightenColor(color, 40);
    }

    public String toString() {
        return "[Biome " + this.name + "]";
    }

    public int getBiomeId() {
        return this.index;
    }

    public String getBiomeName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }

    public Climate getClimate() {
        return this.climate;
    }

    public int getClimateColor() {
        if (this.climate != UNKNOWN && this.climate != WATER) return this.climate.color;
        if (this.name.endsWith("River") || this.name.equals("Frozen Ocean")
            || this.name.equals("Kelp Forest")
            || this.name.equals("Coral Reef")
            || this.name.startsWith("Mushroom Island")) return this.color;
        if (this.name.endsWith("Beach")) return LAND.color;
        return this.climate.color;
    }

    public static int indexFromName(String name) {
        Biome biome = biomeMap.get(name);
        if (biome != null) return biome.index;
        return -1;
    }

    public static BiomeGenBase getBiomeGenBase(int index) {
        Biome biome = biomes[index];
        if (biome != null) return BiomeGenBase.getBiome(index);
        return null;
    }

    public static final class Height {

        public float baseHeight;

        public float variation;

        public Height(float baseHeight, float variation) {
            this.baseHeight = baseHeight;
            this.variation = variation;
        }

        public Height getExtreme() {
            return new Height(this.baseHeight * 0.8F, this.variation * 0.6F);
        }

        public Height getRare() {
            return new Height(this.baseHeight + 0.1F, this.variation + 0.2F);
        }
    }

    public static final class Climate {

        public String name;

        public int index;

        public int color;

        public Climate(String name, int color, int climate) {
            Biome.climates[climate] = this;
            this.name = name;
            this.color = color;
            this.index = climate;
            Biome.climateMap.put(name, this);
        }

        public int getId() {
            return this.index;
        }

        public String getClimateName() {
            return Util.capitalizeString(this.name);
        }

        public int getColor() {
            return this.color;
        }

        public int getClimateColor() {
            return this.color;
        }

        public Climate getOceanic() {
            return Biome.OCEAN;
        }

        public boolean getIsPureClimate() {
            return (this == Biome.SNOWY);
        }

        public String toString() {
            return "[Climate " + this.name + "]";
        }

        public static Climate climateFromName(String name) {
            Climate climate = Biome.climateMap.get(name);
            if (climate != null) return climate;
            return Biome.UNKNOWN;
        }

        public static Climate climateFromId(int index) {
            Climate climate = Biome.climates[index];
            if (climate != null) return climate;
            return Biome.UNKNOWN;
        }

        private static Climate getDefaultClimate(int index) {
            BiomeGenBase.TempCategory temp = null;
            BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[index];
            if (biome != null) {
                temp = biome.getTempCategory();
                if (temp == BiomeGenBase.TempCategory.COLD) return Biome.SNOWY;
                if (temp == BiomeGenBase.TempCategory.MEDIUM) return Biome.MEDIUM;
                if (temp == BiomeGenBase.TempCategory.WARM) return Biome.HOT;
            }
            return Biome.UNKNOWN;
        }

        public static void updateClimates() {
            if (ForgeAmidst.climateControlFound() && Options.instance.mapType.get()
                .equals("Climate Control")) {
                try {
                    ClimateControlSettings dimensionalSettings = (ClimateControlSettings) ForgeAmidst.getInstance()
                        .getDimensionalSettings(0);
                    ArrayList<BiomeSettings> biomeSettings = dimensionalSettings.biomeSettings();
                    for (BiomeSettings setting : biomeSettings) {
                        if (setting.biomesAreActive()) {
                            ArrayList biomeSettingIds = ForgeAmidst.getInstance()
                                .getBiomeSettingIds(setting);
                            for (BiomeSettings.ID biome : (Iterable<BiomeSettings.ID>) biomeSettingIds) {
                                int biomeId = (Integer) biome.biomeID()
                                    .value();
                                if (biomeId > -1 && biomeId < Biome.biomes.length && Biome.biomes[biomeId] != null)
                                    try {
                                        (Biome.biomes[biomeId]).climate = climateFromName(
                                            biome.distribution()
                                                .name());
                                    } catch (Exception e) {}
                            }
                        }
                    }
                } catch (Exception e) {}
                for (Biome biome : Biome.biomes) {
                    if (biome != null) {
                        String biomeName = biome.getBiomeName();
                        Climate climate = biome.getClimate();
                        if (biomeName.endsWith("River")) {
                            biome.climate = Biome.WATER;
                        } else if (biomeName.equals("Frozen Ocean") || biomeName.equals("Kelp Forest")
                            || biomeName.equals("Coral Reef")) {
                                biome.climate = Biome.OCEAN;
                            } else if (biomeName.equals("Boreal Forest") || biomeName.equals("Tundra")
                                || biomeName.equals("Shield")
                                || biomeName.equals("Coniferous Forest")) {
                                    biome.climate = Biome.COOL;
                                } else if (biomeName.equals("Seasonal Forest Clearing")
                                    && Biome.indexFromName("Seasonal Forest") > -1) {
                                        biome.climate = Biome.biomes[Biome.indexFromName("Seasonal Forest")]
                                            .getClimate();
                                    } else if (biomeName.endsWith("Beach")) {
                                        biome.climate = Biome.LAND;
                                    } else if (biomeName.startsWith("Mushroom Island")) {
                                        biome.climate = Biome.UNKNOWN;
                                    } else if (climate == Biome.UNKNOWN
                                        && ((biome.getBiomeId() >= 40 && biome.getBiomeId() < 128)
                                            || biome.getBiomeId() > 167)) {
                                                climate = getDefaultClimate(biome.getBiomeId());
                                                biome.climate = climate;
                                            }
                    }
                }
            } else {
                for (Biome biome : Biome.biomes) {
                    if (biome != null) {
                        String biomeName = biome.getBiomeName();
                        Climate climate = biome.getClimate();
                        if (biomeName.endsWith("River")) {
                            biome.climate = Biome.WATER;
                        } else if (biomeName.equals("Frozen Ocean") || biomeName.equals("Kelp Forest")
                            || biomeName.equals("Coral Reef")) {
                                biome.climate = Biome.OCEAN;
                            } else if (biomeName.equals("Boreal Forest") || biomeName.equals("Tundra")
                                || biomeName.equals("Shield")
                                || biomeName.equals("Coniferous Forest")) {
                                    biome.climate = Biome.COOL;
                                } else if (biomeName.equals("Bamboo Forest")) {
                                    biome.climate = Biome.WARM;
                                } else if (biomeName.endsWith("Beach")) {
                                    biome.climate = Biome.LAND;
                                } else if (biomeName.startsWith("Mushroom Island")) {
                                    biome.climate = Biome.UNKNOWN;
                                } else if (climate == Biome.UNKNOWN
                                    || (biome.getBiomeId() >= 40 && biome.getBiomeId() < 128)
                                    || biome.getBiomeId() > 167) {
                                        biome.climate = getDefaultClimate(biome.getBiomeId());
                                    }
                    }
                }
            }
        }
    }

    static {
        BiomeGenBase[] biomeList = BiomeGenBase.getBiomeGenArray();
        for (BiomeGenBase biome : biomeList) {
            if (biome != null && biomes[biome.biomeID] == null && biome.biomeID >= 40) {
                try {
                    String biomeClass = biome.getClass()
                        .getName();
                    if (biomeClass.equals("biomesoplenty.common.biome.BOPNetherBiome")
                        || biomeClass.equals("biomesoplenty.common.biome.BOPEndBiome")) continue;
                } catch (NoClassDefFoundError e) {}
                int color = Util.mcColor(biome.color);
                if (biome.biomeName.equals("Lush River")) {
                    color = Util.makeColor(0, 128, 255);
                } else if (biome.biomeName.equals("Dry River")) {
                    color = Util.makeColor(96, 128, 255);
                } else if (biome.biomeName.equals("Kelp Forest")) {
                    color = Util.makeColor(0, 50, 89);
                } else if (biome.biomeName.equals("Coral Reef")) {
                    color = Util.makeColor(0, 41, 89);
                } else if (biome.biomeName.equals("Gravel Beach")) {
                    color = Util.makeColor(144, 136, 132);
                } else if (biome.biomeName.equals("Mountain Foothills")) {
                    color = Util.makeColor(128, 136, 105);
                } else if (biome.biomeName.equals("Mountain Peaks")) {
                    color = Util.makeColor(128, 127, 105);
                } else if (biome.biomeName.equals("Mountain")) {
                    color = Util.makeColor(128, 136, 105);
                }
                if (indexFromName(biome.biomeName) > -1) {
                    new Biome(
                        biome.biomeName + " " + biome.biomeID,
                        biome.biomeID,
                        color,
                        new Height(biome.rootHeight, biome.heightVariation));
                } else {
                    new Biome(
                        biome.biomeName,
                        biome.biomeID,
                        color,
                        new Height(biome.rootHeight, biome.heightVariation));
                }
            }
        }
    }
}
