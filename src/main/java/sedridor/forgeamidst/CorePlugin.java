package sedridor.forgeamidst;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "sedridor.forgeamidst" })
@IFMLLoadingPlugin.SortingIndex(2000)
public class CorePlugin implements IFMLLoadingPlugin {

    public static File location;

    public String[] getASMTransformerClass() {
        return new String[] { "sedridor.forgeamidst.Transformer" };
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        location = (File) data.get("coremodLocation");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
