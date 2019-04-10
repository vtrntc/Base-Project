package com.vtr.habilidades.habilidades.swords;

public class Bleed {

	private int minLevel;
	private int amount;
	private int time;
	
	private double damage;

	public Bleed(int minLevel, int amount, int time, double damage) {
		this.minLevel = minLevel;
		this.amount = amount;
		this.time = time;
		this.damage = damage;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getTime() {
		return time;
	}
	
	public double getDamage() {
		return damage;
	}
}
