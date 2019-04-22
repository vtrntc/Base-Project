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
		System.out.println("get chance1 " + habilidadePlayer);
		HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(habilidade.getType());
		System.out.println("get chance2");
		if(habilidadeInfo != null) {
			System.out.println("get chance3");
			double playerChance = habilidadeInfo.getLevel() * chance;
			if(playerChance > maxChance) {
				System.out.println("get chance4");
				playerChance = maxChance;
			}
			
			System.out.println("get chance5");
			return playerChance;
		}
		
		System.out.println("get chance6");
		return 0;
	}
}
