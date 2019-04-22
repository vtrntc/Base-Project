package com.vtr.habilidades.habilidades.axes.extras;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.vtr.api.spigot.message.MessageUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraPercent;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;
import com.vtr.habilidades.user.HabilidadeUser;

public class TreeCut extends HabilidadeExtraPercent {

	private List<Material> wood = Arrays.asList(Material.LOG, Material.LOG_2);
	
	public TreeCut(double chance, double maxChance) {
		super(HabilidadeType.AXES, HabilidadeExtraType.TREE_CUT, chance, maxChance);
	}
	
	private boolean isWood(Material material) {
		return wood.contains(material);
	}
	
	public boolean activate(Event event) {
		BlockBreakEvent e = (BlockBreakEvent) event;
		
		Player p = e.getPlayer();
		
		ItemStack item = p.getItemInHand();
		if(item != null) {
			if(getHabilidade().isTool(item.getType())) {
				Block block = e.getBlock();
				if(isWood(block.getType())) {
					HabilidadeUser habilidadePlayer = HabilidadePlugin.getModuleFactory().getUserModule(p.getName());
					
					HabilidadeInfo habilidadeInfo = habilidadePlayer.getHabilidade(getHabilidade().getType());
					if(habilidadeInfo != null) {
						if(use(habilidadePlayer)) {
							new BukkitRunnable() {
								Block atual = e.getBlock().getRelative(BlockFace.UP);
								public void run() {
									if(!isWood(atual.getType())) {
										cancel();
									}else{
										atual.breakNaturally();
										atual = atual.getRelative(BlockFace.UP);
									}
								}
							}.runTaskTimer(HabilidadePlugin.getInstance(), 0, 1L);
							
							MessageUtils.getMessage(HabilidadePlugin.getYamlConfig(), "TreeCut").send(p);
						}
					}
				}
			}
		}
		
		return false;
	}
}
