/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.persistence.PersistenceException;
import me.rotzloch.marocraft.Main;
import me.rotzloch.marocraft.rewardsigns.classes.RewardLock;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class Helper {

    public static Main PLUGIN;
    public static Translation TRANSLATE;
    public static Economy ECONOMY;
    private static Logger LOGGER;

    public static void setPlugin(Main plugin) {
        PLUGIN = plugin;
        LOGGER = PLUGIN.getLogger();
        LoadConfig();
        TRANSLATE = new Translation(Config().getString("config.Language"));
    }

    //region Logger
    public static void LogMessage(String message) {
        LogMessage(Level.INFO, message);
    }

    public static void LogMessage(Level level, String message) {
        LOGGER.log(level, message);
    }
    //endregion

    //region Configuration
    public static void LoadConfig() {
        Config().addDefault("config.Language", "de");

        Config().addDefault("config.ItemStacker.Enabled", true);
        Config().addDefault("config.ItemStacker.Radius", 5);
        Config().addDefault("config.ItemStacker.ItemPerStack", 256);

        Config().addDefault("config.Land.Enabled", true);
        Config().addDefault("config.Land.FirstGSForFree", true);
        Config().addDefault("config.Land.BaseBuyPrice", 200);
        Config().addDefault("config.Land.AddBuyPricePerGS", 50);
        Config().addDefault("config.Land.TaxEnabled", true);
        Config().addDefault("config.Land.TaxPerGS", 0.1);
        Config().addDefault("config.Land.TaxTimeSeconds", 900);
        Config().addDefault("config.Land.SellBySign", true);
        Config().addDefault("config.Land.IgnoreWorlds", Collections.emptyList());

        Config().addDefault("config.BlockBreakingReward.Enabled", true);
        Config().addDefault("config.BlockBreakingReward.Minutes", 2);
        Config().addDefault("config.BlockBreakingReward.AmountPerBlock", 0.1);
        List<Material> defaultIgnoreTypes = Arrays.asList(Material.AIR, Material.BED,
                Material.BOAT, Material.SIGN, Material.SNOW, Material.TORCH,
                Material.REDSTONE, Material.REDSTONE_WIRE,
                Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF);
        Config().addDefault("config.BlockBreakingReward.IgnoreTypes", defaultIgnoreTypes.stream().map(Material::name).collect(toList()));

        Helper.Config().addDefault("config.AutoReplant.Enabled", true);
        Helper.Config().addDefault("config.AutoReplant.ReplantTicksWait", 40);

        Config().options().copyDefaults(true);
        PLUGIN.saveConfig();
    }

    public static FileConfiguration Config() {
        return PLUGIN.getConfig();
    }
    //endregion

    //region Listener
    public static void RegisterListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, PLUGIN);
    }
    //endregion

    //region PluginManager
    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }
    //endregion

    public static RegionManager getRegionManager(World world) {
        return getWorldGuard().getRegionManager(world);
    }

    //region Tasks
    public static void StartDelayedTask(Runnable run, long timeTicks) {
        Bukkit.getScheduler().runTaskLater(PLUGIN, run, timeTicks);
    }

    public static void StopAsyncTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public static int StartAsyncTask(Runnable task, long timeTicks) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, task, timeTicks, timeTicks).getTaskId();
    }
    //endregion

    //region Economy
    public static boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            LogMessage("No Vault Plugin found.");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            LogMessage("No RegisteredServiceProvider found.");
            return false;
        }
        ECONOMY = rsp.getProvider();
        return true;
    }

    public static boolean hasAccount(String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(PlayerFetcher.getPlayerUniqueId(playerName));
        if (offlinePlayer == null) {
            return false;
        }
        return ECONOMY.hasAccount(offlinePlayer);
    }

    public static boolean hasEnoughBalance(String playerName, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(PlayerFetcher.getPlayerUniqueId(playerName));
        return ECONOMY.getBalance(offlinePlayer) >= amount;

    }

    public static String getCurrencyName(double amount) {
        if (amount > 1) {
            return ECONOMY.currencyNamePlural();
        }
        return ECONOMY.currencyNameSingular();
    }
    //endregion

    //region Database
    public static boolean setupDatabase() {
        try {
            PLUGIN.getDatabase().find(RewardLock.class).findRowCount();
        } catch (PersistenceException ex) {
            LogMessage("Installing database for " + PLUGIN.getDescription().getName() + " due to first time usage");
            PLUGIN.InstallDDL();
        }
        return true;
    }
    //endregion
}
