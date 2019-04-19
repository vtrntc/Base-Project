package com.vtr.habilidades.habilidades.swords.extras.bleed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPercent;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Bleed extends HabilidadeExtraPercent {

	private List<BleedLevel> bleeds;
	
	private Map<Player, BleedInfo> playersBleeding;
	
	public Bleed(double perLevel, double maxChance, List<BleedLevel> bleeds) {
		super(HabilidadeType.SWORDS, HabilidadeExtraType.BLEED, perLevel, maxChance);
		this.playersBleeding = new HashMap<>();
		this.bleeds = bleeds;
		this.bleeds.sort((b1, b2) -> {
			return Integer.compare(b2.getMinLevel(), b1.getMinLevel());
		});
	}
	
	private BleedLevel getBleed(int level) {
		BleedLevel last = bleeds.get(bleeds.size() - 1);
		if(last != null && level > last.getMinLevel()) {
			return last;
		}
		
		return bleeds.stream().filter(fe -> level >= fe.getMinLevel() && level <= fe.getMinLevel()).findFirst().orElse(null);
	}
	
	public boolean activate(Event event) {
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		if(e.getEntity() instanceof Player) {
			Player p = PlayerUtils.getPlayerDamagerFromEntityDamageByEntityEvent(e);
			if(p != null) {
				HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(p.getName());
				
				HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(habilidade.getType());
				if(habilidadeInfo != null) {
					if(use(habilidadePlayer)) {
						BleedLevel bleed = getBleed(habilidadeInfo.getLevel());
						if(bleed != null) {	
							Player target = (Player) e.getEntity();
							
							if(!playersBleeding.containsKey(target)) {
								BleedInfo bleedInfo = new BleedInfo(target, bleed);
								bleedInfo.start();
								
								playersBleeding.put(target, bleedInfo);
								
								Map<String, String> replacers = new HashMap<>();
								replacers.put("%player%", p.getName());
								replacers.put("%time%", Integer.toString(bleed.getTime()));
								
								MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Bleeding").replace(replacers).send(target);
								
								replacers.put("%player%", target.getName());
								
								MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "Bleed").replace(replacers).send(p);
								return true;
							}
						}
					}
				}
			}
		} 
		
		return false;
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

	private class BleedInfo {
		
		private Player player;

		private BleedLevel bleed;
		
		private BukkitTask bukkitTask;

		public BleedInfo(Player player, BleedLevel bleed) {
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
