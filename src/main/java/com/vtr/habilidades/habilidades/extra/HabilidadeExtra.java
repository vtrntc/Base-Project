package com.vtr.habilidades.habilidades.extra;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import com.vtr.api.spigot.APISpigot;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeType;

public abstract class HabilidadeExtra implements Listener {

	protected HabilidadeExtraType extraType;
	
	protected Habilidade habilidade;

	public HabilidadeExtra(HabilidadeType habilidadeType, HabilidadeExtraType extraType) {
		this.extraType = extraType;
		this.habilidade = HabilidadePlugin.getManager().getHabilidadeByTypeName(habilidadeType.name());
	}
	
	public abstract boolean activate(Event event);
	
	public HabilidadeExtraType getExtraType() {
		return extraType;
	}
}
