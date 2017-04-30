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
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.rotzloch.marocraft.Helper;
import me.rotzloch.marocraft.Translation;
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
        Helper.RegionManager(world).addRegion(region);
    }

    public void Sell() {
        if (!alreadyExist() || !isOwner()) {
            // TODO: Message
            return;
        }
        Helper.RegionManager(world).removeRegion(regionName);
    }

    public void Info() {
        if (!alreadyExist()) {
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück %s gehört niemandem.\nDu kannst es kaufen für %s %s!", regionName, "1", "MaRo-Coin"));
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
        }
    }

    public void Unlock() {
        if (alreadyExist() && isOwner()) {
            region.setFlag(DefaultFlag.USE, State.ALLOW);
        }
    }

    public void AddMember(String playerName) {
        if (alreadyExist() && isOwner()) {
            region.getMembers().addPlayer(playerName);
        }
    }

    public void RemoveMember(String playerName) {
        if (alreadyExist() && isOwner()) {
            region.getMembers().removePlayer(playerName);
        }
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
            return Helper.RegionManager(world).getRegion(regionName);
        }
        CuboidSelection selection = new CuboidSelection(world,
                chunk.getBlock(0, 0, 0).getLocation(),
                chunk.getBlock(15, 255, 15).getLocation());
        return new ProtectedCuboidRegion(regionName,
                selection.getNativeMinimumPoint().toBlockVector(),
                selection.getNativeMaximumPoint().toBlockVector());
    }

    private boolean alreadyExist() {
        return Helper.RegionManager(world).hasRegion(regionName);
    }

    private boolean isOwner() {
        return region.getOwners().contains(localPlayer);
    }
}
