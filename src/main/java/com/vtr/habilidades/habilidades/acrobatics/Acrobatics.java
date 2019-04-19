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

import com.vtr.api.spigot.utils.LocationUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtra;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Acrobatics extends Habilidade {

	private List<AcrobaticsFallExperience> fallExperience;
	
	public Acrobatics(String name, List<HabilidadeDrop> drops, List<Material> tools, List<HabilidadeExtra> extras, List<AcrobaticsFallExperience> fallExperience) {
		super(HabilidadeType.ACROBATICS, name, drops, tools, extras);
		this.fallExperience = fallExperience;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		getHabilidadeExtra(HabilidadeExtraType.DODGE).activate(e);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageEvent e) {
		if(e.getCause() == DamageCause.FALL) {
			if(e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				
				HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(p.getName());
				
				HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
				if(habilidadeInfo != null) {
					AcrobaticsInfo acrobaticsInfo = (AcrobaticsInfo) habilidadeInfo;
					
					if(!exploitPrevention(p, acrobaticsInfo)) {
						boolean extra = false;
						if(p.isSneaking()) {
							extra = getHabilidadeExtra(HabilidadeExtraType.PERFECT_ROLL).activate(e);
						}else{
							extra = getHabilidadeExtra(HabilidadeExtraType.ROLL).activate(e);
						}
						
						if(!extra) {
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
