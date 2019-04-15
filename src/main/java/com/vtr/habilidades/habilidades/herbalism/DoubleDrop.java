package com.vtr.habilidades.habilidades.herbalism;

import java.util.List;

import org.bukkit.Material;

public class DoubleDrop {

	private double chance;

	private List<Material> allowed;
	
	public DoubleDrop(double chance, List<Material> allowed) {
		this.chance = chance;
		this.allowed = allowed;
	}

	public double getChance() {
		return chance;
	}
	
	public boolean isAllowed(Material material) {
		return allowed.contains(material);
	}
}
