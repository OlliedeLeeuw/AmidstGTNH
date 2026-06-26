package sedridor.forgeamidst;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import sedridor.amidst.project.FinderWindow;

public class CommandAmidst extends CommandBase {

    public String getCommandName() {
        return "amidst";
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return "/amidst";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
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
    }
}
