/*
 * Copyright (C) 2017 Rotzloch - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited
 * by law. This file is proprietary and confidential.
 */
package me.rotzloch.marocraft.rewardsigns.listener;

import org.bukkit.event.Listener;

/**
 *
 * @author Rotzloch <rotzloch@vonrotz-beutter.ch>
 */
public class RewardSignListener implements Listener {

    /*
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent ev) {
        Reward reward = new Reward(ev, ev.getPlayer());
        if (reward != null && !ev.isCancelled() && ev.getLine(0).equalsIgnoreCase("[Reward]")) {
            Helper.SendMessageInfo(ev.getPlayer(), Text.RewardCreated);
        }
    }
    
    @EventHandler (priority = EventPriority.HIGHEST) 
	public void onSignClick(PlayerInteractEvent ev) {
		try {
			if(ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			Block evBlock = ev.getClickedBlock();
			if(evBlock == null) {
				return;
			}
			if(evBlock.getType() == Material.SIGN_POST || evBlock.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign)evBlock.getState();
				if(sign.getLine(0).equalsIgnoreCase("[REWARD]")) {
					if(!ev.getPlayer().hasPermission("marocraft.reward.use")) {
						Helper.SendMessageNoPermission(ev.getPlayer());
						ev.setCancelled(true);
						return;
					}				
					Reward reward = new Reward(sign, ev.getPlayer());
					if(reward != null && !ev.isCancelled()) {
						reward.GetReward(ev.getPlayer());
					} else {
						Helper.SendMessageError(ev.getPlayer(), Text.Error);
					}
				}
			}
		} catch(Exception e) {
			Helper.LogError(e.getMessage());
			e.printStackTrace();
		}
	}
     */
}


/*

public class Reward {
	private String _rewardString;
	private Location _rewardTPLocation;
	private int _rewardTimeLock;
	
	public Reward(SignChangeEvent ev, Player player) {
		if(ev.getLine(0).equalsIgnoreCase("[Reward]")) {
        	if(!player.hasPermission("marocraft.reward.create")) {
        		Helper.SendMessageNoPermission(ev.getPlayer());
        		ev.setCancelled(true);
        		return;
        	}
        	if(ev.getLine(1).equals("") || ev.getLine(2).equals("") || ev.getLine(3).equals("")) {
                this.Help(ev.getPlayer());
                ev.setCancelled(true);
                return;
            }
        	String[] locArray = ev.getLine(3).split(" ");
        	Double x = Double.parseDouble(locArray[0]);
        	Double y = Double.parseDouble(locArray[1]);
        	Double z = Double.parseDouble(locArray[2]);
        	if(x == null || y == null || z == null) {
        		this.Help(player);
                ev.setCancelled(true);
                return;
        	}
        	this._rewardString = ev.getLine(1);
        	this._rewardTPLocation = new Location(player.getWorld(),x,y,z);
        	this._rewardTimeLock = Integer.parseInt(ev.getLine(2));
        } else {
            return;
        }
	}

	public Reward(Sign sign, Player player) {
		this._rewardString = sign.getLine(1);
		Double x = Double.parseDouble(sign.getLine(3).split(" ")[0]);
    	Double y = Double.parseDouble(sign.getLine(3).split(" ")[1]);
    	Double z = Double.parseDouble(sign.getLine(3).split(" ")[2]);
    	this._rewardTPLocation = new Location(player.getWorld(),x,y,z);
    	this._rewardTimeLock = Integer.parseInt(sign.getLine(2));
	}
	
	public void Help(Player player) {
		player.sendMessage(Text.RewardHelp());
	}

	public void GetReward(Player player) {
		RewardLock lock = Helper.RewardLocks(this.ID(), player.getName());
		if(lock != null) {
			Helper.SendMessageError(player,Text.RewardLock);
			return;
		}
		if(player != null && this._rewardString != null && this._rewardTimeLock != -1) {
			String[] rewards = this._rewardString.split("-");
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, this._rewardTimeLock);
			RewardLock rewardLock = new RewardLock(player, calendar.getTimeInMillis(),this.ID());
			if(rewards[1].equalsIgnoreCase("Maros")) {
				Helper.PayToTarget(null, player.getName(), Double.parseDouble(rewards[0]));
				player.teleport(this._rewardTPLocation);
			} else {
				String item = rewards[0];
				String amount = rewards[1];
				ItemStack stack = new ItemStack(Integer.parseInt(item));
				stack.setAmount(Integer.parseInt(amount));
				player.getInventory().addItem(stack);
				player.teleport(this._rewardTPLocation);
				
			}
			Helper.AddRewardLock(rewardLock);
		} else {
			Helper.SendMessageError(player, Text.Error);
		}
	}
	
	public String ID() {
		String id = "";
		
		id += "[REWARD]#";
		id += this._rewardString + "#";
		id += this._rewardTimeLock + "#";
		id += this._rewardTPLocation.getX() + "-"+this._rewardTPLocation.getY() + "-"+ this._rewardTPLocation.getZ();
		
		return id;
	}
	
}

*/
