package com.vtr.habilidades.habilidades.swords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.vtr.api.utils.MathUtils;
import com.vtr.api.utils.PlayerUtils;
import com.vtr.api.utils.StringUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Swords extends Habilidade {

	private int maxDodgeLevel;
	
	private int maxCounterLevel;
	
	private List<Bleed> bleeds;
	
	private Map<EntityType, Double> entitiesXp;
	
	private Map<Player, BleedInfo> playersBleeding;
	
	public Swords(String name, List<HabilidadeDrop> drops, List<Material> tools, int maxDodgeLevel, int maxCounterLevel, List<Bleed> bleeds, Map<EntityType, Double> entitiesXp) {
		super(HabilidadeType.SWORDS, name, drops, tools);
		this.maxDodgeLevel = maxDodgeLevel;
		this.maxCounterLevel = maxCounterLevel;
		this.bleeds = bleeds;
		this.entitiesXp = entitiesXp;
		this.playersBleeding = new HashMap<>();
		this.bleeds.sort((b1, b2) -> {
			return Integer.compare(b2.getMinLevel(), b1.getMinLevel());
		});
	}
	
	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if(playersBleeding.containsKey(p)) {
			BleedInfo bleedInfo = playersBleeding.get(p);
			bleedInfo.stop();
		}
	}
	
	@EventHandler
	private void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		
		if(playersBleeding.containsKey(p)) {
			BleedInfo bleedInfo = playersBleeding.get(p);
			bleedInfo.stop();
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		Player p = PlayerUtils.getPlayerDamagerFromEntityDamageByEntityEvent(e);
		if(p != null) {
			HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
			
			HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
			if(e.getEntity() instanceof Player) {
				Player target = (Player) e.getEntity();
				
				HabilidadePlayer targetPlayer = HabilidadePlugin.getManager().getPlayer(target.getName());
				
				HabilidadeInfo targetInfo = targetPlayer.getHabilidade(type);
				if(targetInfo != null) {
					int level = targetInfo.getLevel();
					
					if(MathUtils.percentDouble((level > maxDodgeLevel ? maxDodgeLevel : level) * 0.1, 100)) {
						StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Dodge").send(target);
						e.setCancelled(true);
					}else if(MathUtils.percentDouble((level > maxCounterLevel ? maxCounterLevel : level) * 0.05, 100)) {
						p.damage(e.getDamage() / 2);
						
						StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "CounterAttacked").replace("%player%", target.getName()).send(p);
						StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "CounterAttack").replace("%player%", target.getName()).send(p);
						
					}
				}
				
				if(habilidadeInfo != null) {
					int level = habilidadeInfo.getLevel();
					if(MathUtils.percentDouble(level * 0.1, 100)) {
						Bleed bleed = getBleed(level);
						if(bleed != null) {	
							if(!e.isCancelled()) {
								if(!playersBleeding.containsKey(target)) {
									BleedInfo bleedInfo = new BleedInfo(target, bleed);
									bleedInfo.start();
									
									playersBleeding.put(target, bleedInfo);
									
									StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Bleeding").replace("%player%", p.getName()).send(target);
									StringUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Bleed").replace("%player%", target.getName()).send(p);
								}
							}
						}
					}
				}
			}
				
				
			if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
				if(isTool(p.getItemInHand().getType())) {
					if(habilidadeInfo != null) {
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
	
	private Bleed getBleed(int level) {
		Bleed last = bleeds.get(bleeds.size() - 1);
		if(last != null && level > last.getMinLevel()) {
			return last;
		}
		
		return bleeds.stream().filter(fe -> level >= fe.getMinLevel() && level <= fe.getMinLevel()).findFirst().orElse(null);
	}
	
	private class BleedInfo {
		
		private Player player;

		private Bleed bleed;
		
		private BukkitTask bukkitTask;

		public BleedInfo(Player player, Bleed bleed) {
			this.player = player;
			this.bleed = bleed;
		}
		
		public void stop() {
			if(bukkitTask != null) {
				bukkitTask.cancel();
			}
			
			playersBleeding.remove(player);
		}
		
		public void start() {
			this.bukkitTask = new BukkitRunnable() {
				int amountDone = 0;
				public void run() {
					if(amountDone >= bleed.getAmount()) {
						stop();
					}
					
					player.getLocation().getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
					
					player.damage(bleed.getDamage());
					
					amountDone++;
				}
			}.runTaskTimer(HabilidadePlugin.getInstance(), 0, bleed.getTime() * 20);
		}
	}
}
