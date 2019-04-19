package com.vtr.habilidades.habilidades.acrobatics.extras;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPercent;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Roll extends HabilidadeExtraPercent {

	public Roll(double chance, double maxChance) {
		super(HabilidadeType.ACROBATICS, HabilidadeExtraType.ROLL, chance, maxChance);
	}

	public boolean activate(Event event) {
		EntityDamageEvent e = (EntityDamageEvent) event;
		if(e.getCause() == DamageCause.FALL) {
			if(e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				
				HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(p.getName());
				
				if(use(habilidadePlayer)) {
					double damage = e.getDamage() / 2;
					if(damage > 0) {
						e.setDamage(damage);
					}else{
						e.setCancelled(true);
					}
					
					MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Roll").send(p);
					return true;
				}
			}
		}
		
		return false;
	}
}
