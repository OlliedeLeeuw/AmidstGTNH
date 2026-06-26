package sedridor.forgeamidst;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.layer.GenLayer;

import climateControl.customGenLayer.GenLayerRiverMixWrapper;

public class MapWorldRTG extends WorldServer {

    public MapWorldRTG(MinecraftServer server, long seed, WorldType worldType, WorldSettings par5WorldSettings) {
        super(server, new MapWorldSaveHandler(), "ForgeAmidst", 0, par5WorldSettings, server.theProfiler);
        System.out.println("MapWorld: " + seed + " " + worldType.getWorldTypeName());
        if (!worldType.getWorldTypeName()
            .equals("RTG") || ForgeAmidst.getWorld() == null) {
            ForgeAmidst.setWorld((World) this);
            ForgeAmidst.biomeGen = ForgeAmidst.getInstance()
                .getGenLayers(0);
            ForgeAmidst.biomeIndexLayer = ForgeAmidst.getInstance()
                .getGenLayers(1);
        }
    }

    protected IChunkProvider createChunkProvider() {
        IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.provider);
        IChunkProvider chunkGenerator = this.provider.createChunkGenerator();
        this.theChunkProviderServer = new MapWorldChunkProvider(this, ichunkloader, chunkGenerator);
        return (IChunkProvider) this.theChunkProviderServer;
    }

    public void updateGenLayers(GenLayerRiverMixWrapper riverLayerWrapper) {
        ForgeAmidst.setWorld((World) this);
        ForgeAmidst.biomeGen = (GenLayer) riverLayerWrapper;
        ForgeAmidst.biomeIndexLayer = riverLayerWrapper.voronoi();
        if (this.provider.terrainType.getWorldTypeName()
            .equals("RTG") && ForgeAmidst.rtgFound()) {
            ForgeAmidst.getInstance()
                .updateGenLayers();
            ForgeAmidst.getInstance()
                .updateGenLayersRTG();
        }
    }
}
