package com.vtr.habilidades.habilidades.extra;

import com.vtr.api.spigot.utils.MathUtils;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public abstract class HabilidadeExtraPercent extends HabilidadeExtra {

	protected double chance;
	
	protected double maxChance;
	
	public HabilidadeExtraPercent(HabilidadeType habilidadeType, HabilidadeExtraType extraType, double chance, double maxChance) {
		super(habilidadeType, extraType);
		this.chance = chance;
		this.maxChance = maxChance;
	}
	
	public double getChance() {
		return chance;
	}
	
	public double getMaxChance() {
		return maxChance;
	}
	
	public boolean use(HabilidadeUser habilidadePlayer) {
		return MathUtils.percentDouble(getChance(habilidadePlayer), 100);
	}
	
	public double getChance(HabilidadeUser habilidadePlayer) {
		HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(habilidade.getType());
		if(habilidadeInfo != null) {
			double playerChance = habilidadeInfo.getLevel() * chance;
			if(playerChance > maxChance) {
				playerChance = maxChance;
			}
			
			return playerChance;
		}
		
		return 0;
	}
}
