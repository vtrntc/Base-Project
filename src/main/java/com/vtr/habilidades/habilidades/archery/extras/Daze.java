package com.vtr.habilidades.habilidades.archery.extras;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.misc.PotionInfo;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPercent;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Daze extends HabilidadeExtraPercent {

	private Random random;
	
	private PotionInfo potionInfo;

	public Daze(double chance, double maxChance, PotionInfo potionInfo) {
		super(HabilidadeType.ARCHERY, HabilidadeExtraType.DAZE, chance, maxChance);
		this.potionInfo = potionInfo;
		this.random = new Random();
	}

	public PotionInfo getPotionInfo() {
		return potionInfo;
	}

	public boolean activate(Event event) {
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getDamager();
			if(arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
				Player p = (Player) arrow.getShooter();
				
				HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(p.getName());
				if(use(habilidadePlayer)) {
					Player target = (Player) e.getEntity();
					
					Location dazedLocation = target.getLocation();
		        	dazedLocation.setPitch(90 - random.nextInt(181));

		        	target.teleport(dazedLocation);
		        
		        	potionInfo.apply(target);
		        	
		        	Map<String, String> replacers = new HashMap<>();
		        	replacers.put("%player%", target.getName());
		        	
					MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Daze").replace(replacers).send(p);
					
					replacers.put("%player%", p.getName());
					
					MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "DazeTarget").replace(replacers).send(target);
				}
			}
		}
		
		return false;
	}
}
