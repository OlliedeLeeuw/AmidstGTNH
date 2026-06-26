package sedridor.forgeamidst;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiYesNoCallback;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMainMenuAmidst extends GuiMainMenu implements GuiYesNoCallback {

    public void initGui() {
        super.initGui();
        int i = this.height / 4 + 48;
        this.buttonList.add(new GuiButton(15, this.width / 2 - 152, i + 48, 48, 20, "AMIDST"));
    }

    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);
        if (par1GuiButton.id == 15) ForgeAmidst.getInstance()
            .loadAmidst();
    }
}
