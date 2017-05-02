/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.land.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.rotzloch.marocraft.land.task.TaxTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class TaxListener implements Listener {

    private final Map<UUID, TaxTask> taxTaskList = new HashMap<>();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        TaxTask taxTask = new TaxTask(event.getPlayer());
        taxTask.StartTax();
        taxTaskList.put(event.getPlayer().getUniqueId(), taxTask);
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerQuitEvent event) {
        TaxTask taxTask = taxTaskList.get(event.getPlayer().getUniqueId());
        taxTask.StopTax();
        taxTaskList.remove(event.getPlayer().getUniqueId());
    }
}
