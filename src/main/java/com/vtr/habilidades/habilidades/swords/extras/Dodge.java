package com.vtr.habilidades.habilidades.swords.extras;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPercent;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Dodge extends HabilidadeExtraPercent {

	public Dodge(double chance, double maxChance) {
		super(HabilidadeType.SWORDS, HabilidadeExtraType.DODGE, chance, maxChance);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
	}

	public boolean activate(Event event) {
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		Player p = PlayerUtils.getPlayerDamagerFromEntityDamageByEntityEvent(e);
		if(p != null) {
			if(e.getEntity() instanceof Player) {
				Player target = (Player) e.getEntity();
				
				HabilidadeUser targetPlayer = HabilidadePlugin.getManager().getPlayer(target.getName());
				
				HabilidadeInfo targetInfo = targetPlayer.getHabilidade(habilidade.getType());
				if(targetInfo != null) {
					if(use(targetPlayer)) {
						MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Dodge").send(target);
						e.setCancelled(true);
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
