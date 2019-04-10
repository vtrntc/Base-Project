package com.vtr.habilidades.objects;

import org.bukkit.inventory.ItemStack;

public class HabilidadeDrop {

	private String name;
	
	private ItemStack item;
	
	private double chance;
	
	private int minLevel;

	public HabilidadeDrop(String name, ItemStack item, double chance, int minLevel) {
		this.name = name;
		this.item = item;
		this.chance = chance;
		this.minLevel = minLevel;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public double getChance() {
		return chance;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
}
