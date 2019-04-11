package com.vtr.habilidades.habilidades.axes;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.api.message.MessageUtils;
import com.vtr.api.utils.MathUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Axes extends Habilidade {

	private int maxLevel;
	
	private int perLevel;
	
	private double damage;
	
	private Map<EntityType, Double> entitiesXp;
	
	public Axes(String name, List<HabilidadeDrop> drops, List<Material> tools, int maxLevel, int perLevel, double damage, Map<EntityType, Double> entitiesXp) {
		super(HabilidadeType.AXES, name, drops, tools);
		this.entitiesXp = entitiesXp;
		this.maxLevel = maxLevel;
		this.perLevel = perLevel;
		this.damage = damage;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled= true)
	private void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
				if(isTool(p.getItemInHand().getType())) {
					HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
					
					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
					if(habilidadeInfo != null) {
						if(e.getEntity() instanceof Player) {
							Player target = (Player) e.getEntity();
							if(MathUtils.percentDouble((habilidadeInfo.getLevel() > maxLevel ? maxLevel : habilidadeInfo.getLevel()) * 0.1, 100)) {
								target.damage(e.getDamage() / 4);
								
								MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "CriticalHit").replace("%player%", target.getName()).send(p);
								MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "TargetCriticalHit").replace("%player%", p.getName()).send(target);
							}
							
							e.setDamage(e.getDamage() + ((habilidadeInfo.getLevel() / perLevel) * damage));
						}
						
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
