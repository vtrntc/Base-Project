package com.vtr.habilidades.habilidades.archery;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPerLevel;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Impact extends HabilidadeExtraPerLevel {

	private double damage;
	
	public Impact(int maxLevel, double perLevel, double damage) {
		super(HabilidadeType.ARCHERY, HabilidadeExtraType.IMPACT, maxLevel, perLevel);
		this.damage = damage;
	}

	public double getDamage() {
		return damage;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Projectile) {
			Projectile projectile = (Projectile) e.getEntity();
			if(projectile.getShooter() != null) {
				if(projectile.getShooter() instanceof Player) {
					Player p = (Player) projectile.getShooter();
					
					HabilidadeUser habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
					
					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(habilidade.getType());
					if(habilidadeInfo != null) {
						
					}
				}
			}
		}
	}
}
