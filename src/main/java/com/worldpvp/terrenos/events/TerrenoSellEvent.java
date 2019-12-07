package com.worldpvp.terrenos.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.worldpvp.terrenos.objects.Terreno;

public class TerrenoSellEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private Player buyer;
	
	private Terreno terreno;
	
	public TerrenoSellEvent(Player buyer, Terreno terreno) {
		this.buyer = buyer;
		this.terreno = terreno;
	}
	
	public Player getBuyer() {
		return buyer;
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
