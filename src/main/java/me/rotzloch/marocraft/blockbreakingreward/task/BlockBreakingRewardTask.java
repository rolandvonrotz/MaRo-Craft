/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.blockbreakingreward.task;

import java.util.concurrent.atomic.AtomicLong;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.entity.Player;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class BlockBreakingRewardTask implements Runnable {

    private final Player player;
    private final long timeTicks;
    private final double amountPerBlock;

    private int taskId = -1;
    public final AtomicLong BlockBreakCounter = new AtomicLong();

    public BlockBreakingRewardTask(Player player) {
        this.player = player;
        timeTicks = (Helper.Config().getLong("config.BlockBreakingReward.Minutes") * 60) * 20;
        amountPerBlock = Helper.Config().getDouble("config.BlockBreakingReward.AmountPerBlock");
    }

    public void Start() {
        BlockBreakCounter.set(0);
        taskId = Helper.StartAsyncTask(this, timeTicks);
    }

    public void Stop() {
        if (taskId != -1) {
            run();
            Helper.StopAsyncTask(taskId);
        }
    }

    @Override
    public void run() {
        if (BlockBreakCounter.get() > 0) {
            double priceBefore = Math.round((BlockBreakCounter.get() * amountPerBlock) * 100);
            double price = priceBefore / 100;
            BlockBreakCounter.set(0);
            player.sendMessage(Helper.TRANSLATE.getText("Du hast in den letzten %s Minuten %s %s verdient.",
                    Helper.Config().getLong("config.BlockBreakingReward.Minutes"),
                    price,
                    Helper.getCurrencyName(price)));
            Helper.ECONOMY.depositPlayer(player, price);
        }

    }

}
