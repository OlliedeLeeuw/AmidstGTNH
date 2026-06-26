package sedridor.forgeamidst;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class MapWorldSaveHandler extends AnvilSaveHandler implements ISaveHandler {

    public MapWorldSaveHandler() {
        super(new File(ForgeAmidst.getDataDir(), "config/forgeamidst"), "MAPWORLD", false);
    }

    public WorldInfo loadWorldInfo() {
        return null;
    }

    public void saveWorldInfoWithPlayer(WorldInfo par1WorldInfo, NBTTagCompound par2nbtTagCompound) {}

    public void saveWorldInfo(WorldInfo par1WorldInfo) {}

    public IPlayerFileData getSaveHandler() {
        return null;
    }

    public void flush() {}

    public File getWorldDirectory() {
        return super.getWorldDirectory();
    }

    public File getMapFileFromName(String par1Str) {
        return super.getMapFileFromName(par1Str);
    }

    public String getWorldDirectoryName() {
        return super.getWorldDirectoryName();
    }
}
