package com.ashkiano.oneplayersleep;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class SleepListener implements Listener {

    private OnePlayerSleep plugin;

    public SleepListener(OnePlayerSleep plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                World world = event.getPlayer().getWorld();
                world.setTime(0); // Sets the time to day.
                world.setStorm(false); // Clears the weather.
                world.setThundering(false); // Stops thundering.
            }, 100L); // Delay in server ticks before running the task, to ensure it happens after the player is in bed.
        }
    }
}