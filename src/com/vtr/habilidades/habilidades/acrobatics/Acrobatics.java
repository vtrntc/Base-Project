package com.vtr.habilidades.habilidades.acrobatics;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.vtr.api.message.MessageUtils;
import com.vtr.api.utils.LocationUtils;
import com.vtr.api.utils.MathUtils;
import com.vtr.api.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Acrobatics extends Habilidade {

	private List<AcrobaticsFallExperience> fallExperience;
	
	public Acrobatics(String name, List<HabilidadeDrop> drops, List<Material> tools, List<AcrobaticsFallExperience> fallExperience) {
		super(HabilidadeType.ACROBATICS, name, drops, tools);
		this.fallExperience = fallExperience;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player damager = PlayerUtils.getPlayerDamagerFromEntityDamageByEntityEvent(e);
			if(damager != null) {
				Player p = (Player) e.getEntity();
				
				HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
				
				HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
				if(habilidadeInfo != null) {
					if(MathUtils.percentDouble(habilidadeInfo.getLevel() * 0.025, 100)) {
						MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Dodge").send(p);
						e.setDamage(e.getDamage() / 2);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageEvent e) {
		if(e.getCause() == DamageCause.FALL) {
			if(e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				
				HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
				
				HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
				if(habilidadeInfo != null) {
					AcrobaticsInfo acrobaticsInfo = (AcrobaticsInfo) habilidadeInfo;
					
					if(!exploitPrevention(p, acrobaticsInfo)) {
						boolean roll = false;
						if(p.isSneaking()) {
							if(MathUtils.percentDouble(habilidadeInfo.getLevel() * 0.2, 100)) {
								roll = true;
								
								MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "PerfectRoll").send(p);
								e.setCancelled(true);
							}
						}else if(MathUtils.percentDouble(habilidadeInfo.getLevel() * 0.1, 100)) {
							roll = true;
							
							MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Roll").send(p);
							e.setCancelled(true);
							
						}
						
						if(!roll) {
							int distance = (int) e.getDamage() + 3;
							
							AcrobaticsFallExperience experience = getExperience(distance);
							if(experience != null) {
								giveXp(habilidadePlayer, habilidadeInfo, experience.getXp());
								
								sendActionBar(p, habilidadeInfo, experience.getXp());
							}
						}
					}
				}
			}
		}
	}
	
	private AcrobaticsFallExperience getExperience(int distance) {
		return fallExperience.stream().filter(fe -> distance >= fe.getDistance() && distance <= fe.getDistance()).findFirst().orElse(null);
	}
	
    public boolean exploitPrevention(Player player, AcrobaticsInfo acrobaticsInfo) {
    	if(player.getItemInHand().getType() == Material.ENDER_PEARL || player.isInsideVehicle()) {
            return true;
        }

        Location fallLocation = player.getLocation();
        
        int maxTries = 10;

        boolean sameLocation = (acrobaticsInfo.getLastFallDamage() != null && LocationUtils.isNear(acrobaticsInfo.getLastFallDamage(), fallLocation, 2));

        int fallTries = acrobaticsInfo.getFallTries();
        
        acrobaticsInfo.setFallTries(sameLocation ? Math.min(fallTries + 1, maxTries) : Math.max(fallTries - 1, 0));
       
        acrobaticsInfo.setLastFallDamage(fallLocation);

        return fallTries + 1 > maxTries;
    }
}
