/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.rewardsigns.entity;

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
    private UUID id;

    @NotNull
    private String rewardId;

    @NotNull
    private UUID playerId;

    @NotNull
    private Long lockEnd;

    public RewardLock() {
        this(null, null, null, null);
    }

    public RewardLock(UUID id, String rewardId, UUID playerId, Long lockEnd) {
        this.id = id;
        this.rewardId = rewardId;
        this.playerId = playerId;
        this.lockEnd = lockEnd;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public void setPlayerId(Player player) {
        this.playerId = player.getUniqueId();
    }

    public Long getLockEnd() {
        return lockEnd;
    }

    public void setLockEnd(Long lockEnd) {
        this.lockEnd = lockEnd;
    }
}
