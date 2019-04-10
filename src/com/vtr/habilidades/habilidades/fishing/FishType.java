package com.vtr.habilidades.habilidades.fishing;

public enum FishType {

	FISH, SALMON, CLOWNFISH, PUFFERFISH;
	
	public static FishType getFish(String fish) {
		for(FishType atual : values()) {
			if(atual.name().equalsIgnoreCase(fish)) {
				return atual;
			}
		}
		
		return null;
	}
}
