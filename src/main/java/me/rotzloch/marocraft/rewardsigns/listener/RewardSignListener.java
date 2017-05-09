/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.rewardsigns.listener;

import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import me.rotzloch.marocraft.rewardsigns.Reward;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class RewardSignListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent ev) {
        if ("[Reward]".equals(ev.getLine(0))) {
            if (!ev.getPlayer().hasPermission("marocraft.rewardSign.create")) {
                Helper.NoPermission(ev.getPlayer());
                ev.setCancelled(true);
                return;
            }
            Reward reward = loadReward(ev.getLines(), ev.getPlayer());
            if (reward == null) {
                ev.setCancelled(true);
                return;
            }
            ev.getPlayer().sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Reward Tafel erstellt."));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignClick(PlayerInteractEvent ev) {
        Block block = ev.getClickedBlock();
        if (ev.getAction() != Action.RIGHT_CLICK_BLOCK
                || block == null
                || (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)) {
            return;
        }
        Sign sign = (Sign) block.getState();
        if ("[Reward]".equals(sign.getLine(0))) {
            if (!ev.getPlayer().hasPermission("marocraft.rewardSign.use")) {
                Helper.NoPermission(ev.getPlayer());
                ev.setCancelled(true);
                return;
            }
            Reward reward = loadReward(sign.getLines(), ev.getPlayer());
            if (reward != null && !ev.isCancelled()) {
                reward.GetReward(ev.getPlayer());
            }
        }
    }

    private Reward loadReward(String[] lines, Player player) {
        if ("".equals(lines[1]) || "".equals(lines[2]) || "".equals(lines[3])) {
            return null;
        }
        List<Double> coordinates = Stream.of(lines[3].split(" "))
                .map(Double::parseDouble)
                .filter(v -> v != null)
                .collect(toList());
        if (coordinates.size() < 3) {
            return null;
        }
        return new Reward(lines[1],
                new Location(player.getWorld(), coordinates.get(0), coordinates.get(1), coordinates.get(2)),
                Integer.parseInt(lines[2]));
    }

}
