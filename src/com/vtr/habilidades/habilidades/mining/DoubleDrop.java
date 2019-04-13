package com.vtr.habilidades.habilidades.mining;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.api.spigot.utils.ItemUtils;
import com.vtr.api.spigot.utils.PlayerUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPerLevel;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class DoubleDrop extends HabilidadeExtraPerLevel {

	private List<Material> allowed;
	
	public DoubleDrop(double perLevel, int maxLevel, List<Material> allowed) {
		super(HabilidadeType.MINING, HabilidadeExtraType.DOUBLE_DROP, maxLevel, perLevel);
		this.allowed = allowed;
	}

	public boolean isAllowed(Material material) {
		return allowed.contains(material);
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		
		Block block = e.getBlock();
		if(block.getType() != Material.AIR) {
			if(!block.hasMetadata("playerPlaced")) {
				ItemStack item = p.getItemInHand();
				if(item != null && item.getType() != Material.AIR && habilidade.isTool(item.getType())) {
					HabilidadePlayer habilidadePlayer = HabilidadePlugin.getManager().getPlayer(p.getName());
					
					if(isAllowed(block.getType())) {
						if(use(habilidadePlayer)) {
							for(ItemStack atual : block.getDrops(item)) {
								if(atual != null && atual.getType() != Material.AIR) {
									ItemStack clone = atual.clone();
									clone.setAmount((clone.getAmount() * 2) * ItemUtils.getFortuneModifierForItem(item));
									
									PlayerUtils.addItemToInventoryOrDrop(p, clone);
								}
							}
							
							MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "DoubleDropMining").send(p);
							
							block.setType(Material.AIR);
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
