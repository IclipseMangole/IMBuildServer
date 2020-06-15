package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.IMAPI.Util.Command.IMCommand;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class cmd_glowing {
    @IMCommand(
            name = "glowing",
            noConsole = true,
            maxArgs = 0,
            permissions = "im.cmd.glowing"
    )
    public void execute(Player p) {
        Bukkit.getOnlinePlayers().forEach(entry -> setGlowing(entry, p, true));
    }

    @IMCommand(
            name = "glowingoff",
            noConsole = true,
            maxArgs = 0,
            permissions = "im.cmd.glowing"
    )
    public void execute2(Player p) {
        Bukkit.getOnlinePlayers().forEach(entry -> setGlowing(p, entry, false));
    }


    public void setGlowing(Player glowingPlayer, Player sendPacketPlayer, boolean glow) {
        try {
            EntityPlayer entityPlayer = ((CraftPlayer) glowingPlayer).getHandle();

            DataWatcher toCloneDataWatcher = entityPlayer.getDataWatcher();
            DataWatcher newDataWatcher = new DataWatcher(entityPlayer);

            // The map that stores the DataWatcherItems is private within the DataWatcher Object.
            // We need to use Reflection to access it from Apache Commons and change it.
            Int2ObjectOpenHashMap<DataWatcher.Item<?>> currentMap = (Int2ObjectOpenHashMap<DataWatcher.Item<?>>) FieldUtils.readDeclaredField(toCloneDataWatcher, "entries", true);
            Int2ObjectOpenHashMap<DataWatcher.Item<?>> newMap = new Int2ObjectOpenHashMap<>();

            // We need to clone the DataWatcher.Items because we don't want to point to those values anymore.
            for (Integer integer : currentMap.keySet()) {
                newMap.put(integer, currentMap.get(integer).d()); // Puts a copy of the DataWatcher.Item in newMap
            }

            // Get the 0th index for the BitMask value. http://wiki.vg/Entities#Entity
            DataWatcher.Item item = newMap.get(0);

            byte initialBitMask = (Byte) item.b(); // Gets the initial bitmask/byte value so we don't overwrite anything.
            byte bitMaskIndex = (byte) 6; // The index as specified in wiki.vg/Entities
            if (glow) {
                item.a((byte) (initialBitMask | 1 << bitMaskIndex));
            } else {
                item.a((byte) (initialBitMask & ~(1 << bitMaskIndex))); // Inverts the specified bit from the index.
            }

            //item.a(glow);

            // Set the newDataWatcher's (unlinked) map data
            FieldUtils.writeDeclaredField(newDataWatcher, "entries", newMap, true);

            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(glowingPlayer.getEntityId(), newDataWatcher, true);

            ((CraftPlayer) sendPacketPlayer).getHandle().playerConnection.sendPacket(metadataPacket);
        } catch (IllegalAccessException e) { // Catch statement necessary for FieldUtils.readDeclaredField()
            e.printStackTrace();
        }
    }
}
