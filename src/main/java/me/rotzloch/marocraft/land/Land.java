/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.land;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.rotzloch.marocraft.util.Helper;
import me.rotzloch.marocraft.util.NameFetcher;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class Land {

    private final World world;
    private final Player player;
    private final LocalPlayer localPlayer;
    private final CommandSender sender;

    private final String regionName;
    private final Chunk chunk;
    private final ProtectedRegion region;

    public Land(Player player, CommandSender sender, int x, int z) {
        this.player = player;
        this.sender = sender;
        this.world = player.getWorld();
        this.regionName = "region_" + x + "_" + z;
        this.chunk = world.getChunkAt(x, z);
        this.localPlayer = Helper.getWorldGuard().wrapPlayer(player);
        this.region = getRegion();
    }

    public void Buy() {
        if (alreadyExist()) {
            Info();
            return;
        }
        setBuyFlagsAndOwner();
        Helper.getRegionManager(world).addRegion(region);
        player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' erfolgreich gekauft.\n%s %s wurden deinem Konto abgezogen!", regionName, "100", "MaRo-Coins"));
    }

    public void Sell() {
        if (!alreadyExist() || !isOwner()) {
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Du kannst nur Grundstücke verkaufen, die dir gehören!"));
            return;
        }
        Helper.getRegionManager(world).removeRegion(regionName);
        player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' erfolgreich verkauft.\n%s %s wurden deinem Konto gutgeschrieben!", regionName, "100", "MaRo-Coins"));
    }

    public void Info() {
        if (!alreadyExist()) {
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück %s gehört niemandem.\nDu kannst es kaufen für %s %s!", regionName, "100", "MaRo-Coins"));
            return;
        }
        String text = Helper.TRANSLATE.getText("Grundstück '%s'\nBesitzer: %s\nMitglieder: %s\nFlags: %s->%s, %s->%s",
                regionName,
                getPlayerNames(region.getOwners().getUniqueIds()),
                getPlayerNames(region.getMembers().getUniqueIds()),
                DefaultFlag.USE.getName(), region.getFlag(DefaultFlag.USE),
                DefaultFlag.MOB_SPAWNING.getName(), region.getFlag(DefaultFlag.MOB_SPAWNING));
        player.sendMessage(ChatColor.GREEN + text);
    }

    public void Lock() {
        if (alreadyExist() && isOwner()) {
            region.setFlag(DefaultFlag.USE, State.DENY);
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' wurde gesperrt.", regionName));
        }
    }

    public void Unlock() {
        if (alreadyExist() && isOwner()) {
            region.setFlag(DefaultFlag.USE, State.ALLOW);
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' wurde entsperrt.", regionName));
        }
    }

    public void Mobs(boolean spawn) {
        if (alreadyExist() && isOwner()) {
            region.setFlag(DefaultFlag.MOB_SPAWNING, spawn ? State.ALLOW : State.DENY);
            if (spawn) {
                player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Auf dem Grundstück '%s' können nun Mobs spawnen.", regionName));
            } else {
                player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Auf dem Grundstück '%s' können nun keine Mobs spawnen.", regionName));
            }
        }
    }

    public void AddMember(String playerName) {
        if (alreadyExist() && isOwner()) {
            region.getMembers().addPlayer(playerName);
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Spieler '%s' wurde dem Grundstück '%s' hinzugefügt.", playerName, regionName));
        }
    }

    public void RemoveMember(String playerName) {
        if (alreadyExist() && isOwner()) {
            region.getMembers().removePlayer(playerName);
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Spieler '%s' wurde vom Grundstück '%s' entfernt.", playerName, regionName));
        }
    }

    public void List() {
        player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Folgende GS besitzt du:\n"));
        player.sendMessage("-----------------------");
        Helper.PLUGIN.getServer().getWorlds().stream().forEach(w -> list(w));
    }

    private void list(World world) {
        Helper.getRegionManager(world).getRegions().forEach((rn, r) -> {
            if (r.isOwner(localPlayer)) {
                player.sendMessage(ChatColor.GREEN + "- " + world.getName() + " -> " + rn);
            }
        });
    }

    public void Help() {
        String help = ChatColor.GOLD + "MaRo-Craft Land Help (/land help): \n";
        help += ChatColor.YELLOW + "-----------------------\n";
        help += ChatColor.GREEN + "/land buy -> GS kaufen.\n";
        help += "/land sell -> GS verkaufen.\n";
        help += "/land add <Playername> -> Spieler als Member hinzufügen.\n";
        help += "/land remove <Playername> -> Spieler als Member entfernen.\n";
        help += "/land lock -> GS Sperren (Türen, Knöpfe, Schalter).\n";
        help += "/land unlock -> GS Entsperren.\n";
        help += "/land mobs <true/false> -> Mob-Spawning On/Off\n";
        help += "/land list -> Zeigt eine Liste deiner Grundstücke an.\n";
        help += "/land info -> GS Information.\n";
        player.sendMessage(Helper.TRANSLATE.getText(help));
    }

    private String getPlayerNames(Set<UUID> uuids) {
        return uuids.stream()
                .map(NameFetcher::getPlayerName)
                .collect(Collectors.joining(","));
    }

    private void setBuyFlagsAndOwner() {
        DefaultDomain owners = new DefaultDomain();
        owners.addPlayer(localPlayer);
        region.setOwners(owners);
        region.setFlag(DefaultFlag.CREEPER_EXPLOSION, State.DENY);
        region.setFlag(DefaultFlag.TNT, State.DENY);
        region.setFlag(DefaultFlag.USE, State.ALLOW);
        region.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
        region.setPriority(5);
    }

    private ProtectedRegion getRegion() {
        if (alreadyExist()) {
            return Helper.getRegionManager(world).getRegion(regionName);
        }
        CuboidSelection selection = new CuboidSelection(world,
                chunk.getBlock(0, 0, 0).getLocation(),
                chunk.getBlock(15, 255, 15).getLocation());
        return new ProtectedCuboidRegion(regionName,
                selection.getNativeMinimumPoint().toBlockVector(),
                selection.getNativeMaximumPoint().toBlockVector());
    }

    private boolean alreadyExist() {
        return Helper.getRegionManager(world).hasRegion(regionName);
    }

    private boolean isOwner() {
        return region.getOwners().contains(localPlayer);
    }
}
