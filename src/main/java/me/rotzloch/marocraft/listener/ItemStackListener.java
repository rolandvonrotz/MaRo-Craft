/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.listener;

import java.util.List;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Stacks items of the same type together.
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class ItemStackListener implements Listener {

    @EventHandler
    public void OnItemSpawn(ItemSpawnEvent event) {
        int radius = Helper.Config().getInt("config.ItemStacker.Radius");
        int maxSize = Helper.Config().getInt("config.ItemStacker.ItemPerStack");
        if (radius <= 0 || event.getEntityType() != EntityType.DROPPED_ITEM) {
            return;
        }
        Item droppedItem = event.getEntity();
        List<Entity> nearbyEntities = droppedItem.getNearbyEntities(radius, radius, radius);
        nearbyEntities.stream()
                .filter(e -> e instanceof Item)
                .map(e -> (Item) e)
                .filter(e -> sameItem(e.getItemStack(), droppedItem.getItemStack()) && e.getLocation().distance(droppedItem.getLocation()) <= radius)
                .forEach(e -> stack(e, droppedItem, maxSize));
    }

    private boolean sameItem(ItemStack currentItemStack, ItemStack droppedItemStack) {
        return currentItemStack.getType() == droppedItemStack.getType()
                && currentItemStack.getData().getData() == droppedItemStack.getData().getData()
                && currentItemStack.getDurability() == droppedItemStack.getDurability();
    }

    private void stack(Item entity, Item droppedItem, int maxSize) {
        int newAmount = droppedItem.getItemStack().getAmount();
        int curAmount = entity.getItemStack().getAmount();
        int more = Math.min(curAmount, maxSize - newAmount);
        newAmount += more;
        curAmount -= more;
        droppedItem.getItemStack().setAmount(newAmount);
        entity.getItemStack().setAmount(curAmount);
        if (curAmount <= 0) {
            entity.remove();
        }
    }
}
