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
import me.rotzloch.marocraft.tasks.MarkerTask;
import me.rotzloch.marocraft.util.Helper;
import me.rotzloch.marocraft.util.PlayerFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class Land {

    private final World world;
    private final Player player;
    private final LocalPlayer localPlayer;
    private final OfflinePlayer currentOwner;

    private final String regionName;
    private final Chunk chunk;
    private final ProtectedRegion region;

    private final double price;
    private double buyPrice;
    private double sellPrice;

    public Land(Player player, int x, int z) {
        this.player = player;
        this.world = player.getWorld();
        this.regionName = "region_" + x + "_" + z;
        this.chunk = world.getChunkAt(x, z);
        this.localPlayer = Helper.getWorldGuard().wrapPlayer(player);
        this.region = getRegion();
        this.price = Helper.Config().getInt("config.Land.BaseBuyPrice");
        this.currentOwner = exist() ? getCurrentOwner() : null;
        calculatePrices();
    }

    public void Buy() {
        if (exist()) {
            Info();
            return;
        }
        if (!Helper.hasEnoughBalance(player.getName(), buyPrice)) {
            player.sendMessage(ChatColor.RED + Helper.TRANSLATE.getText("Dieses Grundstück ist zu teuer für dich.\nEs kostet: %s %s", buyPrice, Helper.getCurrencyName(buyPrice)));
            return;
        }
        Helper.ECONOMY.withdrawPlayer(player, buyPrice);
        setBuyFlagsAndOwner();
        Helper.getRegionManager(world).addRegion(region);
        setMarker(50);
        player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' erfolgreich gekauft.\n%s %s wurden deinem Konto abgezogen!", regionName, buyPrice, Helper.getCurrencyName(buyPrice)));
    }

    public void Sell() {
        if (!exist() || !isOwner()) {
            player.sendMessage(ChatColor.RED + Helper.TRANSLATE.getText("Du kannst nur Grundstücke verkaufen, die dir gehören!"));
            return;
        }
        Helper.ECONOMY.depositPlayer(player, sellPrice);
        Helper.getRegionManager(world).removeRegion(regionName);
        setMarker(76);
        player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' erfolgreich verkauft.\n%s %s wurden deinem Konto gutgeschrieben!", regionName, sellPrice, Helper.getCurrencyName(sellPrice)));
    }

    public void SellBySign(SignChangeEvent event) {
        if (exist() && isOwner()) {
            event.setLine(0, ChatColor.DARK_BLUE + "[L-SELL]");
            event.setLine(2, regionName);
            event.setLine(3, player.getName());
            Bukkit.broadcastMessage(Helper.TRANSLATE.getText("Grundstück '%s' wird von '%s' zum Verkauf angeboten.", regionName, player.getName()));
        }
    }

    public boolean BuyBySign(String playername, int buyPrice) {
        if (isOwner()) {
            player.sendMessage(ChatColor.RED + Helper.TRANSLATE.getText("Du kannst dein eigenes Grundstück nicht kaufen!"));
            return false;
        }
        if (!Helper.hasEnoughBalance(player.getName(), buyPrice)) {
            player.sendMessage(ChatColor.RED + Helper.TRANSLATE.getText("Dieses Grundstück ist zu teuer für dich.\nEs kostet: %s %s", buyPrice, Helper.getCurrencyName(buyPrice)));
            return false;
        }
        Helper.ECONOMY.withdrawPlayer(player, buyPrice);
        Helper.ECONOMY.depositPlayer(currentOwner, buyPrice);
        region.getOwners().removePlayer(currentOwner.getUniqueId());
        region.getMembers().clear();
        region.getOwners().addPlayer(localPlayer);
        Bukkit.broadcastMessage(Helper.TRANSLATE.getText("Grundstück '%s' wurde von '%s' gekauft.", regionName, player.getName()));
        return true;
    }

    public void Info() {
        if (!exist()) {
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück %s gehört niemandem.\nDu kannst es kaufen für %s %s!", regionName, buyPrice, Helper.getCurrencyName(buyPrice)));
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
        if (exist() && isOwner()) {
            region.setFlag(DefaultFlag.USE, State.DENY);
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' wurde gesperrt.", regionName));
        }
    }

    public void Unlock() {
        if (exist() && isOwner()) {
            region.setFlag(DefaultFlag.USE, State.ALLOW);
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Grundstück '%s' wurde entsperrt.", regionName));
        }
    }

    public void Mobs(boolean spawn) {
        if (exist() && isOwner()) {
            region.setFlag(DefaultFlag.MOB_SPAWNING, spawn ? State.ALLOW : State.DENY);
            if (spawn) {
                player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Auf dem Grundstück '%s' können nun Mobs spawnen.", regionName));
            } else {
                player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Auf dem Grundstück '%s' können nun keine Mobs spawnen.", regionName));
            }
        }
    }

    public void AddMember(String playerName) {
        if (exist() && isOwner()) {
            region.getMembers().addPlayer(PlayerFetcher.getPlayerUniqueId(playerName));
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Spieler '%s' wurde dem Grundstück '%s' hinzugefügt.", playerName, regionName));
        }
    }

    public void RemoveMember(String playerName) {
        if (exist() && isOwner()) {
            region.getMembers().removePlayer(PlayerFetcher.getPlayerUniqueId(playerName));
            player.sendMessage(ChatColor.GREEN + Helper.TRANSLATE.getText("Spieler '%s' wurde vom Grundstück '%s' entfernt.", playerName, regionName));
        }
    }

    public void List() {
        player.sendMessage(ChatColor.GOLD + Helper.TRANSLATE.getText("Folgende GS besitzt du:\n"));
        player.sendMessage(ChatColor.YELLOW + "-----------------------");
        Bukkit.getWorlds().stream().forEach(w -> list(w));
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
        help += ChatColor.YELLOW + "-----------------------\n" + ChatColor.GREEN;
        help += "/land buy -> GS kaufen.\n";
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
                .map(PlayerFetcher::getPlayerName)
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
        if (exist()) {
            return Helper.getRegionManager(world).getRegion(regionName);
        }
        CuboidSelection selection = new CuboidSelection(world,
                chunk.getBlock(0, 0, 0).getLocation(),
                chunk.getBlock(15, 255, 15).getLocation());
        return new ProtectedCuboidRegion(regionName,
                selection.getNativeMinimumPoint().toBlockVector(),
                selection.getNativeMaximumPoint().toBlockVector());
    }

    private boolean exist() {
        return Helper.getRegionManager(world).hasRegion(regionName);
    }

    private boolean isOwner() {
        return region.getOwners().contains(localPlayer);
    }

    private void setMarker(int markerId) {
        if (!player.hasPermission("marocraft.land.nomarker")) {
            MarkerTask task = new MarkerTask(world, chunk, markerId);
            Helper.StartDelayedTask(task, 1);
        }
    }

    private int countGs() {
        return Bukkit.getWorlds().stream()
                .map(Helper::getRegionManager)
                .mapToInt(rm -> rm.getRegionCountOfPlayer(localPlayer))
                .sum();
    }

    private void calculatePrices() {
        int count = countGs();
        if (count == 0 && Helper.Config().getBoolean("config.Land.FirstGSForFree")) {
            buyPrice = 0;
            sellPrice = 0;
            return;
        } else if (count > 0 && Helper.Config().getBoolean("config.Land.FirstGSForFree")) {
            count -= 1;
        }
        int additionalPrice = count * Helper.Config().getInt("config.Land.AddBuyPricePerGS");
        buyPrice = price + additionalPrice;
        sellPrice = (price + additionalPrice) * 0.8;
    }

    private OfflinePlayer getCurrentOwner() {
        UUID playerId = region.getOwners().getUniqueIds().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Region '" + regionName + "' has no owner!"));
        return Bukkit.getOfflinePlayer(playerId);
    }
}
