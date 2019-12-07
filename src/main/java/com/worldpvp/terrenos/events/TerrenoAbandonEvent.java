package com.worldpvp.terrenos.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.worldpvp.terrenos.objects.Terreno;

public class TerrenoAbandonEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private Player player;
	private Terreno terreno;
	
	public TerrenoAbandonEvent(Player player, Terreno terreno) {
		this.player = player;
		this.terreno = terreno;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Terreno getTerreno() {
		return terreno;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
}
