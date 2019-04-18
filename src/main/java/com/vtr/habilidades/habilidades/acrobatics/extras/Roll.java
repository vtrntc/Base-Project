package com.vtr.habilidades.habilidades.acrobatics.extras;

import org.bukkit.event.Event;

import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPerLevel;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeType;

public class Roll extends HabilidadeExtraPerLevel {

	private double damageReduce;
	
	public Roll(double perLevel, int levelBase, int maxLevel, double damageReduce) {
		super(HabilidadeType.ACROBATICS, HabilidadeExtraType.ROLL, perLevel, levelBase, maxLevel);
		this.damageReduce = damageReduce;
	}

	public boolean activate(Event e) {
		
		
		return false;
	}
}
