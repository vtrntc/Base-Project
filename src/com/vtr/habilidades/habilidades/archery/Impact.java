package com.vtr.habilidades.habilidades.archery;

public class Impact {

	private int bonus;
	
	private int perLevel;
	
	private int maxDamage;
	
	private double chance;

	public Impact(int bonus, int perLevel, int maxDamage, double chance) {
		this.bonus = bonus;
		this.perLevel = perLevel;
		this.maxDamage = maxDamage;
		this.chance = chance;
	}
	
	public int getBonus() {
		return bonus;
	}
	
	public int getPerLevel() {
		return perLevel;
	}
	
	public int getMaxDamage() {
		return maxDamage;
	}
	
	public double getChance() {
		return chance;
	}
}
