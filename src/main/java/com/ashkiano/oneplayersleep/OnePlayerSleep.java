package com.ashkiano.oneplayersleep;

import com.ashkiano.ashlib.PluginStatistics;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//TODO opravit nefunkčnost propojení s AshLib na 1.21
public class OnePlayerSleep extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        //TODO tuto chybu vypisovat i OP hráčům do chatu
        if (!isAshLibPresent()) {
            getLogger().severe("AshLib plugin is missing! Please download and install AshLib to run OnePlayerSleep. (can be downloaded from: https://www.spigotmc.org/resources/ashlib.118282/ )");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new PluginStatistics(this);

        getServer().getPluginManager().registerEvents(this, this);

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

    private boolean isAshLibPresent() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AshLib");
        return plugin != null && plugin.isEnabled();
    }
}
