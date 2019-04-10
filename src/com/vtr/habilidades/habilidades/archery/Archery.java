package com.vtr.habilidades.habilidades.archery;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.vtr.api.utils.MathUtils;
import com.vtr.api.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Archery extends Habilidade {

//	Arqueiro é a skill/habilidade que lida com "arcos/flechas". 
//	Com a skill/habilidade Arqueiro você tem a possibilidade de retirar cada vez mais vida do inimigo a cada level que passa.
//	Com level 1000 na skill você causa [​IMG] a mais de dano no adversário. 
//	Atordoamento:
//	Você tem a chance de atordoar seu inimigo, o deixando imóvel por alguns segundos, e o obrigando a olhar para cima ou para baixo.
	
	private Daze daze;
	
	private Impact impact;
	
	private SkillShot skillShot;
	
	private ArrowRetrieval arrowRetrieval;
	
	private static Random random = new Random();
	
	public Archery(String name, List<HabilidadeDrop> drops, List<Material> tools, Impact impact, Daze daze, SkillShot skillShot, ArrowRetrieval arrowRetrieval) {
		super(HabilidadeType.ARCHERY, name, drops, tools);
		this.impact = impact;
		this.daze = daze;
		this.skillShot = skillShot;
		this.arrowRetrieval = arrowRetrieval;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		Player p = null;
		if(e.getEntity() instanceof Player) {
			p = (Player) e.getEntity();
		}else if(e.getEntity() instanceof Projectile) {
			Projectile projectile = (Projectile) e.getEntity();
			if(projectile.getShooter() != null) {
				if(projectile.getShooter() instanceof Player) {
					p = (Player) projectile.getShooter();
				}
			}
		}
		
		if(p != null) {
			if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
				if(isTool(p.getItemInHand().getType())) {
					HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
					
					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
					if(habilidadeInfo != null) {
						if(impact != null) {
							double increasePercent = (habilidadeInfo.getLevel() / impact.getPerLevel()) * impact.getBonus();
							if(MathUtils.percentDouble(increasePercent, 100)) {
								e.setDamage(e.getDamage() + increasePercent);
							}
						}
						
						if(e.getEntity() instanceof Player) {
							Player target = (Player) e.getEntity();
							
							if(daze != null) {
								//TODO daze percent
								if(MathUtils.percentDouble(habilidadeInfo.getLevel(), 100)) {
									Location dazedLocation = target.getLocation();
						        	dazedLocation.setPitch(90 - random.nextInt(181));
		
						        	target.teleport(dazedLocation);
						        
						        	daze.getPotionInfo().apply(target);
								}
							}
							
							if(arrowRetrieval != null) {
								double chance = arrowRetrieval.getChance() + (habilidadeInfo.getLevel() * arrowRetrieval.getChance());
								if(chance > arrowRetrieval.getMaxIncrease()) {
									chance = arrowRetrieval.getMaxIncrease();
								}
								
								if(MathUtils.percentDouble(chance, 100)) {
									PlayerUtils.addItemToInventoryOrDrop(p, new ItemStack(Material.ARROW));
								}
							}
						}
					}
				}
			}
		}
	}
}
