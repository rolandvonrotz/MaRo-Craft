/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.util;

import com.sk89q.worldguard.util.profile.resolver.BukkitPlayerService;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import me.rotzloch.marocraft.Helper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class NameFetcher {

    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final JSONParser PARSER = new JSONParser();

    private static final Map<UUID, String> NAMES = new HashMap<>();

    public static String getPlayerName(UUID uuid) {
        if (!NAMES.containsKey(uuid)) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL + uuid.toString().replace("-", "")).openConnection();
                JSONObject response = (JSONObject) PARSER.parse(new InputStreamReader(connection.getInputStream()));
                NAMES.put(uuid, (String) response.get("name"));
            } catch (IOException | ParseException ex) {
                Helper.LogMessage(Level.SEVERE, ex.getMessage());
                return "Unknown";
            }
        }
        return NAMES.get(uuid);
    }
}
