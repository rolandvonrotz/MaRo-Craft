/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.land.task;

import me.rotzloch.marocraft.land.Land;
import me.rotzloch.marocraft.util.Helper;
import org.bukkit.entity.Player;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class TaxTask implements Runnable {

    private final Player player;
    private final double taxPerGs;
    private final long timeTicks;
    private int taskId = -1;

    public TaxTask(Player player) {
        this.player = player;
        taxPerGs = Helper.Config().getDouble("config.Land.TaxPerGS");
        timeTicks = Math.max(200, Helper.Config().getLong("config.Land.TaxTimeSeconds") * 20);
    }

    public void StartTax() {
        taskId = Helper.StartAsyncTask(this, timeTicks);
    }

    public void StopTax() {
        if (taskId != -1) {
            run();
            Helper.StopAsyncTask(taskId);
        }
    }

    @Override
    public void run() {
        int countGs = Land.CountGs(Helper.getWorldGuard().wrapPlayer(player));
        if (countGs > 0) {
            double priceBefore = Math.round((countGs * taxPerGs) * 100);
            double price = priceBefore / 100;
            player.sendMessage(Helper.TRANSLATE.getText("Grundstücksteuer von %s %s abgezogen.", price, Helper.getCurrencyName(price)));
            Helper.ECONOMY.withdrawPlayer(player, price);
        }
    }
}
