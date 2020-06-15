package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.IMAPI.Util.Command.IMCommand;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class cmd_armorstand {
    @IMCommand(
            name = "armorstand",
            maxArgs = 0,
            permissions = "im.cmd.armorstand",
            noConsole = true
    )
    public void execute(Player p) {

        Location location = p.getLocation();
        WorldServer s = ((CraftWorld) location.getWorld()).getHandle();
        EntityLiving stand = new EntityArmorStand(s, location.getX(), location.getY(), location.getZ());

        stand.setCustomName(new ChatComponentText(p.getDisplayName()));
        stand.setCustomNameVisible(true);
        //Gravity false
        stand.noclip = true;
        stand.setNoGravity(true);

        stand.setInvisible(true);


        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);


    }
}
