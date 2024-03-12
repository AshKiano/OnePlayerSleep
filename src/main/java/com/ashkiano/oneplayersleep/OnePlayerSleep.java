package com.ashkiano.oneplayersleep;

import org.bukkit.plugin.java.JavaPlugin;

public class OnePlayerSleep extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SleepListener(this), this);
        Metrics metrics = new Metrics(this, 21209);
        this.getLogger().info("Thank you for using the OnePlayerSleep plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
