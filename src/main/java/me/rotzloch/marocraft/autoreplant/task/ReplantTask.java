/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.autoreplant.task;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class ReplantTask implements Runnable {

    private final Block block;

    public ReplantTask(Block block) {
        this.block = block;
    }

    @Override
    public void run() {
        byte data = block.getData();
        block.setType(Material.SAPLING);
        block.setData(data);
    }

}
