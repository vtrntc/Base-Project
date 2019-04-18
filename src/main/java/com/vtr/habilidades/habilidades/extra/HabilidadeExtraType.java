package com.vtr.habilidades.habilidades.extra;

public enum HabilidadeExtraType {

	//AXES
	TREE_CUT, 
	
	//ACROBATICS
	ROLL,
	PERFECT_ROLL,
	DODGE, 
	
	//ARCHERY
	IMPACT,
	DAZE,
	
	//MINING
	DOUBLE_DROP,
	
	//SWORDS
	COUNTER_ATTACK, 
	BLEED;
	
	public static HabilidadeExtraType getType(String type) {
		for(HabilidadeExtraType habilidadeType : values()) {
			if(habilidadeType.name().equalsIgnoreCase(type)) {
				return habilidadeType;
			}
		}
		
		return null;
	}
}
