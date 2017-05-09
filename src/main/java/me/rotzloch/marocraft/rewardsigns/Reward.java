/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.rewardsigns;

import java.time.Instant;
import me.rotzloch.marocraft.rewardsigns.entity.RewardLock;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class Reward {

    private final String reward;
    private final Location tpLocation;
    private final long timeLock;

    public Reward(String reward, Location tpLocation, long timeLock) {
        this.reward = reward;
        this.tpLocation = tpLocation;
        this.timeLock = timeLock;
    }

    public void GetReward(Player player) {
        if (player == null || reward == null || timeLock == -1) {
            return;
        }
        RewardLock alreadyLocked = Helper.PLUGIN.getDatabase().find(RewardLock.class).where()
                .eq("playerId", player.getUniqueId())
                .eq("id", getId())
                .ge("lockEnd", Instant.now().toEpochMilli())
                .findUnique();
        if (alreadyLocked != null) {
            player.sendMessage(ChatColor.RED + Helper.TRANSLATE.getText("Diese Belohnung ist für dich noch gesperrt!"));
            return;
        }
        String[] rewards = reward.split("-");
        RewardLock rewardLock = new RewardLock(getId(),
                player.getUniqueId(),
                Instant.now().plusSeconds(timeLock).toEpochMilli());

        if (rewards[1].equalsIgnoreCase(Helper.getCurrencyName(2))) {
            Helper.ECONOMY.depositPlayer(player, Double.parseDouble(rewards[0]));
        } else {
            player.getInventory()
                    .addItem(new ItemStack(Integer.parseInt(rewards[0]),
                            Integer.parseInt(rewards[1])));
        }
        player.teleport(tpLocation);
        Helper.PLUGIN.getDatabase().save(rewardLock);
    }

    private String getId() {
        return new StringBuilder().append("[REWARD]#")
                .append(reward).append("#")
                .append(timeLock).append("#")
                .append(tpLocation.getX()).append("-")
                .append(tpLocation.getY()).append("-")
                .append(tpLocation.getZ())
                .toString();
    }

}
