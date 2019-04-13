package com.vtr.habilidades.habilidades.extra;

import com.vtr.api.spigot.utils.MathUtils;
import com.vtr.habilidades.habilidades.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public abstract class HabilidadeExtraPerLevel extends HabilidadeExtra {

	private int maxLevel;
	
	private double perLevel;
	
	public HabilidadeExtraPerLevel(HabilidadeType habilidadeType, HabilidadeExtraType extraType, int maxLevel, double perLevel) {
		super(habilidadeType, extraType);
		this.maxLevel = maxLevel;
		this.perLevel = perLevel;
	}
	
	public double getChance(HabilidadePlayer habilidadePlayer) {
		HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(habilidade.getType());
		if(habilidadeInfo != null) {
			return habilidadeInfo.getLevel() * perLevel;
		}
		
		return 0;
	}
	
	public boolean use(HabilidadePlayer habilidadePlayer) {
		return MathUtils.percentDouble(getChance(habilidadePlayer), 100);
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public double getPerLevel() {
		return perLevel;
	}
}
