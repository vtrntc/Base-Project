package com.vtr.habilidades.habilidades.archery;

public class ArrowRetrieval {

	private int perLevel;
	
	private int maxIncrease;
	
	private double chance;

	public ArrowRetrieval(int perLevel, int maxIncrease, double chance) {
		this.perLevel = perLevel;
		this.maxIncrease = maxIncrease;
		this.chance = chance;
	}
	
	public int getPerLevel() {
		return perLevel;
	}
	
	public int getMaxIncrease() {
		return maxIncrease;
	}
	
	public double getChance() {
		return chance;
	}
}
