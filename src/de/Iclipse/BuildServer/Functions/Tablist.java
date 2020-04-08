package de.Iclipse.BuildServer.Functions;

import de.Iclipse.BuildServer.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class Tablist {
    static String header;
    static String footer;
    static String port;
    static String ranks;

    public final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
    private Team a;
    private Team b;
    private Team c;


    private HashMap<Player, String> rankColor = new HashMap<>();


    public Tablist() {
        header = "§8«§5§lIM§r§fBuildServer§r§8 »";
        footer = "§7Server: §e" + Data.instance.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName();
        ranks = "§4Admin §cMod \n §3User";

        this.a = sb.getTeam("1a") == null ? sb.registerNewTeam("1a") : sb.getTeam("1a");
        this.b = sb.getTeam("2b") == null ? sb.registerNewTeam("2b") : sb.getTeam("2b");
        this.c = sb.getTeam("3c") == null ? sb.registerNewTeam("3c") : sb.getTeam("3c");


        this.a.setPrefix("§7[§4Admin§7]§4 ");
        this.b.setPrefix("§7[§cMod§7]§c ");
        this.c.setPrefix("§3 ");
        this.a.setColor(ChatColor.getByChar('4'));
        this.b.setColor(ChatColor.getByChar('c'));
        this.c.setColor(ChatColor.getByChar('3'));
    }


    public void setTablist(Player p) {

        if (p.hasPermission("im.tab.serversettings")) {
            p.setPlayerListHeader(header + port);
            p.setPlayerListFooter(ranks + "\n" + footer);
        } else {
            p.setPlayerListHeader(header);
            p.setPlayerListFooter(ranks);
        }
    }


    public void setPlayer(Player p) {
        String team = "";
        if (p.hasPermission("im.color.admin")) {
            team = "1a";
        } else if (p.hasPermission("im.color.mod")) {
            team = "2b";
        } else {
            team = "3c";
        }
        if (!sb.getTeam(team).hasPlayer(Bukkit.getOfflinePlayer(p.getUniqueId())))
            sb.getTeam(team).addPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
        if (!sb.getTeam(team).hasEntry(p.getName())) sb.getTeam(team).addEntry(p.getName());
        rankColor.put(p, sb.getTeam(team).getPrefix());

        String name = "";
        System.out.println("DisplayName: " + p.getDisplayName());
        name = sb.getTeam(team).getPrefix() + p.getName();
        ChatColor.translateAlternateColorCodes('§', name);

        p.setPlayerListName(name);
        p.setDisplayName(name);
        p.setCustomName(name);
        p.setCustomNameVisible(true);
        p.setScoreboard(sb);
        Bukkit.getScheduler().runTaskTimer(Data.instance, () -> {
            Bukkit.getOnlinePlayers().forEach(pl -> pl.setScoreboard(sb));
        }, 1, 1);
    }
}
