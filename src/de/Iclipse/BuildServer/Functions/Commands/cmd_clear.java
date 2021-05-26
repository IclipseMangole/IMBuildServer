package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.BuildServer.IMBuildServer;
import de.Iclipse.IMAPI.Util.Command.IMCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class cmd_clear {
    @IMCommand(
            name = "clear",
            maxArgs = 0,
            minArgs = 0,
            usage = "clear.usage",
            description = "clear.description",
            noConsole = true,
            permissions = {"im.cmd.clear"}
    )
    public void clear(Player p) {
        final int[] items = {0};
        p.getWorld().getEntities().forEach(entity -> {
            if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
                entity.remove();
                items[0]++;
            }
        });
        IMBuildServer.getInstance().getData().getDispatcher().send(p, "clear.successfull", "" + items[0]);
    }
}
