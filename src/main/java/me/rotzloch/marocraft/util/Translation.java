/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class Translation {

    private final File translateFile;
    private JSONObject messages = null;
    private final JSONParser parser = new JSONParser();

    public Translation(String language) {
        this.translateFile = new File(Helper.PLUGIN.getDataFolder(), language + ".json");
        try {
            if (!this.translateFile.exists()) {
                this.translateFile.createNewFile();
            }
            messages = (JSONObject) parser.parse(new FileReader(translateFile));
        } catch (IOException | ParseException ex) {
            Helper.LogMessage(Level.SEVERE, ex.getMessage());
            this.messages = new JSONObject();
        }
    }

    public String getText(String key, Object... args) {
        if (!messages.containsKey(key)) {
            addNotExistText(key);
        }
        return String.format((String) messages.get(key), args);
    }

    private void addNotExistText(String key) {
        messages.put(key, key);

        try (FileWriter file = new FileWriter(translateFile)) {
            file.write(messages.toJSONString());
            file.flush();
        } catch (IOException ex) {
            Helper.LogMessage(Level.SEVERE, ex.getMessage());
        }
    }

}
