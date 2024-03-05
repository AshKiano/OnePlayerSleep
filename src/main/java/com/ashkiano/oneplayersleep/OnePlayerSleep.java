package com.ashkiano.oneplayersleep;

import org.bukkit.plugin.java.JavaPlugin;

public class OnePlayerSleep extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SleepListener(this), this);
        Metrics metrics = new Metrics(this, 21209);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
