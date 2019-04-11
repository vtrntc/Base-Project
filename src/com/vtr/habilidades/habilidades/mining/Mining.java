package com.vtr.habilidades.habilidades.mining;

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

import com.vtr.api.message.MessageUtils;
import com.vtr.api.utils.MathUtils;
import com.vtr.api.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.objects.HabilidadeBlock;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class Mining extends Habilidade {
	
	private DoubleDrop doubleDrop;
	
	private Map<Material, HabilidadeBlock> miningBlocks;
	
	public Mining(String name, List<Material> tools, List<HabilidadeDrop> drops, Map<Material, HabilidadeBlock> miningBlocks, DoubleDrop doubleDrop) {
		super(HabilidadeType.MINING, name, drops, tools);
		this.miningBlocks = miningBlocks;
		this.doubleDrop = doubleDrop;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		Block block = e.getBlock();
		if(block.getType() != Material.AIR) {
			if(!block.hasMetadata("playerPlaced")) {
				if(miningBlocks.containsKey(block.getType())) {
					ItemStack item = p.getItemInHand();
					if(item != null && item.getType() != Material.AIR && isTool(item.getType())) {
						HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
						
						HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(type);
						if(habilidadeInfo != null) {
							HabilidadeBlock habilidadeBlock = miningBlocks.get(block.getType());
							
							giveXp(habilidadePlayer, habilidadeInfo, habilidadeBlock.getXp());
							
							sendActionBar(p, habilidadeInfo, habilidadeBlock.getXp());
							
							HabilidadeDrop drop = dropsRandomChooser.choose();
							if(drop != null) {
								PlayerUtils.addItemToInventoryOrDrop(p, drop.getItem());
							}
							
							if(doubleDrop.isAllowed(block.getType())) {
								if(MathUtils.percentDouble(habilidadeInfo.getLevel() * doubleDrop.getChance(), 100)) {
									for(ItemStack atual : block.getDrops(p.getItemInHand())) {
										if(atual != null && atual.getType() != Material.AIR) {
											ItemStack clone = atual.clone();
											clone.setAmount(clone.getAmount() * 2);
											
											PlayerUtils.addItemToInventoryOrDrop(p, clone);
										}
									}
									
									MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "DoubleDropMining").send(p);
									
									block.setType(Material.AIR);
									e.setCancelled(true);
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
