package sedridor.forgeamidst;

import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapWorldChunkProvider extends ChunkProviderServer implements IChunkProvider {

    private static final Logger logger = LogManager.getLogger();

    public MapWorldChunkProvider(WorldServer par1WorldServer, IChunkLoader par2IChunkLoader,
        IChunkProvider par3IChunkProvider) {
        super(par1WorldServer, par2IChunkLoader, par3IChunkProvider);
    }

    private void safeSaveExtraChunkData(Chunk par1Chunk) {}

    private void safeSaveChunk(Chunk par1Chunk) {}

    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {}

    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
        return true;
    }

    public String makeString() {
        return "MapWorldChunkProvider";
    }

    public boolean unloadQueuedChunks() {
        return false;
    }

    public boolean canSave() {
        return false;
    }

    public void getLoadedChunkCount(int par1, int par2) {}
}
