package com.vtr.habilidades.objects;

public enum HabilidadeType {

	MINING, HERBALISM, FISHING, EXCAVATION, UNARMED, ARCHERY, SWORDS, AXES, ACROBATICS;
	
	public static HabilidadeType getType(String type) {
		for(HabilidadeType habilidadeType : values()) {
			if(habilidadeType.name().equalsIgnoreCase(type)) {
				return habilidadeType;
			}
		}
		
		return null;
	}
}
