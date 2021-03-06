/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import me.rotzloch.marocraft.autoreplant.listener.AutoReplantListener;
import me.rotzloch.marocraft.blockbreakingreward.listener.BlockBreakingRewardListener;
import me.rotzloch.marocraft.itemstacker.listener.ItemStackListener;
import me.rotzloch.marocraft.land.command.LandCommandExecutor;
import me.rotzloch.marocraft.land.listener.LandListener;
import me.rotzloch.marocraft.land.listener.TaxListener;
import me.rotzloch.marocraft.rewardsigns.entity.RewardLock;
import me.rotzloch.marocraft.rewardsigns.listener.RewardSignListener;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MaRo-Craft Plugin
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class Main extends JavaPlugin {

    @Override
    public void onLoad() {
        Helper.setPlugin(this);
    }

    @Override
    public void onEnable() {
        if (Helper.setupEconomy() && Helper.setupDatabase()) {
            if (Helper.Config().getBoolean("config.ItemStacker.Enabled")) {
                Helper.RegisterListener(new ItemStackListener());
            }
            if (Helper.Config().getBoolean("config.Land.Enabled")) {
                getCommand("land").setExecutor(new LandCommandExecutor());
                if (Helper.Config().getBoolean("config.Land.SellBySign")) {
                    Helper.RegisterListener(new LandListener());
                }
                if (Helper.Config().getBoolean("config.Land.TaxEnabled")) {
                    Helper.RegisterListener(new TaxListener());
                }
            }
            if (Helper.Config().getBoolean("config.BlockBreakingReward.Enabled")) {
                Helper.RegisterListener(new BlockBreakingRewardListener());
            }
            if (Helper.Config().getBoolean("config.AutoReplant.Enabled")) {
                Helper.RegisterListener(new AutoReplantListener());
            }
            if (Helper.Config().getBoolean("config.RewardSigns.Enabled")) {
                Helper.RegisterListener(new RewardSignListener());
            }
        } else {
            Helper.LogMessage(Level.SEVERE, String.format("Disabled due to no Vault dependency found!"));
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return Arrays.asList(RewardLock.class);
    }

    public void InstallDDL() {
        installDDL();
    }
}
