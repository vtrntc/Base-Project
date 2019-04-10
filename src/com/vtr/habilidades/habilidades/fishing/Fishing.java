package com.vtr.habilidades.habilidades.fishing;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.vtr.api.utils.MathUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Fishing extends Habilidade {
	
	private Map<FishType, Double> fishs;

	public Fishing(String name, List<HabilidadeDrop> drops, List<Material> tools, Map<FishType, Double> fishs) {
		super(HabilidadeType.FISHING, name, drops, tools);
		this.fishs = fishs;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onFish(PlayerFishEvent e) {
		Player p = e.getPlayer();
		
		HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
		
		
		HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
		if(habilidadeInfo != null) {
			boolean dropItems = false;
			
			switch(e.getState()) {
				case FAILED_ATTEMPT:
					dropItems = true;
					break;
				case CAUGHT_FISH:
					Item item = ((Item) e.getCaught());
					if(item != null) {
						ItemStack itemStack = item.getItemStack();
						if(itemStack != null && itemStack.getType() != Material.AIR) {
							if(habilidadeInfo != null) {
								FishType type = FishType.FISH;
								switch(itemStack.getData().getData()) {
									case 1:
										type = FishType.SALMON;
										break;
									case 2:
										type = FishType.CLOWNFISH;
										break;
									case 3:
										type = FishType.PUFFERFISH;
										break;
									default:
										break;
								}
								
								dropItems = true;
								
								if(fishs.containsKey(type)) {
									double xp = fishs.get(type);
									
									giveXp(habilidadePlayer, habilidadeInfo, xp);
									sendActionBar(p, habilidadeInfo, xp);
								}
							}
						}
					}
					
					break;
				default:
					break;
			}
			
			if(dropItems) {
				List<HabilidadeDrop> drops = getDropsForLevel(habilidadeInfo.getLevel());
				if(!drops.isEmpty()) {
					for(HabilidadeDrop drop : drops) {
						double activationChance = drop.getMinLevel() == 0 ? drop.getChance() : drop.getChance() * ((habilidadeInfo.getLevel() / drop.getMinLevel()));
						
						if(activationChance >= 100) {
							p.getWorld().dropItemNaturally(p.getEyeLocation(), drop.getItem());
						}else if(MathUtils.percentDouble(activationChance, habilidadeInfo.getLevel())) {
							p.getWorld().dropItemNaturally(p.getEyeLocation(), drop.getItem());
							break;
						}
					}
				}
			}
		}
	}
}
