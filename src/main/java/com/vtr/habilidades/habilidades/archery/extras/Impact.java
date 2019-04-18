package com.vtr.habilidades.habilidades.archery.extras;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPerLevel;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Impact extends HabilidadeExtraPerLevel {

	private double damage;
	
	public Impact(double perLevel, int levelBase, int maxLevel, double damage) {
		super(HabilidadeType.ARCHERY, HabilidadeExtraType.IMPACT, perLevel, levelBase, maxLevel);
		this.damage = damage;
	}

	public double getDamage() {
		return damage;
	}
	
	public boolean activate(Event event) {
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		if(e.getEntity() instanceof Projectile) {
			Projectile projectile = (Projectile) e.getEntity();
			if(projectile.getShooter() != null) {
				if(projectile.getShooter() instanceof Player) {
					Player p = (Player) projectile.getShooter();
					
					HabilidadeUser habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
					
					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(habilidade.getType());
					if(habilidadeInfo != null) {
						e.setDamage(e.getDamage() + (damage * perLevel));
					}
				}
			}
		}
		
		return false;
	}
}
