package sedridor.forgeamidst;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import sedridor.amidst.project.FinderWindow;

@SideOnly(Side.CLIENT)
public class GuiIngameMenuAmidst extends GuiIngameMenu {

    public void initGui() {
        super.initGui();
        this.buttonList.set(3, new GuiButton(12, this.width / 2 + 2, this.height / 4 + 80, 98, 20, "ForgeAMIDST"));
    }

    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 12) {
            if (ForgeAmidst.getWorld() != null) {
                FinderWindow map = FinderWindow.instance;
                if (map == null) {
                    ForgeAmidst.getInstance()
                        .loadAmidst();
                    ForgeAmidst.getInstance()
                        .loadMap();
                } else if (!map.isVisible()) {
                    map.setVisible(true);
                }
            }
        } else {
            super.actionPerformed(par1GuiButton);
        }
    }
}
