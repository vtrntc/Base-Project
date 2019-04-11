package com.vtr.habilidades.habilidades.swords.extras;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.api.message.MessageUtils;
import com.vtr.api.utils.MathUtils;
import com.vtr.api.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.HabilidadeExtra;
import com.vtr.habilidades.habilidades.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Dodge extends HabilidadeExtra {

	private int maxLevel;
	
	public Dodge(int maxLevel) {
		super(HabilidadeType.SWORDS, HabilidadeExtraType.DODGE);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		Player p = PlayerUtils.getPlayerDamagerFromEntityDamageByEntityEvent(e);
		if(p != null) {
			if(e.getEntity() instanceof Player) {
				Player target = (Player) e.getEntity();
				
				HabilidadePlayer targetPlayer = HabilidadePlugin.getManager().getPlayer(target.getName());
				
				HabilidadeInfo targetInfo = targetPlayer.getHabilidade(habilidade.getType());
				if(targetInfo != null) {
					int level = targetInfo.getLevel();
					
					if(MathUtils.percentDouble((level > maxLevel ? maxLevel : level) * 0.1, 100)) {
						MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Dodge").send(target);
						e.setCancelled(true);
					}
				}
			}
		}
	}
}
