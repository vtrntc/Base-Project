package com.vtr.habilidades.habilidades.acrobatics;

import org.bukkit.Location;

import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeInfo;

public class AcrobaticsInfo extends HabilidadeInfo {

	private Location lastFallDamage;
	
	private int fallTries;
	
	public AcrobaticsInfo(String player, Habilidade habilidade, int level, double xp) {
		super(player, habilidade, level, xp);
	}

	public Location getLastFallDamage() {
		return lastFallDamage;
	}
	
	public void setLastFallDamage(Location lastFallDamage) {
		this.lastFallDamage = lastFallDamage;
	}
	
	public int getFallTries() {
		return fallTries;
	}
	
	public void setFallTries(int fallTries) {
		this.fallTries = fallTries;
	}
}
