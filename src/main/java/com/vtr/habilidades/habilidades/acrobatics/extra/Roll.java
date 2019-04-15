package com.vtr.habilidades.habilidades.acrobatics.extra;

import org.bukkit.event.Event;

import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPerLevel;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeType;

public class Roll extends HabilidadeExtraPerLevel {

	private double damageReduce;
	
	public Roll(int maxLevel, double perLevel, double damageReduce) {
		super(HabilidadeType.ACROBATICS, HabilidadeExtraType.ROLL, maxLevel, perLevel);
		this.damageReduce = damageReduce;
	}

	public boolean activate(Event e) {
		
		
		return false;
	}
}
