package com.worldpvp.terrenos.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.worldpvp.terrenos.objects.Terreno;

public class TerrenoPvPToggleEvent extends Event {

	private boolean cancelled = false;

	private static HandlerList handlers = new HandlerList();
	
	private Player player;
	private Terreno terreno;
	private boolean isPvP;
	
	public TerrenoPvPToggleEvent(Player player, Terreno terreno, boolean isPvP) {
		this.player = player;
		this.terreno = terreno;
		this.isPvP = isPvP;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Terreno getTerreno() {
		return terreno;
	}
	
	public boolean isPvP() {
		return isPvP;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
