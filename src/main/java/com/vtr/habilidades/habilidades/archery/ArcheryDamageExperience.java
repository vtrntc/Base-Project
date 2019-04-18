package com.vtr.habilidades.habilidades.archery;

public class ArcheryDamageExperience {

	private int distance;
	
	private double xp;

	public ArcheryDamageExperience(int distance, double xp) {
		this.distance = distance;
		this.xp = xp;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public double getXp() {
		return xp;
	}
}
