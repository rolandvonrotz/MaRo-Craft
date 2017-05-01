/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft;

import java.util.logging.Level;
import me.rotzloch.marocraft.commands.LandCommandExecutor;
import me.rotzloch.marocraft.listener.ItemStackListener;
import me.rotzloch.marocraft.listener.LandListener;
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
        if (Helper.setupEconomy()) {
            if (Helper.Config().getBoolean("config.ItemStacker.Enabled")) {
                Helper.RegisterListener(new ItemStackListener());
            }
            if (Helper.Config().getBoolean("config.Land.Enabled")) {
                getCommand("land").setExecutor(new LandCommandExecutor());
                Helper.RegisterListener(new LandListener());
            }
        } else {
            Helper.LogMessage(Level.SEVERE, String.format("Disabled due to no Vault dependency found!"));
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

    }

}
