/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.util;

import me.rotzloch.marocraft.util.Translation;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.rotzloch.marocraft.Main;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class Helper {

    public static Main PLUGIN;
    public static Translation TRANSLATE;
    private static Logger LOGGER;

    public static void setPlugin(Main plugin) {
        Helper.PLUGIN = plugin;
        LOGGER = Helper.PLUGIN.getLogger();
        Helper.LoadConfig();
        Helper.TRANSLATE = new Translation(Helper.Config().getString("config.Language"));
    }

    //region Logger
    public static void LogMessage(String message) {
        Helper.LogMessage(Level.INFO, message);
    }

    public static void LogMessage(Level level, String message) {
        LOGGER.log(level, message);
    }
    //endregion

    //region Configuration
    public static void LoadConfig() {
        Helper.Config().addDefault("config.Language", "de");

        Helper.Config().addDefault("config.ItemStacker.Enabled", true);
        Helper.Config().addDefault("config.ItemStacker.Radius", 5);
        Helper.Config().addDefault("config.ItemStacker.ItemPerStack", 256);

        Helper.Config().addDefault("config.Land.Enabled", true);

        Helper.Config().options().copyDefaults(true);
        Helper.PLUGIN.saveConfig();
    }

    public static FileConfiguration Config() {
        return Helper.PLUGIN.getConfig();
    }
    //endregion

    //region Listener
    public static void RegisterListener(Listener listener) {
        Helper.getPluginManager().registerEvents(listener, PLUGIN);
    }
    //endregion

    //region PluginManager
    public static PluginManager getPluginManager() {
        return Helper.PLUGIN.getServer().getPluginManager();
    }

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Helper.getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }
    //endregion

    public static RegionManager getRegionManager(World world) {
        return Helper.getWorldGuard().getRegionManager(world);
    }
}
