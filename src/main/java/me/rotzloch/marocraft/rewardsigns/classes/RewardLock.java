/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.rewardsigns.classes;

import com.avaje.ebean.validation.NotNull;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
@Entity()
@Table(name = "mc_rewardLock")
public class RewardLock implements Serializable {

    @Id
    private long id;

    @NotNull
    private UUID playerId;

    @NotNull
    private long timeLock;

    @NotNull
    private String rewardId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public void setPlayerId(Player player) {
        this.playerId = player.getUniqueId();
    }

    public long getTimeLock() {
        return timeLock;
    }

    public void setTimeLock(long timeLock) {
        this.timeLock = timeLock;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

}
