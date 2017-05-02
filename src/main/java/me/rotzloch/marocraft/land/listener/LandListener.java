/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.land.listener;

import java.util.logging.Level;
import me.rotzloch.marocraft.land.Land;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class LandListener implements Listener {

    @EventHandler
    public void OnBlockPlace(SignChangeEvent event) {
        try {
            if (event.getLine(0).equalsIgnoreCase("[L-SELL]")
                    && Integer.parseInt(event.getLine(1)) > 0) {
                Chunk chunk = event.getBlock().getLocation().getChunk();
                Land land = new Land(event.getPlayer(), chunk.getX(), chunk.getZ());
                land.SellBySign(event);
            }
        } catch (IndexOutOfBoundsException | NumberFormatException ex) {
            Helper.LogMessage(Level.SEVERE, ex.getMessage());
        }
    }

    @EventHandler
    public void OnSignClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if (sign.getLine(0).equalsIgnoreCase(
                    ChatColor.DARK_BLUE + "[L-SELL]")) {
                int sellprice = Integer.parseInt(sign.getLine(1));
                Chunk chunk = sign.getLocation().getChunk();
                Land land = new Land(event.getPlayer(), chunk.getX(), chunk.getZ());
                if (land.BuyBySign(sign.getLine(3), sellprice)) {
                    event.getClickedBlock().breakNaturally();
                }
            }
        }
    }
}
