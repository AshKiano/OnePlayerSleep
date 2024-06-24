package com.ashkiano.oneplayersleep;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class OnePlayerSleep extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        new Metrics(this, 21209);

        new UpdateChecker(this, UpdateCheckSource.SPIGOT, "115383")
                .setNotifyRequesters(false)
                .setNotifyOpsOnJoin(false)
                .setUserAgent(UserAgentBuilder.getDefaultUserAgent())
                .checkEveryXHours(12)
                .onSuccess((commandSenders, latestVersion) -> {
                    String messagePrefix = "&8[&6One Player Sleep&8] ";
                    String currentVersion = getDescription().getVersion();

                    if (currentVersion.equalsIgnoreCase(latestVersion)) {
                        String updateMessage = color(messagePrefix + "&aYou are using the latest version of OnePlayerSleep!");

                        Bukkit.getConsoleSender().sendMessage(updateMessage);
                        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(updateMessage));
                        return;
                    }

                    List<String> updateMessages = List.of(
                            color(messagePrefix + "&cYour version of OnePlayerSleep is outdated!"),
                            color(String.format(messagePrefix + "&cYou are using %s, latest is %s!", currentVersion, latestVersion)),
                            color(messagePrefix + "&cDownload latest here:"),
                            color("&6https://www.spigotmc.org/resources/oneplayersleep-1-16-1-21.115383/")
                    );

                    Bukkit.getConsoleSender().sendMessage(updateMessages.toArray(new String[]{}));
                    Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(updateMessages.toArray(new String[]{})));
                })
                .onFail((commandSenders, e) -> {
                }).checkNow();

        // Print the donation message to the console
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GOLD + "Thank you for using the OnePlayerSleep plugin!",
                    ChatColor.GOLD + "If you enjoy using this plugin!",
                    ChatColor.GOLD + "Please consider making a donation to support the development!",
                    ChatColor.GOLD + "You can donate at: " + ChatColor.GREEN + "https://donate.ashkiano.com"
            );
        }, 20);
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            World world = event.getPlayer().getWorld();
            world.setTime(0); // Sets the time to day.
            world.setStorm(false); // Clears the weather.
            world.setThundering(false); // Stops thundering.
        }, 100L); // Delay in server ticks before running the task, to ensure it happens after the player is in bed.
    }

}
