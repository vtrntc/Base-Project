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
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Archery extends Habilidade {

//	Arqueiro é a skill/habilidade que lida com "arcos/flechas". 
//	Com a skill/habilidade Arqueiro você tem a possibilidade de retirar cada vez mais vida do inimigo a cada level que passa.
//	Com level 1000 na skill você causa [​IMG] a mais de dano no adversário. 
//	Atordoamento:
//	Você tem a chance de atordoar seu inimigo, o deixando imóvel por alguns segundos, e o obrigando a olhar para cima ou para baixo.
	
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
					giveXp(habilidadePlayer, habilidadeInfo, experience.getXp());
					
					sendActionBar(p, habilidadeInfo, experience.getXp());
				}
			}
		}
	}
	
	
//		Player p = null;
//		if(e.getEntity() instanceof Player) {
//			p = (Player) e.getEntity();
//		}else if(e.getEntity() instanceof Projectile) {
//			Projectile projectile = (Projectile) e.getEntity();
//			if(projectile.getShooter() != null) {
//				if(projectile.getShooter() instanceof Player) {
//					p = (Player) projectile.getShooter();
//				}
//			}
//		}
//		
//		if(p != null) {
//			if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
//				if(isTool(p.getItemInHand().getType())) {
//					HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
//					
//					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
//					if(habilidadeInfo != null) {
//						if(impact != null) {
//							double increasePercent = (habilidadeInfo.getLevel() / impact.getPerLevel()) * impact.getBonus();
//							if(MathUtils.percentDouble(increasePercent, 100)) {
//								e.setDamage(e.getDamage() + increasePercent);
//							}
//						}
//						
//						if(e.getEntity() instanceof Player) {
//							Player target = (Player) e.getEntity();
//							
//							if(daze != null) {
//								//TODO daze percent
//								if(MathUtils.percentDouble(habilidadeInfo.getLevel(), 100)) {
//									Location dazedLocation = target.getLocation();
//						        	dazedLocation.setPitch(90 - random.nextInt(181));
//		
//						        	target.teleport(dazedLocation);
//						        
//						        	daze.getPotionInfo().apply(target);
//								}
//							}
//							
//							if(arrowRetrieval != null) {
//								double chance = arrowRetrieval.getChance() + (habilidadeInfo.getLevel() * arrowRetrieval.getChance());
//								if(chance > arrowRetrieval.getMaxIncrease()) {
//									chance = arrowRetrieval.getMaxIncrease();
//								}
//								
//								if(MathUtils.percentDouble(chance, 100)) {
//									PlayerUtils.addItemToInventoryOrDrop(p, new ItemStack(Material.ARROW));
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}
}
