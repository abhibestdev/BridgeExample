package bridgeexample;

import bridge.Bridge;
import bridge.event.ServerMessageReceieveEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BridgeExample extends JavaPlugin {

    private Map<UUID, PlayerData> playerDataMap = new HashMap<UUID, PlayerData>();
    private String serverName = getConfig().getString("server-name");

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(
                new Listener() {
                    @EventHandler
                    public void onJoin(PlayerJoinEvent event) {
                        final Player player = event.getPlayer();
                        playerDataMap.put(player.getUniqueId(), new PlayerData());
                        final PlayerData playerData = playerDataMap.get(player.getUniqueId());
                        new BukkitRunnable() {
                            public void run() {
                                player.sendMessage(ChatColor.GREEN + "Your found group was " + playerData.getGroup().getName() + ".");
                            }
                        }.runTaskLaterAsynchronously(BridgeExample.this, 20L);
                    }

                    @EventHandler
                    public void onQuit(PlayerQuitEvent event) {
                        Player player = event.getPlayer();
                        final PlayerData playerData = playerDataMap.get(player.getUniqueId());
                        playerDataMap.remove(player.getUniqueId());
                    }

                    @EventHandler
                    public void onMessageReceieved(ServerMessageReceieveEvent event) {
                        if (!event.getChannel().equalsIgnoreCase(serverName)) {
                            return;
                        }
                        String[] receieved = event.getMessage().split(":");
                        if (receieved[0].equalsIgnoreCase("Groups")) {
                            Player player = getServer().getPlayer(UUID.fromString(receieved[1]));
                            if (player != null) {
                                PlayerData playerData = playerDataMap.get(player.getUniqueId());
                                playerData.setGroup(Group.valueOf(receieved[2]));
                            }
                        }
                        System.out.println(event.getChannel() + ": " + event.getMessage());
                    }
                }, this);

    }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equals("group")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " <player> <group>");
                return true;
            }
            Player player = getServer().getPlayer(args[0]);
            if (player == null) {
                return true;
            }
            try {
                Group group = Group.valueOf(args[1].toUpperCase());
                PlayerData playerData = this.playerDataMap.get(player.getUniqueId());
                playerData.setGroup(group);
                sender.sendMessage(ChatColor.GREEN + "Group updated.");
            } catch (Exception ex) {
            }
            return true;
        }
        if (cmd.getName().equals("connect")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + commandLabel + " <server>");
                return true;
            }
            Player player = (Player) sender;
            final PlayerData playerData = playerDataMap.get(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "Sending you to server " + ChatColor.WHITE + args[0] + ChatColor.GOLD + "...");
            Bridge.sendMessageToServer(player, args[0], "Groups:" + player.getUniqueId() + ":" + playerData.getGroup().toString()); // GOTTA SAVE THE RANK THOUGH TO RETRIEVE IT. THIS JUST TRANSMITS ACROSS SERVERS
            Bridge.connectToServer(player, args[0]);
        }
        return true;
    }
}
