package com.vtr.habilidades.habilidades.swords;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.api.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Swords extends Habilidade {

	private Map<EntityType, Double> entitiesXp;
	
	public Swords(String name, List<HabilidadeDrop> drops, List<Material> tools, Map<EntityType, Double> entitiesXp) {
		super(HabilidadeType.SWORDS, name, drops, tools);
		this.entitiesXp = entitiesXp;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		Player p = PlayerUtils.getPlayerDamagerFromEntityDamageByEntityEvent(e);
		if(p != null) {
			if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
				if(isTool(p.getItemInHand().getType())) {
					HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
					
					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
					if(habilidadeInfo != null) {
						if(entitiesXp.containsKey(e.getEntity().getType())) {
							double xp = entitiesXp.get(e.getEntity().getType());
							
							giveXp(habilidadePlayer, habilidadeInfo, xp);
							
							sendActionBar(p, habilidadeInfo, xp);
						}
					}
				}
			}
		}
	}
}
