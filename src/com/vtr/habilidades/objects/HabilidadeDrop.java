package com.vtr.habilidades.objects;

import org.bukkit.inventory.ItemStack;

public class HabilidadeDrop {

	private String name;
	
	private ItemStack item;
	
	private double chance;
	
	private double maxChance;
	
	private int minLevel;

	public HabilidadeDrop(String name, ItemStack item, double chance, double maxChance, int minLevel) {
		this.name = name;
		this.item = item;
		this.chance = chance;
		this.maxChance = maxChance;
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
	
	public double getMaxChance() {
		return maxChance;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
}
