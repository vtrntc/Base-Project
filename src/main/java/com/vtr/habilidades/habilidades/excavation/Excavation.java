package com.vtr.habilidades.habilidades.excavation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.utils.MathUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtra;
import com.vtr.habilidades.objects.HabilidadeBlock;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class Excavation extends Habilidade {
	
	private Map<Material, HabilidadeBlock> diggingBlocks;
	
	public Excavation(String name, List<Material> tools, List<HabilidadeDrop> drops, List<HabilidadeExtra> extras, Map<Material, HabilidadeBlock> diggingBlocks) {
		super(HabilidadeType.EXCAVATION, name, drops, tools, extras);
		this.diggingBlocks = diggingBlocks;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		Block block = e.getBlock();
		if(block.getType() != Material.AIR) {
			if(!block.hasMetadata("playerPlaced")) {
				if(diggingBlocks.containsKey(block.getType())) {
					ItemStack item = p.getItemInHand();
					if(item != null && item.getType() != Material.AIR && isTool(item.getType())) {
						HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(p.getName());
						
						HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
						if(habilidadeInfo != null) {
							HabilidadeBlock habilidadeBlock = diggingBlocks.get(block.getType());
							
							giveXp(habilidadePlayer, habilidadeInfo, habilidadeBlock.getXp());
							
							sendActionBar(p, habilidadeInfo, habilidadeBlock.getXp());
							
							List<HabilidadeDrop> drops = getDropsForLevel(habilidadeBlock.getDrops(), habilidadeInfo.getLevel());
							if(!drops.isEmpty()) {
								for(HabilidadeDrop drop : drops) {
									double activationChance = drop.getChance() * ((habilidadeInfo.getLevel() / drop.getMinLevel()));
									if(activationChance > drop.getMaxChance()) {
										activationChance = drop.getMaxChance();
									}
									
									if(activationChance >= 100) {
										p.getWorld().dropItemNaturally(block.getLocation().clone().add(0.5, 0, 0.5), drop.getItem());
									}else if(MathUtils.percentDouble(activationChance, habilidadeInfo.getLevel())) {
										p.getWorld().dropItemNaturally(block.getLocation().clone().add(0.5, 0, 0.5), drop.getItem());
										break;
									}
								}
							}
							
							if(canLevelUP(habilidadePlayer)) {
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 2F, 3F);
								
								Map<String, String> replacers = new HashMap<>();
								replacers.put("%habilidade%", habilidadeInfo.getHabilidade().getName());
								replacers.put("%level%", Integer.toString(habilidadeInfo.getLevel()));
								
								MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "LevelUp").replace(replacers).send(p);
							}
						}
					}
				}
			}
		}
	}
}
