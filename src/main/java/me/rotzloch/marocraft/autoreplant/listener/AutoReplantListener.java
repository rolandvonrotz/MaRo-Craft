/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.autoreplant.listener;

import me.rotzloch.marocraft.autoreplant.task.ReplantTask;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class AutoReplantListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            if (block.getType() == Material.LOG
                    && block.getRelative(BlockFace.DOWN).getType() == Material.DIRT) {
                Helper.StartDelayedTask(new ReplantTask(block), Helper.Config().getInt("config.AutoReplant.ReplantTicksWait"));
            }
        }
    }
}
