package com.vtr.habilidades.habilidades.unarmed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.vtr.api.message.MessageUtils;
import com.vtr.api.utils.MathUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Unarmed extends Habilidade {

	private Disarmor disarmor;
	
	private Map<EntityType, Double> entitiesXp;
	
	public Unarmed(String name, List<HabilidadeDrop> drops, List<Material> tools, Map<EntityType, Double> entitiesXp, Disarmor disarmor) {
		super(HabilidadeType.UNARMED, name, drops, tools);
		this.entitiesXp = entitiesXp;
		this.disarmor = disarmor;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			
			if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
				Entity entity = e.getEntity();
				if(entitiesXp.containsKey(entity.getType())) {
					HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
					
					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
					if(habilidadeInfo != null) {
						if(entity.getType() == EntityType.PLAYER) {
							if(disarmor != null) {
								double chance = disarmor.getChance() + (habilidadeInfo.getLevel() * disarmor.getChance());
								if(chance > disarmor.getMaxIncrease()) {
									chance = disarmor.getMaxIncrease();
								}
								
								if(MathUtils.percentDouble(chance, 100)) {
									Player target = (Player) entity;
									
									ItemStack item = target.getItemInHand();
									if(item != null && item.getType() != Material.AIR) {
										target.getWorld().dropItem(target.getLocation(), item);
										target.setItemInHand(new ItemStack(Material.AIR));
										
										Map<String, String> replacers = new HashMap<>();
										replacers.put("%player%", target.getName());
										
										MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "TargetDisarmed").replace(replacers).send(p);
										
										replacers.put("%player%", p.getName());
										
										MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Disarmed").replace(replacers).send(p);
									}
								}
							}
						}
						
						double xp = entitiesXp.get(entity.getType());
						
						giveXp(habilidadePlayer, habilidadeInfo, xp);
						sendActionBar(p, habilidadeInfo, xp);
					}
				}
			}
		}
	}
}
