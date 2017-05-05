/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.blockbreakingreward.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import me.rotzloch.marocraft.blockbreakingreward.task.BlockBreakingRewardTask;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class BlockBreakingRewardListener implements Listener {

    private static final List<Material> IGNORE_TYPES = ((List<String>) Helper.Config().getList("config.BlockBreakingReward.IgnoreTypes")).stream().map(Material::getMaterial).collect(toList());

    private final Map<UUID, BlockBreakingRewardTask> taskList = new HashMap<>();

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        BlockBreakingRewardTask task = taskList.get(event.getPlayer().getUniqueId());
        if (player.getGameMode() == GameMode.SURVIVAL && !event.isCancelled() && task != null) {
            Material type = event.getBlock().getType();
            if (!IGNORE_TYPES.contains(type)) {
                task.BlockBreakCounter.incrementAndGet();
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        BlockBreakingRewardTask task = new BlockBreakingRewardTask(event.getPlayer());
        task.Start();
        taskList.put(event.getPlayer().getUniqueId(), task);
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerQuitEvent event) {
        BlockBreakingRewardTask task = taskList.get(event.getPlayer().getUniqueId());
        task.Stop();
        taskList.remove(event.getPlayer().getUniqueId());
    }
}
