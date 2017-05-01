/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.commands;

import me.rotzloch.marocraft.util.Helper;
import me.rotzloch.marocraft.land.Land;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class LandCommandExecutor implements CommandExecutor {

    private static final String COMMAND = "land";

    private static enum Action {
        BUY, KAUFEN, SELL, VERKAUFEN, INFO, HELP, ADD, REMOVE, LOCK, UNLOCK, MOBS, LIST
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(COMMAND)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            Helper.LogMessage("'" + COMMAND + "' Command need to be called from player");
            return true;
        }
        Player player = (Player) sender;
        Action action;
        try {
            action = Action.valueOf(args[0].toUpperCase());
        } catch (Exception notEnum) {
            Helper.LogMessage("Unsupported Action");
            return true;
        }

        int chunkX = player.getLocation().getChunk().getX();
        int chunkZ = player.getLocation().getChunk().getZ();

        Land land = new Land(player, sender, chunkX, chunkZ);

        switch (action) {
            case KAUFEN:
            case BUY:
                land.Buy();
                break;
            case VERKAUFEN:
            case SELL:
                land.Sell();
                break;
            case INFO:
                land.Info();
                break;
            case LOCK:
                land.Lock();
                break;
            case UNLOCK:
                land.Unlock();
                break;
            case ADD:
                land.AddMember(args[1]);
                break;
            case REMOVE:
                land.RemoveMember(args[1]);
                break;

        }

        return true;
    }

}
