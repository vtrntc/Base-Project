package com.vtr.habilidades.habilidades.unarmed;

public class Disarmor {

	private double chance;
	
	private int perLevel;
	
	private int maxIncrease;

	public Disarmor(double chance, int perLevel, int maxIncrease) {
		this.chance = chance;
		this.perLevel = perLevel;
		this.maxIncrease = maxIncrease;
	}
	
	public double getChance() {
		return chance;
	}
	
	public int getPerLevel() {
		return perLevel;
	}
	
	public int getMaxIncrease() {
		return maxIncrease;
	}
}
