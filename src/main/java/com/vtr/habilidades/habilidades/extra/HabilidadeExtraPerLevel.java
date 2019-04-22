package com.vtr.habilidades.habilidades.extra;

import com.vtr.api.spigot.utils.MathUtils;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public abstract class HabilidadeExtraPerLevel extends HabilidadeExtra {

	protected int maxLevel;
	
	protected int levelBase;
	
	protected double perLevel;
	
	public HabilidadeExtraPerLevel(HabilidadeType habilidadeType, HabilidadeExtraType extraType, double perLevel, int levelBase, int maxLevel) {
		super(habilidadeType, extraType);
		this.maxLevel = maxLevel;
		this.perLevel = perLevel;
		this.levelBase = levelBase;
	}
	
	public double getChance(HabilidadeUser habilidadePlayer) {
		HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(getHabilidade().getType());
		if(habilidadeInfo != null) {
			int lvl = habilidadeInfo.getLevel();
			if(lvl > maxLevel) {
				lvl = maxLevel;
			}
			
			//TODO maybe that is wrong
			if(lvl >= levelBase) {
				return habilidadeInfo.getLevel() * perLevel;
			}
		}
		
		return 0;
	}
	
	public boolean use(HabilidadeUser habilidadePlayer) {
		return MathUtils.percentDouble(getChance(habilidadePlayer), 100);
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public double getPerLevel() {
		return perLevel;
	}
}
