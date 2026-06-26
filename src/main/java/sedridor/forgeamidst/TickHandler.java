package sedridor.forgeamidst;

import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import sedridor.amidst.project.FinderWindow;
import sedridor.amidst.project.Project;

public class TickHandler {

    private static KeyBinding mapButton;

    private static long nextBindTriggerTime;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent tick) {
        if (tick.phase == TickEvent.Phase.END && (ForgeAmidst.getMC()).theWorld != null)
            if ((ForgeAmidst.getMC()).currentScreen == null && System.currentTimeMillis() >= nextBindTriggerTime
                && mapButton.isPressed()) {
                    nextBindTriggerTime = System.currentTimeMillis() + 1000L;
                    if (ForgeAmidst.getWorld() != null) {
                        FinderWindow map = FinderWindow.instance;
                        if (map == null) {
                            ForgeAmidst.getInstance()
                                .loadAmidst();
                            ForgeAmidst.getInstance()
                                .loadMap();
                        } else if (!map.isVisible()) {
                            map.setVisible(true);
                            if (FinderWindow.instance.curProject == null) {
                                map.clearProject();
                                map.setProject(
                                    new Project(
                                        (ForgeAmidst.getWorld()).provider.getSeed(),
                                        (ForgeAmidst.getWorld()).provider.terrainType.getWorldTypeName()));
                            }
                        }
                    }
                }
    }

    protected static void registerBind() {
        mapButton = new KeyBinding("ForgeAMIDST", 88, "key.categories.misc");
        ClientRegistry.registerKeyBinding(mapButton);
        nextBindTriggerTime = System.currentTimeMillis();
    }
}
