/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft;

import me.rotzloch.marocraft.commands.LandCommandExecutor;
import me.rotzloch.marocraft.listener.ItemStackListener;
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
        if (Helper.Config().getBoolean("config.ItemStacker.Enabled")) {
            Helper.RegisterListener(new ItemStackListener());
        }
        if (Helper.Config().getBoolean("config.Land.Enabled")) {
            this.getCommand("land").setExecutor(new LandCommandExecutor());
        }
    }

    @Override
    public void onDisable() {

    }

}
