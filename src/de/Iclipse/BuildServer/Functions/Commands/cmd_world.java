package de.Iclipse.BuildServer.Functions.Commands;

import de.Iclipse.BuildServer.Data;
import de.Iclipse.IMAPI.Util.Command.IMCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

import static de.Iclipse.BuildServer.Data.dsp;
import static de.Iclipse.IMAPI.Data.prefix;
import static de.Iclipse.IMAPI.Data.textcolor;
import static de.Iclipse.IMAPI.IMAPI.deleteFile;


public class cmd_world {
    StringBuilder builder;

    @IMCommand(
            name = "world",
            permissions = "im.cmd.world",
            usage = "world.usage",
            description = "world.description",
            minArgs = 0,
            maxArgs = 0
    )
    public void world(CommandSender sender) {
        builder = new StringBuilder();
        builder.append(dsp.get("world.world", sender) + "\n");
        add(sender, "create");
        add(sender, "teleport");
        add(sender, "list");
        add(sender, "save");
        add(sender, "delete");
        sender.sendMessage(builder.toString());
    }

    @IMCommand(
            name = "create",
            permissions = "im.cmd.world.create",
            usage = "world.create.usage",
            description = "world.create.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "world"
    )
    public void create(CommandSender sender, String name) {
        Bukkit.getWorlds().forEach(entry -> {
            if (entry.getName().equalsIgnoreCase(name)) {
                dsp.send(sender, "world.create.exists");
                return;
            }
        });
        Bukkit.getServer().createWorld(new WorldCreator(name + "_world"));
        dsp.send(sender, "world.create.success");
    }

    @IMCommand(
            name = "teleport",
            permissions = "im.cmd.world.teleport",
            usage = "world.teleport.usage",
            description = "world.teleport.description",
            minArgs = 1,
            maxArgs = 1,
            noConsole = true,
            parent = "world"
    )
    public void teleport(Player p, String name) {
        for (World entry : Bukkit.getWorlds()) {
            if (entry.getName().equalsIgnoreCase(name) || entry.getName().equalsIgnoreCase(name + "_world")) {
                dsp.send(p, "world.teleport.success");
                p.teleport(entry.getSpawnLocation());
                return;
            }
        }
        dsp.send(p, "world.teleport.notfound");
    }

    @IMCommand(
            name = "list",
            permissions = "im.cmd.world.list",
            usage = "world.list.usage",
            description = "world.list.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "world"
    )
    public void list(CommandSender sender) {
        if (sender instanceof Player) {
            TextComponent base = new TextComponent(prefix + Data.dsp.get("world.name", sender) + ": ");
            Bukkit.getWorlds().forEach(world -> {
                TextComponent component = new TextComponent("§5" + world.getName().replace("_world", "") + textcolor + ", ");
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world teleport " + world.getName()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Switch to " + world.getName().replace("_world", "")).create()));
                base.addExtra(component);
            });
            sender.spigot().sendMessage(base);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(prefix + dsp.get("world.name", sender) + ": ");
            Bukkit.getWorlds().forEach(entry -> {
                builder.append("§e" + entry.getName().replace("_world", "") + "§7,");
            });
            sender.sendMessage(builder.toString());
        }

    }

    @IMCommand(
            name = "save",
            permissions = "im.cmd.world.save",
            usage = "world.save.usage",
            description = "world.save.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "world",
            noConsole = true
    )
    public void save(Player p) {
        p.getWorld().save();
        dsp.send(p, "world.save.successfull");
    }

    @IMCommand(
            name = "delete",
            permissions = "im.cmd.world.delete",
            usage = "world.delete.usage",
            description = "world.delete.description",
            minArgs = 1,
            maxArgs = 2,
            parent = "world"
    )
    public void delete(CommandSender sender, String name, String confirm) {
        if (Bukkit.getWorld(name + "_world") != null || name == "world") {
            if (confirm == null) {
                if (sender instanceof Player) {
                    TextComponent component = new TextComponent(dsp.get("world.delete.confirm", sender));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world delete " + name + " confirm"));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Delete " + name).create()));
                    sender.spigot().sendMessage(component);
                } else {
                    dsp.send(sender, "world.delete.confirm");
                }
            } else {
                if (confirm.equalsIgnoreCase("confirm")) {
                    String realName = name + "_world";
                    Bukkit.unloadWorld(realName, false);
                    deleteFile(Bukkit.getWorld(realName).getWorldFolder());
                    dsp.send(sender, "world.delete.successfull");
                }
            }
        } else {
            dsp.send(sender, "world.delete.notfound");
        }
    }

    @IMCommand(
            name = "resize",
            permissions = "im.cmd.world.delete",
            usage = "world.delete.usage",
            description = "world.delete.description",
            minArgs = 1,
            maxArgs = 2,
            parent = "world"
    )
    public void resize(CommandSender sender, String name, String confirm) {
        if (Bukkit.getWorld(name + "_world") != null || name == "world") {
            if (confirm == null) {
                if (sender instanceof Player) {
                    TextComponent component = new TextComponent(dsp.get("world.delete.confirm", sender));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world resize " + name + " confirm"));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Resize " + name).create()));
                    sender.spigot().sendMessage(component);
                } else {
                    dsp.send(sender, "world.delete.confirm");
                }
            } else {
                if (confirm.equalsIgnoreCase("confirm")) {
                    String realName = name + "_world";
                    Bukkit.unloadWorld(realName, false);
                    Bukkit.getScheduler().runTaskAsynchronously(Data.instance, () -> {
                        File f = new File(Bukkit.getWorld(realName).getWorldFolder() + "/region");
                        for (int i = 0; i < f.listFiles().length; i++) {
                            String[] array = f.listFiles()[i].getName().split("\\.");
                            if (Math.abs(Integer.parseInt(array[1])) > 20 || Math.abs(Integer.parseInt(array[2])) > 20) {
                                f.listFiles()[i].delete();
                            }
                        }
                        dsp.send(sender, "world.delete.successfull");
                    });
                }
            }
        } else {
            dsp.send(sender, "world.delete.notfound");
        }
    }


    private void add(CommandSender sender, String command) {
        builder.append("\n" + de.Iclipse.IMAPI.Data.symbol + "§e" + dsp.get("world." + command + ".usage", sender) + "§8: §7 " + dsp.get("world." + command + ".description", sender) + ChatColor.RESET);
    }
}
