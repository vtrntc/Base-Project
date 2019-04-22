package com.vtr.habilidades.habilidades.extra;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeType;

public abstract class HabilidadeExtra implements Listener {

	protected HabilidadeExtraType extraType;
	
	private HabilidadeType habilidadeType;
	
	private Habilidade habilidade;

	public HabilidadeExtra(HabilidadeType habilidadeType, HabilidadeExtraType extraType) {
		this.extraType = extraType;
		this.habilidadeType = habilidadeType;
	}
	
	public abstract boolean activate(Event event);
	
	public HabilidadeExtraType getExtraType() {
		return extraType;
	}
	
	public Habilidade getHabilidade() {
		if(habilidade == null) {
			habilidade = HabilidadePlugin.getManager().getHabilidadeByTypeName(habilidadeType.name());
		}
		
		return habilidade;
	}
}
