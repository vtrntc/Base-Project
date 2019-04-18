package com.vtr.habilidades.habilidades.archery;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtra;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Archery extends Habilidade {

	private ArcheryDamageExperience maxLevel;
	
	private List<ArcheryDamageExperience> experience;
	
	public Archery(String name, List<HabilidadeDrop> drops, List<Material> tools, List<HabilidadeExtra> extras, List<ArcheryDamageExperience> experience) {
		super(HabilidadeType.ARCHERY, name, drops, tools, extras);
		this.experience = experience;
		this.maxLevel = experience.get(experience.size());
	}
	
	public ArcheryDamageExperience getExperience(int distance) {
		return experience.stream().filter(e -> e.getDistance() >= distance && e.getDistance() <= distance).findFirst().orElse(null);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getDamager();
			if(arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
				Player p = (Player) arrow.getShooter();
				
				ArcheryDamageExperience experience = getExperience((int) p.getLocation().distance(e.getEntity().getLocation()));
				if(experience == null) {
					experience = maxLevel;
				}
				
				HabilidadeUser habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
				
				HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
				if(habilidadeInfo != null) {
					getHabilidadeExtra(HabilidadeExtraType.IMPACT).activate(e);
					getHabilidadeExtra(HabilidadeExtraType.DAZE).activate(e);
					
					giveXp(habilidadePlayer, habilidadeInfo, experience.getXp());
					
					sendActionBar(p, habilidadeInfo, experience.getXp());
				}
			}
		}
	}
}
