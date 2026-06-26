package sedridor.forgeamidst;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import climateControl.ClimateControl;
import climateControl.api.BiomeSettings;
import climateControl.api.ClimateControlSettings;
import climateControl.customGenLayer.GenLayerRiverMixWrapper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import rtg.RTG;
import rtg.world.biome.WorldChunkManagerRTG;
import sedridor.amidst.Amidst;
import sedridor.amidst.project.FinderWindow;
import sedridor.amidst.project.Project;

@Mod(
    modid = "forgeamidst",
    name = "ForgeAmidst",
    version = "1.7.10-1.4-beta",
    dependencies = "after:climatecontrol;after:RTG")
public class ForgeAmidst {

    @Mod.Instance("forgeamidst")
    private static ForgeAmidst instance;

    @SideOnly(Side.CLIENT)
    private static Minecraft mc;

    private static File mcDataDir;

    private static World world;

    private static MinecraftServer server;

    public static GenLayer biomeGen;

    public static GenLayer biomeIndexLayer;

    private static long seed;

    private static Object climatecontrol;

    private static Object dimensionManager;

    protected Logger logger = LogManager.getLogger();

    private static boolean climateControlFound = false;

    private static boolean rtgFound = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        mc = FMLClientHandler.instance()
            .getClient();
        mcDataDir = (FMLClientHandler.instance()
            .getClient()).mcDataDir;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
        if (Loader.isModLoaded("climatecontrol")) {
            climateControlFound = true;
            try {
                climatecontrol = getModInstance("climatecontrol");
            } catch (NoClassDefFoundError e) {
                System.out.println("ClimateControl not found");
            }
        }
        if (Loader.isModLoaded("RTG")) rtgFound = true;
        FMLCommonHandler.instance()
            .bus()
            .register(new TickHandler());
        TickHandler.registerBind();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAmidst());
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        if (server != null) {
            server.stopServer();
            server = null;
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));
            DimensionManager.setWorld(0, null);
            if (climateControlFound) ((ClimateControl) climatecontrol).serverStopped(null);
            if (rtgFound) RTG.instance.serverStopped(null);
        }
        if (FinderWindow.instance != null) FinderWindow.instance.menuBar.newMenu.setEnabled(false);
    }

    private static Object getModInstance(String modId) {
        return FMLCommonHandler.instance()
            .findContainerFor(modId)
            .getMod();
    }

    protected Object getCCField(String fieldName) {
        try {
            Field field = ClimateControl.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(climatecontrol);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected GenLayer getRiverMixWrapper(int par1) {
        try {
            Field field = climateControl.DimensionManager.class.getDeclaredField("wrappers");
            field.setAccessible(true);
            HashMap<Integer, GenLayerRiverMixWrapper> riverLayerWrapper = (HashMap<Integer, GenLayerRiverMixWrapper>) field
                .get(dimensionManager);
            if (riverLayerWrapper != null) return (GenLayer) riverLayerWrapper.get(Integer.valueOf(par1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getDimensionalSettings(int par1) {
        try {
            Field field = climateControl.DimensionManager.class.getDeclaredField("dimensionalSettings");
            field.setAccessible(true);
            HashMap<Integer, ClimateControlSettings> dimensionalSettings = (HashMap<Integer, ClimateControlSettings>) field
                .get(dimensionManager);
            if (dimensionalSettings != null) return dimensionalSettings.get(Integer.valueOf(par1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList getBiomeSettingIds(Object setting) {
        try {
            Field field = BiomeSettings.class.getDeclaredField("ids");
            field.setAccessible(true);
            return (ArrayList) field.get(setting);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected GenLayer getGenLayers(int par1) {
        if (world == null) return null;
        try {
            if (climateControlFound && world.provider.terrainType.getWorldTypeName()
                .equals("RTG")) {
                GenLayer riverLayerWrapper = getRiverMixWrapper(0);
                if (riverLayerWrapper == null) return null;
                if (par1 == 1) return ((GenLayerRiverMixWrapper) riverLayerWrapper).voronoi();
                return riverLayerWrapper;
            }
            if (rtgFound && world.provider.terrainType.getWorldTypeName()
                .equals("RTG")) return getGenLayersRTG(par1);
            if (par1 == 1) {
                Field field1 = WorldChunkManager.class.getDeclaredField("genBiomesVoronoi");
                field1.setAccessible(true);
                return (GenLayer) field1.get(world.provider.worldChunkMgr);
            }
            Field field = WorldChunkManager.class.getDeclaredField("genBiomes");
            field.setAccessible(true);
            return (GenLayer) field.get(world.provider.worldChunkMgr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected GenLayer getGenLayersRTG(int par1) {
        if (world == null) return null;
        try {
            if (par1 == 1) {
                Field field1 = WorldChunkManagerRTG.class.getDeclaredField("biomeIndexLayer");
                field1.setAccessible(true);
                return (GenLayer) field1.get(world.provider.worldChunkMgr);
            }
            Field field = WorldChunkManagerRTG.class.getDeclaredField("genBiomes");
            field.setAccessible(true);
            return (GenLayer) field.get(world.provider.worldChunkMgr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void updateGenLayers() {
        if (world == null) return;
        try {
            Field genBiomes0 = WorldChunkManager.class.getDeclaredField("genBiomes");
            genBiomes0.setAccessible(true);
            genBiomes0.set(world.provider.worldChunkMgr, biomeGen);
            Field genBiomes1 = WorldChunkManager.class.getDeclaredField("genBiomesVoronoi");
            genBiomes1.setAccessible(true);
            genBiomes1.set(world.provider.worldChunkMgr, biomeIndexLayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateGenLayersRTG() {
        if (world == null) return;
        try {
            Field genBiomes0 = WorldChunkManagerRTG.class.getDeclaredField("genBiomes");
            genBiomes0.setAccessible(true);
            genBiomes0.set(world.provider.worldChunkMgr, biomeGen);
            Field genBiomes1 = WorldChunkManagerRTG.class.getDeclaredField("biomeIndexLayer");
            genBiomes1.setAccessible(true);
            genBiomes1.set(world.provider.worldChunkMgr, biomeIndexLayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean rtgFound() {
        return rtgFound;
    }

    public static boolean climateControlFound() {
        return climateControlFound;
    }

    public static Object getCC() {
        return climatecontrol;
    }

    public static Object getDimensionManager() {
        return dimensionManager;
    }

    public static ForgeAmidst getInstance() {
        return instance;
    }

    public static File getDataDir() {
        return mcDataDir;
    }

    public static Minecraft getMC() {
        return mc;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static World getWorld() {
        return world;
    }

    public static void setWorld(World world) {
        ForgeAmidst.world = world;
    }

    public static void setServer(MinecraftServer server) {
        ForgeAmidst.server = server;
    }

    protected void loadAmidst() {
        Amidst.main(new String[] { "-mcjar", "versions\\1.7.10\\1.7.10.jar" });
    }

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event) {
        if (event.gui instanceof net.minecraft.client.gui.GuiMainMenu) {
            event.gui = new GuiMainMenuAmidst();
        } else if (event.gui instanceof net.minecraft.client.gui.GuiIngameMenu) {
            event.gui = new GuiIngameMenuAmidst();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world != null && !event.world.isRemote && event.world.provider.dimensionId == 0) {
            world = event.world;
            if (FinderWindow.instance != null) {
                FinderWindow.instance.menuBar.newMenu.setEnabled(false);
                FinderWindow.instance.menuBar.currentWorld.setEnabled(true);
            }
        }
    }

    @SubscribeEvent
    public void unloadWorld(WorldEvent.Unload event) {
        if (event.world != null && !event.world.isRemote && event.world.provider.dimensionId == 0) {
            world = null;
            biomeGen = null;
            biomeIndexLayer = null;
            if (FinderWindow.instance != null) {
                FinderWindow.instance.menuBar.newMenu.setEnabled(true);
                FinderWindow.instance.menuBar.currentWorld.setEnabled(false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBiomeGenInit(WorldTypeEvent.InitBiomeGens event) {
        if (climateControlFound) dimensionManager = getCCField("dimensionManager");
        System.out.println("FF: onBiomeGenInit - dimensionManager: " + dimensionManager);
        if (FMLCommonHandler.instance()
            .getEffectiveSide()
            .isServer() && event.seed != 0L) {
            seed = event.seed;
            biomeGen = event.newBiomeGens[0];
            biomeIndexLayer = event.newBiomeGens[1];
        }
    }

    protected void loadMap() {
        FinderWindow map = FinderWindow.instance;
        if (map == null) return;
        if (!map.isVisible()) map.setVisible(true);
        map.clearProject();
        map.setProject(new Project(world.provider.getSeed(), world.provider.terrainType.getWorldTypeName()));
    }
}
