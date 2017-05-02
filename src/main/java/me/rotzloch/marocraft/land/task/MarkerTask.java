/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.land.task;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class MarkerTask implements Runnable {

    private World world;
    private Chunk chunk;
    private int markerId;

    public MarkerTask(World world, Chunk chunk, int markerId) {
        this.world = world;
        this.chunk = chunk;
        this.markerId = markerId;
    }

    @Override
    public void run() {
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                if (x != 0 && x != 15 && z > 0 && z < 15) {
                    continue;
                }
                if (z != 0 && z != 15 && x > 0 && x < 15) {
                    continue;
                }
                setMarker(x, z);
            }
        }
    }

    private void setMarker(int x, int z) {
        int y = world.getMaxHeight() - 2;
        Block block = chunk.getBlock(x, y, z);
        while (y >= 0) {
            // Do not stack cornerID Blocks
            if (block.getTypeId() == markerId) {
                break;
            }

            switch (block.getType()) {
                case LEAVES: // Fall through
                case AIR:
                    y--;
                    break;

                case SAPLING: // Fall through
                case LONG_GRASS: // Fall through
                case DEAD_BUSH: // Fall through
                case YELLOW_FLOWER: // Fall through
                case RED_ROSE: // Fall through
                case BROWN_MUSHROOM: // Fall through
                case RED_MUSHROOM: // Fall through
                case SNOW:
                case TORCH:
                case REDSTONE_TORCH_ON:
                case REDSTONE_TORCH_OFF:
                    block.setTypeId(markerId);
                    y = -1;
                    break;

                case BED_BLOCK: // Fall through
                case POWERED_RAIL: // Fall through
                case DETECTOR_RAIL: // Fall through
                case RAILS: // Fall through
                case STONE_PLATE: // Fall through
                case WOOD_PLATE:
                    block.getRelative(0, 2, 0).setTypeId(markerId);
                    y = -1;
                    break;

                default:
                    block.getRelative(0, 1, 0).setTypeId(markerId);
                    y = -1;
                    break;
            }

            block = block.getRelative(0, -1, 0);
        }
    }

}
