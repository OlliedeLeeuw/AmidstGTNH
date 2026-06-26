package sedridor.forgeamidst;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public class MapWorld extends WorldServer {

    public MapWorld(MinecraftServer server, long seed, WorldType worldType, WorldSettings par5WorldSettings) {
        super(server, new MapWorldSaveHandler(), "ForgeAmidst", 0, par5WorldSettings, server.theProfiler);
        System.out.println("MapWorld: " + seed + " " + worldType.getWorldTypeName());
        ForgeAmidst.setWorld((World) this);
        ForgeAmidst.biomeGen = ForgeAmidst.getInstance()
            .getGenLayers(0);
        ForgeAmidst.biomeIndexLayer = ForgeAmidst.getInstance()
            .getGenLayers(1);
    }

    protected IChunkProvider createChunkProvider() {
        IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.provider);
        IChunkProvider chunkGenerator = this.provider.createChunkGenerator();
        this.chunkProvider = new MapWorldChunkProvider(this, ichunkloader, chunkGenerator);
        return (IChunkProvider) this.chunkProvider;
    }
}
