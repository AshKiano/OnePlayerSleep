package com.ashkiano.oneplayersleep;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OnePlayerSleep extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        new Metrics(this, 21209);

        // Print the donation message to the console
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GOLD + "Thank you for using the OnePlayerSleep plugin!",
                    ChatColor.GOLD + "If you enjoy using this plugin!",
                    ChatColor.GOLD + "Please consider making a donation to support the development!",
                    ChatColor.GOLD + "You can donate at: " + ChatColor.GREEN + "https://donate.ashkiano.com"
            );
        }, 20);

        checkForUpdates();
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

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://plugins.ashkiano.com/version_check.php?plugin=" + pluginName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has("error")) {
                    this.getLogger().warning("Error when checking for updates: " + jsonObject.getString("error"));
                } else {
                    String latestVersion = jsonObject.getString("latest_version");

                    String currentVersion = this.getDescription().getVersion();
                    if (currentVersion.equals(latestVersion)) {
                        this.getLogger().info("This plugin is up to date!");
                    } else {
                        this.getLogger().warning("There is a newer version (" + latestVersion + ") available! Please update!");
                    }
                }
            } else {
                this.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to check for updates. Error: " + e.getMessage());
        }
    }
}
