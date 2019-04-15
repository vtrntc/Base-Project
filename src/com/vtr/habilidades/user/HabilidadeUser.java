package com.vtr.habilidades.user;

import java.util.HashMap;
import java.util.Map;

import com.vtr.api.shared.user.module.UserModule;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.acrobatics.AcrobaticsInfo;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;

public class HabilidadeUser extends UserModule {

	private String player;
	
	private boolean needUpdate;
	
	private Map<HabilidadeType, HabilidadeInfo> habilidades;

	public HabilidadeUser(String player, Map<HabilidadeType, HabilidadeInfo> habilidades) {
		this.player = player;
		this.habilidades = habilidades;
		
		for(Habilidade habilidade : HabilidadePlugin.getManager().getHabilidades()) {
			if(!habilidades.containsKey(habilidade.getType())) {
				switch(habilidade.getType()) {
					case ACROBATICS:
						habilidades.put(habilidade.getType(), new AcrobaticsInfo(player, habilidade, 0, 0));
						break;
					default:
						habilidades.put(habilidade.getType(), new HabilidadeInfo(player, habilidade, 0, 0));
						break;
				}
			}
		}
	}
	
	public HabilidadeUser(String player) {
		this(player, new HashMap<>());
	}
	
	public String getPlayer() {
		return player;
	}
	
	public Map<HabilidadeType, HabilidadeInfo> getHabilidades() {
		return habilidades;
	}
	
	public HabilidadeInfo getHabilidade(HabilidadeType type) {
		return habilidades.get(type);
	}
	
	public boolean isNeedUpdate() {
		return needUpdate;
	}
	
	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}
}
