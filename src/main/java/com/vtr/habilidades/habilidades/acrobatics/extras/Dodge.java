package com.vtr.habilidades.habilidades.acrobatics.extras;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPercent;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Dodge extends HabilidadeExtraPercent {

	public Dodge(double chance, double maxChance) {
		super(HabilidadeType.ACROBATICS, HabilidadeExtraType.DODGE, chance, maxChance);
	}

	public boolean activate(Event event) {
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		
		Player p = PlayerUtils.getPlayerDamagerFromEntityDamageByEntityEvent(e);
		if(p != null) {
			if(e.getEntity() instanceof Player) {
				Player target = (Player) e.getEntity();
				
				HabilidadeUser targetPlayer = HabilidadePlugin.getManager().getPlayer(target.getName());
				if(use(targetPlayer)) {
					e.setDamage(e.getDamage() / 2);
					
					Map<String, String> replacers = new HashMap<>();
					replacers.put("%player%", p.getName());
					
					MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Dodge").replace(replacers).send(target);
					return true;
				}
			}
		}
		
		return false;
	}
}
