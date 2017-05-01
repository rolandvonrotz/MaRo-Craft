/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class PlayerFetcher {

    private static final Map<UUID, String> NAMES = new HashMap<>();
    private static final Map<String, UUID> UUIDS = new HashMap<>();

    public static String getPlayerName(UUID uuid) {
        if (!NAMES.containsKey(uuid)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer == null) {
                return "Unknown";
            }
            NAMES.put(uuid, offlinePlayer.getName());
            UUIDS.put(offlinePlayer.getName(), uuid);
        }
        return NAMES.get(uuid);
    }

    public static UUID getPlayerUniqueId(String playerName) {
        if (!UUIDS.containsKey(playerName)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (offlinePlayer == null) {
                return null;
            }
            UUIDS.put(playerName, offlinePlayer.getUniqueId());
            NAMES.put(offlinePlayer.getUniqueId(), playerName);
        }
        return UUIDS.get(playerName);
    }
}
